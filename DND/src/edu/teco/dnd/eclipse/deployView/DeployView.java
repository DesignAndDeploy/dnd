package edu.teco.dnd.eclipse.deployView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Deploy;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.eclipse.EclipseUtil;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.server.DistributionCreator;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ModuleManagerListener;
import edu.teco.dnd.server.NoBlocksException;
import edu.teco.dnd.server.NoModulesException;
import edu.teco.dnd.server.ServerManager;
import edu.teco.dnd.server.ServerState;
import edu.teco.dnd.server.ServerStateListener;
import edu.teco.dnd.util.Dependencies;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.StringUtil;

/**
 * This class gives the user access to all functionality needed to deploy an application. The user can load an existing
 * data flow graph, rename its function blocks and constrain them to specific modules and / or locations. The user can
 * also create a distribution and deploy the function blocks on the modules.
 * 
 */
public class DeployView extends EditorPart implements ServerStateListener, ModuleManagerListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DeployView.class);

	private ServerManager<?> serverManager;
	private ModuleManager manager;

	private ArrayList<UUID> idList = new ArrayList<UUID>();

	private Collection<FunctionBlockModel> functionBlocks;

	private Map<FunctionBlockModel, BlockTarget> mapBlockToTarget;

	private Resource resource;
	private DeployViewGraphics graphicsManager;

	private boolean newConstraints;

	private boolean widgetsInitialized = false;
	private int selectedIndex; // Index of selected field of moduleCombo
								// = index in idList + 1
	private UUID selectedID;
	private FunctionBlockModel selectedBlockModel;
	private Map<FunctionBlockModel, UUID> moduleConstraints = new HashMap<FunctionBlockModel, UUID>();
	private Map<FunctionBlockModel, String> locationConstraints = new HashMap<FunctionBlockModel, String>();

	private URL[] classPath = new URL[0];

	@Override
	public void setFocus() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		LOGGER.entry(site, input);
		setSite(site);
		setInput(input);
		serverManager = Activator.getDefault().getServerManager();
		serverManager.addServerStateListener(this);
		manager = serverManager.getModuleManager();
		manager.addListener(this);
		mapBlockToTarget = new HashMap<FunctionBlockModel, BlockTarget>();
		LOGGER.exit();
	}

	@Override
	public void createPartControl(Composite parent) {
		LOGGER.entry(parent);
		functionBlocks = new ArrayList<FunctionBlockModel>();

		graphicsManager = new DeployViewGraphics(parent);
		graphicsManager.initializeParent();
		graphicsManager.initializeWidgets(this);

		loadBlockModels(getEditorInput());
		widgetsInitialized = true;
		LOGGER.exit();
	}

	/**
	 * Invoked whenever the UpdateModules Button is pressed.
	 */
	protected void updateModules() {
		if (Activator.getDefault().getServerManager().isRunning()) {
			manager.update();
		} else {
			warn(Messages.DEPLOY_SERVER_NOT_RUNNING);
			graphicsManager.addNewInfoText(Messages.DEPLOY_SERVER_NOT_RUNNING_INFO);
		}
	}

	/**
	 * Invoked whenever the UpdateBlocks Button is pressed.
	 */
	protected void updateBlocks() {
		LOGGER.entry();
		updateBlockList();
		if (selectedBlockModel != null) {
			for (FunctionBlockModel newModel : functionBlocks) {
				if (newModel.getID().equals(selectedBlockModel.getID())) {
					selectedBlockModel = newModel;
					graphicsManager.updateBlockSelection(selectedBlockModel.getPosition(),
							selectedBlockModel.getBlockName(), -1);
					graphicsManager.addNewInfoText(Messages.DEPLOY_BLOCKUPDATE_COMPLETE);
					LOGGER.exit();
					return;
				}
			}
			resetSelectedBlock();
		}
		graphicsManager.addNewInfoText(Messages.DEPLOY_BLOCKUPDATE_COMPLETE); //$NON-NLS-1$
		LOGGER.exit();
	}

	/**
	 * To be invoked before constraints are saved. This method does basically the same things like updateBlocks(), but
	 * doesn't change the text fields for name, location and module to be assigned to a block. Therefore, whatever the
	 * user entered in these fields will still be available after the update and not be changed to the name and position
	 * the block has within the graphiti diagram, so the constraints can still be saved for the selected block.
	 */
	private void updateBlocksForConstraints() {
		LOGGER.entry();
		updateBlockList();
		if (selectedBlockModel != null) {
			for (FunctionBlockModel newModel : functionBlocks) {
				if (newModel.getID().equals(selectedBlockModel.getID())) {
					selectedBlockModel = newModel;
					LOGGER.exit();
					return;
				}
			}
			resetSelectedBlock();
		}
		LOGGER.exit();
	}

	/**
	 * Updates the Blocks of DeployView.
	 * 
	 * @return Map from UUID of new Blocks to the Model.
	 */
	private void updateBlockList() {
		Collection<FunctionBlockModel> newBlockModels = new ArrayList<FunctionBlockModel>();
		Map<UUID, FunctionBlockModel> newIDs = new HashMap<UUID, FunctionBlockModel>();
		Map<UUID, FunctionBlockModel> oldIDs = new HashMap<UUID, FunctionBlockModel>();

		if (getEditorInput() instanceof FileEditorInput) {
			try {
				newBlockModels = loadInput((FileEditorInput) getEditorInput());
			} catch (IOException e) {
				LOGGER.catching(e);
			}
		} else {
			LOGGER.error(Messages.DEPLOY_INPUT_INCORRECT, getEditorInput());
		}

		for (FunctionBlockModel model : newBlockModels) {
			newIDs.put(model.getID(), model);
		}
		for (FunctionBlockModel model : functionBlocks) {
			oldIDs.put(model.getID(), model);
		}

		resetDeployment();

		for (FunctionBlockModel oldModel : functionBlocks) {
			if (newIDs.containsKey(oldModel.getID())) {
				FunctionBlockModel newModel = newIDs.get(oldModel.getID());
				replaceBlock(oldModel, newModel);
			} else {
				removeBlock(oldModel);
			}
		}

		for (FunctionBlockModel newModel : newBlockModels) {
			if (!oldIDs.containsKey(newModel.getID())) {
				addBlock(newModel);
			}
		}
		functionBlocks = newBlockModels;
	}

	/**
	 * Adds representation of a functionBlockModel that has just been added to the functionBlockModels list.
	 * 
	 * @param model
	 *            FunctionBlockModel to add.
	 */
	private void addBlock(FunctionBlockModel model) {
		String position = model.getPosition();
		if (position != null && !position.isEmpty()) {
			locationConstraints.put(model, position);
		}
		graphicsManager.createDeploymentItem(model.getBlockName(), position, model);
	}

	/**
	 * Replaces a FunctionBlockModel with another FunctionBlockModel. This method does NOT add the newBlock to the
	 * functionBlockModels list but takes care of everything else - representation and constraints.
	 * 
	 * @param oldBlock
	 *            old Block
	 * @param newBlock
	 *            new Block.
	 */
	private synchronized void replaceBlock(FunctionBlockModel oldBlock, FunctionBlockModel newBlock) {
		UUID module = moduleConstraints.get(oldBlock);
		moduleConstraints.remove(oldBlock);
		locationConstraints.remove(oldBlock);

		if (module != null) {
			moduleConstraints.put(newBlock, module);
		}

		String newPosition = newBlock.getPosition();
		if (newPosition != null && !newPosition.isEmpty()) {
			locationConstraints.put(newBlock, newPosition);
		}

		graphicsManager.replaceBlock(newBlock.getBlockName(), newPosition, oldBlock, newBlock);
	}

	private synchronized void removeBlock(FunctionBlockModel model) {
		moduleConstraints.remove(model);
		locationConstraints.remove(model);
		graphicsManager.disposeDeploymentItem(model);
	}

	/**
	 * Invoked whenever the Start / Stop Server Button is pressed.
	 */
	protected void toggleServer() {
		new Thread() {
			@Override
			public void run() {
				if (Activator.getDefault().getServerManager().isRunning()) {
					Activator.getDefault().shutdownServer();
				} else {
					Activator.getDefault().startServer();
				}
			}
		}.start();
	}

	/**
	 * Invoked whenever the Create Button is pressed.
	 */
	protected void distribute() {
		LOGGER.entry();
		Collection<Constraint> constraints = new ArrayList<Constraint>();
		synchronized (this) {
			constraints.add(new UserConstraints(new HashMap<FunctionBlockModel, UUID>(moduleConstraints),
					locationConstraints));
		}

		final Collection<ModuleInfo> modules =
				Activator.getDefault().getServerManager().getModuleManager().getModules();
		Distribution dist = null;
		try {
			dist = DistributionCreator.createDistribution(functionBlocks, constraints, modules);
		} catch (NoBlocksException e) {
			warn(Messages.DEPLOY_NO_BLOCKS);
			LOGGER.exit();
			return;
		} catch (NoModulesException e) {
			warn(Messages.DEPLOY_NO_MODULES);
			LOGGER.exit();
			return;
		}

		if (dist == null) {
			warn(Messages.DEPLOY_NO_VALID_DISTRIBUTION);
		} else {
			mapBlockToTarget = dist.getMapping();
			for (FunctionBlockModel block : mapBlockToTarget.keySet()) {
				final ModuleInfo m = mapBlockToTarget.get(block).getModule();
				graphicsManager.modifyDistributionInfo(block, m.getName(), m.getLocation());
				newConstraints = false;
			}
			graphicsManager.distributionCreated();
		}
		LOGGER.exit();
	}

	/**
	 * Invoked whenever the Deploy Button is pressed.
	 */
	protected void deploy() {
		LOGGER.entry();
		if (newConstraints) {
			int cancel = warn(Messages.DEPLOY_CONSTRAINTS_NEW);
			if (cancel == -4) {
				LOGGER.exit();
				return;
			}
		}

		if (mapBlockToTarget.isEmpty()) {
			warn(Messages.DEPLOY_NO_DIST_YET);
			LOGGER.exit();
			return;
		}

		final Dependencies dependencies =
				new Dependencies(StringUtil.joinArray(classPath, Messages.DEPLOY_COLON), Arrays.asList(
						Pattern.compile("java\\..*"), //$NON-NLS-2$ //$NON-NLS-1$
						Pattern.compile("edu\\.teco\\.dnd\\..*"), Pattern.compile("com\\.google\\.gson\\..*"), //$NON-NLS-1$ //$NON-NLS-2$
						Pattern.compile("org\\.apache\\.bcel\\..*"), Pattern.compile("io\\.netty\\..*"), //$NON-NLS-1$ //$NON-NLS-2$
						Pattern.compile("org\\.apache\\.logging\\.log4j"))); //$NON-NLS-1$
		final Deploy deploy =
				new Deploy(serverManager.getConnectionManager(), mapBlockToTarget, graphicsManager.getAppName(),
						dependencies);
		// TODO: I don't know if this will be needed by DeployView. It can be used to wait until the deployment finishes
		// or to run code at that point

		deploy.getDeployFutureNotifier().addListener(new FutureListener<FutureNotifier<? super Void>>() {
			@Override
			public void operationComplete(final FutureNotifier<? super Void> future) {
				DisplayUtil.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						updateModules();
						if (LOGGER.isInfoEnabled()) {
							LOGGER.info("deploy: {}", future.isSuccess()); //$NON-NLS-1$
						}
						graphicsManager.deploymentFinished(future.isSuccess());
					}
				});
			}
		});

		DeployViewProgress.startDeploying(graphicsManager.getAppName(), deploy, mapBlockToTarget);
		graphicsManager.deploymentStarted();
		resetDeployment();
		LOGGER.exit();
	}

	/**
	 * Invoked whenever a Function BlockModel from the deploymentTable is selected.
	 */
	protected void blockModelSelected() {
		graphicsManager.blockSelected(!idList.isEmpty());

		selectedBlockModel = graphicsManager.getSelectedBlock();

		String position = null;

		if (locationConstraints.containsKey(selectedBlockModel)) {
			position = locationConstraints.get(selectedBlockModel);
		}
		selectedIndex = idList.indexOf(moduleConstraints.get(selectedBlockModel)) + 1;

		graphicsManager.updateBlockSelection(position, selectedBlockModel.getBlockName(), selectedIndex);

	}

	/**
	 * Invoked whenever a ModuleInfo from moduleCombo is selected.
	 */
	protected void moduleSelected() {
		selectedIndex = graphicsManager.getModuleComboIndex();
	}

	/**
	 * Invoked whenever the saveConstraints button is selected or "Enter" is pressed while the focus is set to one of
	 * the constraint text fields.
	 */
	protected synchronized void saveConstraints() {
		updateBlocksForConstraints();
		if (selectedBlockModel == null) {
			warn(Messages.DEPLOY_CONSTRAINTS_BLOCK_REMOVED);
			return;
		}

		String location = graphicsManager.getLocationsText();
		String newName = graphicsManager.getBlockNameText();
		try {
			Pattern.compile(location);
		} catch (PatternSyntaxException e) {
			warn(Messages.DEPLOY_NO_REGEX_LOCATION);
			graphicsManager.addNewInfoText(Messages.DEPLOY_CONSTRAINTS_NOT_SAVED);
			return;
		}
		try {
			Pattern.compile(newName);
		} catch (PatternSyntaxException e) {
			warn(Messages.DEPLOY_NO_REGEX_NAME);
			graphicsManager.addNewInfoText(Messages.DEPLOY_CONSTRAINTS_NOT_SAVED);
			return;

		}

		if (selectedIndex > 0) {
			selectedID = idList.get(selectedIndex - 1);
		} else {
			selectedID = null;
		}

		if (!location.isEmpty() && selectedID != null) {
			graphicsManager.replaceInfoText(Messages.DEPLOY_CONSTRAINTS_INFORM);
			int cancel = warn(Messages.DEPLOY_CONSTRAINTS_WARN);
			if (cancel == -4) {
				graphicsManager.addNewInfoText(Messages.DEPLOY_CONSTRAINTS_NOT_SAVED);
				return;
			}
		}

		if (location.isEmpty()) {
			locationConstraints.remove(selectedBlockModel);
		} else {
			locationConstraints.put(selectedBlockModel, location);
		}
		selectedBlockModel.setPosition(location);

		String module = null;
		if (selectedID != null) {
			moduleConstraints.put(selectedBlockModel, selectedID);
			module = graphicsManager.getItemFromModuleCombo(selectedIndex);
		} else {
			moduleConstraints.remove(selectedBlockModel);
		}
		selectedBlockModel.setBlockName(newName);
		graphicsManager.displayConstraints(newName, module, location);

		try {
			resource.save(Collections.EMPTY_MAP);
			resource.setModified(true); // not sure if this does sth.
		} catch (IOException e) {
			e.printStackTrace();
		}

		firePropertyChange(IEditorPart.PROP_DIRTY); // not sure if this does sth.

		graphicsManager.addNewInfoText(Messages.DEPLOY_CONSTRAINTS_SAVED);

		newConstraints = true;
	}

	/**
	 * Adds a ModuleInfo ID.
	 * 
	 * @param id
	 *            the ID to add
	 */
	private synchronized void addID(final UUID id) {
		LOGGER.entry(id);
		if (!idList.contains(id)) {
			LOGGER.trace("id {} is new, adding", id); //$NON-NLS-1$
			graphicsManager.addToModuleCombo(id.toString());
			idList.add(id);

		} else {
			LOGGER.debug("trying to add existing id {}", id); //$NON-NLS-1$
		}
		LOGGER.exit();
	}

	/**
	 * Removes a ModuleInfo ID including all dependent information on this module.
	 * 
	 * @param id
	 *            the ID to remove
	 */
	private synchronized void removeID(final UUID id) {
		LOGGER.entry(id);
		int idIndex = idList.indexOf(id);
		if (idIndex >= 0) {
			graphicsManager.removeFromModuleCombo(idIndex + 1);
			idList.remove(idIndex);
			resetDeployment();
			for (FunctionBlockModel model : moduleConstraints.keySet()) {
				if (moduleConstraints.get(model).equals(id)) {
					moduleConstraints.remove(model);
					graphicsManager.moduleRenamed(model, Messages.DEPLOYGRAPHICS_EMPTYSTRING); //$NON-NLS-1$
				}
			}
			LOGGER.trace("found combo entry for id {}", id); //$NON-NLS-1$
		} else {
			LOGGER.debug("trying to remove nonexistant id {}", id); //$NON-NLS-1$
		}
		LOGGER.exit();
	}

	/**
	 * Loads the FunctinoBlockModels of a data flow graph and displays them in the deployment table.
	 * 
	 * @param input
	 *            the input of the editor
	 */
	private void loadBlockModels(IEditorInput input) {
		// set to empty Collection to prevent NPE in case loading fails
		functionBlocks = new ArrayList<FunctionBlockModel>();
		if (input instanceof FileEditorInput) {
			final IProject project = EclipseUtil.getWorkspaceProject(((FileEditorInput) input).getPath());
			if (project != null) {
				classPath = getClassPath(project);
			} else {
				classPath = new URL[0];
			}

			try {
				functionBlocks = loadInput((FileEditorInput) input);
			} catch (IOException e) {
				LOGGER.catching(e);
			}
		} else {
			LOGGER.error("Input is not a FileEditorInput {}", input); //$NON-NLS-1$
		}
		for (FunctionBlockModel blockModel : functionBlocks) {
			addBlock(blockModel);
		}
	}

	private URL[] getClassPath(final IProject project) {
		final Set<IPath> paths = EclipseUtil.getAbsoluteBinPaths(project);
		final ArrayList<URL> classPath = new ArrayList<URL>(paths.size());
		for (final IPath path : paths) {
			try {
				classPath.add(path.toFile().toURI().toURL());
			} catch (final MalformedURLException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.catching(Level.DEBUG, e);
					LOGGER.debug("Not adding path {} to class path", path); //$NON-NLS-1$
				}
			}
		}
		return classPath.toArray(new URL[0]);
	}

	/**
	 * Loads the given data flow graph. The file given in the editor input must be a valid graph. Its function block
	 * models are loaded into a list and returned.
	 * 
	 * @param input
	 *            the input of the editor
	 * @return a collection of FunctionBlockModels that were defined in the model
	 * @throws IOException
	 *             if reading fails
	 */
	private Collection<FunctionBlockModel> loadInput(final FileEditorInput input) throws IOException {
		LOGGER.entry(input);
		Collection<FunctionBlockModel> blockModelList = new ArrayList<FunctionBlockModel>();
		graphicsManager.setAppName(input.getFile().getName()
				.replaceAll("\\.blocks", Messages.DEPLOYGRAPHICS_EMPTYSTRING)); //$NON-NLS-1$ //$NON-NLS-2$

		URI uri = URI.createURI(input.getURI().toASCIIString());
		resource = new XMIResourceImpl(uri);
		resource.setTrackingModification(true);
		resource.load(null);
		for (EObject object : resource.getContents()) {
			if (object instanceof FunctionBlockModel) {
				LOGGER.trace("found FunctionBlockModel {}", object); //$NON-NLS-1$
				FunctionBlockModel blockmodel = (FunctionBlockModel) object;
				blockModelList.add(blockmodel);
			}
		}
		return blockModelList;
	}

	/**
	 * Opens a warning window displaying the given message.
	 * 
	 * @param message
	 *            Warning message
	 * @return int representing the choice of the user.
	 */
	private int warn(String message) {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		dialog.setText(Messages.DEPLOY_WARNING);
		dialog.setMessage(message);
		return dialog.open();
	}

	/**
	 * Invoked whenever a possibly created deployment gets invalid.
	 */
	private void resetDeployment() {
		mapBlockToTarget = new HashMap<FunctionBlockModel, BlockTarget>();
		graphicsManager.resetDeployment();
	}

	/**
	 * Invoked whenever the selected Block gets dirty.
	 */
	private void resetSelectedBlock() {
		selectedBlockModel = null;
		graphicsManager.resetBlockSelection();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		manager.removeListener(this);
	}

	@Override
	public void moduleAdded(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (widgetsInitialized) {
					addID(module.getUUID());
				}
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleRemoved(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (widgetsInitialized) {
					removeID(module.getUUID());
				}
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleUpdated(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().asyncExec(new Runnable() {
			@Override
			public synchronized void run() {
				if (widgetsInitialized) {
					final UUID moduleID = module.getUUID();
					if (!idList.contains(moduleID)) {
						addID(moduleID);
					}

					int comboIndex = idList.indexOf(moduleID) + 1;
					String text = Messages.DEPLOYGRAPHICS_EMPTYSTRING; //$NON-NLS-1$
					if (module.getName() != null) {
						text = module.getName();
					}
					text = text.concat(Messages.DEPLOY_COLON); //$NON-NLS-1$
					text = text.concat(moduleID.toString());
					graphicsManager.setItemToModuleCombo(comboIndex, text);
					if (moduleConstraints.containsValue(moduleID)) {
						for (FunctionBlockModel blockModel : moduleConstraints.keySet()) {
							graphicsManager.moduleRenamed(blockModel, text);
						}
					}
				}
			}
		});
		LOGGER.exit();
	}

	@Override
	public void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		switch (state) {
		case RUNNING:
			serverOnline();
			break;

		case STOPPED:
			serverOffline();
		}
	}

	private void serverOnline() {
		DisplayUtil.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (widgetsInitialized) {
					graphicsManager.serverOnline();

					synchronized (DeployView.this) {
						while (!idList.isEmpty()) {
							removeID(idList.get(0)); // TODO: Unschön, aber geht
														// hoffentlich?
						}
					}
				}
			}
		});
	}

	private void serverOffline() {
		DisplayUtil.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (widgetsInitialized) {
					graphicsManager.serverOffline();
					resetDeployment();
					synchronized (DeployView.this) {
						while (!idList.isEmpty()) {
							removeID(idList.get(0)); // TODO: Unschön, aber geht
														// hoffentlich?
						}
					}
				}
			}
		});
	}
}
