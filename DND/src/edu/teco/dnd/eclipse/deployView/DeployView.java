package edu.teco.dnd.eclipse.deployView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.deploy.Deploy;
import edu.teco.dnd.deploy.DistributionGenerator;
import edu.teco.dnd.deploy.MinimalModuleCountEvaluator;
import edu.teco.dnd.deploy.UserConstraints;
import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.eclipse.EclipseUtil;
import edu.teco.dnd.eclipse.ModuleManager;
import edu.teco.dnd.eclipse.ModuleManagerListener;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.util.Dependencies;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung anwählen -
 * Verteilungsalgorithmus auswählen - Fest im Code einbinden? - Verteilung
 * erstellen lassen und anzeigen - Verteilung bestätigen
 * 
 */
public class DeployView extends EditorPart implements ModuleManagerListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DeployView.class);
	private Display display;
	private Activator activator;
	private ModuleManager manager;

	private ArrayList<UUID> idList = new ArrayList<UUID>();

	private Collection<FunctionBlockModel> functionBlockModels;
	private Map<TableItem, FunctionBlockModel> mapItemToBlockModel;

	private Map<FunctionBlock, BlockTarget> mapBlockToTarget;

	private DeployViewGraphics graphicsManager;
	
	private Button serverButton;
	private Button updateModulesButton; // Button to update moduleCombo
	private Button updateBlocksButton;
	private Button createButton; // Button to create deployment
	private Button deployButton; // Button to deploy deployment
	private Button constraintsButton;
	private Label appName;
	private Text blockModelName; // Name of BlockModel
	private Combo moduleCombo;
	private Text places;
	private Table deployment; // Table to show blockModels and current
								// deployment

	private int selectedIndex; // Index of selected field of moduleCombo
	private UUID selectedID;
	private TableItem selectedItem;
	private FunctionBlockModel selectedBlockModel; // FunctionblockModel to edit
													// specs
	/**
	 * Enthält für jeden FunktionsblockModel die UUID des Moduls, auf das er
	 * gewünscht ist, oder null, falls kein Modul vom User ausgewählt. Achtung:
	 * Kann UUIDs von Modulen enthalten, die nicht mehr laufen.
	 * 
	 * Vielleicht gut: Constraints auch speichern, wenn Anwendung geschlossen
	 * wird.
	 */
	private Map<FunctionBlockModel, UUID> moduleConstraints = new HashMap<FunctionBlockModel, UUID>();
	private Map<FunctionBlockModel, String> placeConstraints = new HashMap<FunctionBlockModel, String>();

	@Override
	public void setFocus() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		LOGGER.entry(site, input);
		setSite(site);
		setInput(input);
		activator = Activator.getDefault();
		display = Display.getCurrent();
		manager = activator.getModuleManager();
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace(
					"Display.getCurrent() returned null, using Display.getDefault(): {}",
					display);
		}
		manager.addModuleManagerListener(this);
		LOGGER.exit();
		mapBlockToTarget = new HashMap<FunctionBlock, BlockTarget>();
	}

	@Override
	public void createPartControl(Composite parent) {
		graphicsManager = new DeployViewGraphics(parent, activator);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		parent.setLayout(layout);

		functionBlockModels = new ArrayList<FunctionBlockModel>();
		mapItemToBlockModel = new HashMap<TableItem, FunctionBlockModel>();

		appName = new Label(parent, SWT.NONE);
		appName.pack();

		serverButton = graphicsManager.createServerButton(parent);
		graphicsManager.createBlockModelSpecsLabel(parent);
		deployment = graphicsManager.createDeploymentTable(parent);
		updateModulesButton = graphicsManager.createUpdateModulesButton(parent);
		graphicsManager.createBlockModelLabel(parent);
		blockModelName = graphicsManager.createBlockModelName(parent);
		updateBlocksButton = graphicsManager.createUpdateBlocksButton(parent);
		graphicsManager.createModuleLabel(parent);
		moduleCombo = graphicsManager.createModuleCombo(parent);
		createButton = graphicsManager.createCreateButton(parent);
		graphicsManager.createPlaceLabel(parent);
		places = graphicsManager.createPlacesText(parent);
		deployButton = graphicsManager.createDeployButton(parent);
		constraintsButton = graphicsManager.createConstraintsButton(parent);
		createSelectionListeners();
		
		loadBlockModels(getEditorInput());
	}

	/**
	 * Invoked whenever the UpdateModules Button is pressed.
	 */
	private void updateModules() {
		if (Activator.getDefault().isRunning()) {
			warn("Not implemented yet. \n Later: Will update information on moduleCombo");
		} else {
			warn("Server not running");
		}

	}

	/**
	 * Invoked whenever the UpdateBlocks Button is pressed.
	 */
	private void updateBlocks() {
		deployment.removeAll();
		moduleConstraints.clear();
		placeConstraints.clear();
		mapItemToBlockModel.clear();
		mapBlockToTarget = new HashMap<FunctionBlock, BlockTarget>();
		selectedItem = null;
		selectedBlockModel = null;
		moduleCombo.setEnabled(false);
		places.setEnabled(false);
		blockModelName.setText("<select block on the left>");
		blockModelName.setEnabled(false);
		loadBlockModels(getEditorInput());
	}

	/**
	 * Invoked whenever the Create Button is pressed.
	 */
	private void create() {
		Collection<Module> moduleCollection = getModuleCollection();
		if (functionBlockModels.isEmpty()) {
			warn("No blockModels to distribute");
			return;
		}
		if (moduleCollection.isEmpty()) {
			warn("No modules to deploy on");
			return;
		}

		Map<FunctionBlock, FunctionBlockModel> blocksToModels = new HashMap<FunctionBlock, FunctionBlockModel>();
		for (FunctionBlockModel model : functionBlockModels) {
			try {
				blocksToModels.put(model.createBlock(), model);
			} catch (InvalidFunctionBlockException e) {
				e.printStackTrace();
			}
		}

		Collection<Constraint> constraints = new ArrayList<Constraint>();
		constraints
				.add(new UserConstraints(moduleConstraints, placeConstraints));

		DistributionGenerator generator = new DistributionGenerator(
				new MinimalModuleCountEvaluator(), constraints);
		Distribution dist = generator.getDistribution(blocksToModels.keySet(),
				moduleCollection);

		if (dist == null) {
			warn("No valid deployment exists");
		} else {
			mapBlockToTarget = dist.getMapping();
			for (FunctionBlock block : mapBlockToTarget.keySet()) {
				FunctionBlockModel blockModel = blocksToModels.get(block);
				TableItem item = getItem(blockModel);
				final String name = mapBlockToTarget.get(block).getModule()
						.getName();
				item.setText(3, name == null ? "" : name);
				final String location = mapBlockToTarget.get(block).getModule()
						.getLocation();
				item.setText(4, location == null ? "" : location);
			}
		}
	}

	/**
	 * Invoked whenever the Deploy Button is pressed.
	 */
	private void deploy() {
		if (mapBlockToTarget.isEmpty()) {
			warn(DeployViewTexts.NO_DEPLOYMENT_YET);
			return;
		}

		final Deploy deploy = new Deploy(Activator.getDefault()
				.getConnectionManager(), new Dependencies(Arrays.asList(Pattern
				.compile("java\\..*"))));
		deploy.deploy("foo", UUID.randomUUID(), mapBlockToTarget).addListener(
				new FutureListener<FutureNotifier<? super Void>>() {
					@Override
					public void operationComplete(
							FutureNotifier<? super Void> future) {
						if (LOGGER.isInfoEnabled()) {
							LOGGER.info("deploy: {}", future.isSuccess());
						}
					}
				});
		mapBlockToTarget = new HashMap<FunctionBlock, BlockTarget>();
		for (TableItem item : deployment.getItems()){
			item.setText(3, "");
			item.setText(4, "");
		}
	}

	/**
	 * Invoked whenever a Function BlockModel from the deploymentTable is
	 * selected.
	 */
	protected void blockModelSelected() {
		moduleCombo.setEnabled(true);
		places.setEnabled(true);
		constraintsButton.setEnabled(true);
		blockModelName.setEnabled(true);
		TableItem[] items = deployment.getSelection();
		if (items.length == 1) {
			selectedItem = items[0];
			selectedBlockModel = mapItemToBlockModel.get(items[0]);
			blockModelName.setText(selectedBlockModel.getBlockName());
			if (placeConstraints.containsKey(selectedBlockModel)) {
				places.setText(placeConstraints.get(selectedBlockModel));
			} else {
				places.setText("");
			}
		}
		selectedIndex = -1;
		selectedID = null;
	}

	/**
	 * Invoked whenever a Module from moduleCombo is selected.
	 */
	private void moduleSelected() {
		selectedIndex = moduleCombo.getSelectionIndex();
		selectedID = idList.get(selectedIndex);
	}

	private void saveConstraints() {
		String text = places.getText();

		if (!text.isEmpty() && selectedID != null) { // skfdjhasdfhsdfksjdhkfjhsdfkjsldkfjslkdjfsajdhfka
														// bka bla bla bla lkj j
														// sdf sdf
			int cancel = warn(DeployViewTexts.WARN_CONSTRAINTS);
			if (cancel == -4) {
				return;
			}
		}

		if (text.isEmpty()) {
			placeConstraints.remove(selectedBlockModel);
		} else {
			placeConstraints.put(selectedBlockModel, text);
		}
		selectedItem.setText(2, text);

		if (selectedID != null && selectedIndex > -1) {
			moduleConstraints.put(selectedBlockModel, selectedID);
			String module = moduleCombo.getItem(selectedIndex);
			selectedItem.setText(1, module);
		} else {
			selectedItem.setText(1, "");
			moduleConstraints.remove(selectedBlockModel);
		}

		selectedBlockModel.setBlockName(this.blockModelName.getText());
		selectedItem.setText(0, blockModelName.getText());
	}

	/**
	 * Adds a Module ID to the moduleCombo.
	 * 
	 * @param id
	 *            the ID to add
	 */
	private synchronized void addID(final UUID id) {
		LOGGER.entry(id);
		if (!idList.contains(id)) {
			LOGGER.trace("id {} is new, adding", id);
			moduleCombo.add(id.toString());
			idList.add(id);

		} else {
			LOGGER.debug("trying to add existing id {}", id);
		}
		LOGGER.exit();
	}

	/**
	 * Removes a Module ID from the moduleCombo.
	 * 
	 * @param id
	 *            the ID to remove
	 */
	private synchronized void removeID(final UUID id) {
		LOGGER.entry(id);
		int index = idList.indexOf(id);
		if (index >= 0) {
			moduleCombo.remove(index);
			idList.remove(index);
			mapBlockToTarget = new HashMap<FunctionBlock, BlockTarget>();
			for (FunctionBlockModel model : moduleConstraints.keySet()){
				if (moduleConstraints.get(model).equals(id)){
					moduleConstraints.remove(model);
					getItem(model).setText(1, "");
				}
			}
			LOGGER.trace("found combo entry for id {}", id);
		} else {
			LOGGER.debug("trying to remove nonexistant id {}", id);
		}
		LOGGER.exit();
	}

	/**
	 * Loads the FunctinoBlockModels of a dataflowgraph and displays them in the
	 * deployment table.
	 * 
	 * @param input
	 *            the input of the editor
	 */
	private void loadBlockModels(IEditorInput input) {
		if (input instanceof FileEditorInput) {
			try {
				functionBlockModels = loadInput((FileEditorInput) input);
			} catch (IOException e) {
				LOGGER.catching(e);
			}
		} else {
			LOGGER.error("Input is not a FileEditorInput {}", input);
		}
		mapItemToBlockModel.clear();
		for (FunctionBlockModel blockModel : functionBlockModels) {
			TableItem item = new TableItem(deployment, SWT.NONE);
			if (blockModel.getBlockName() != null) {
				item.setText(0, blockModel.getBlockName());
			}
			String pos = blockModel.getPosition();
			if (pos != null) {
				item.setText(2, pos);
				placeConstraints.put(blockModel, pos);
			}
			mapItemToBlockModel.put(item, blockModel);
		}
	}

	/**
	 * Loads the given data flow graph. The file given in the editor input must
	 * be a valid graph. Its function block models are loaded into a list and
	 * returned.
	 * 
	 * @param input
	 *            the input of the editor
	 * @return a collection of FunctionBlockModels that were defined in the
	 *         model
	 * @throws IOException
	 *             if reading fails
	 */
	private Collection<FunctionBlockModel> loadInput(final FileEditorInput input)
			throws IOException {
		LOGGER.entry(input);
		Collection<FunctionBlockModel> blockModelList = new ArrayList<FunctionBlockModel>();
		appName.setText(input.getFile().getName().replaceAll("\\.diagram", ""));

		Set<IPath> paths = EclipseUtil.getAbsoluteBinPaths(input.getFile()
				.getProject());
		LOGGER.debug("using paths {}", paths);
		/**
		 * URL[] urls = new URL[paths.size()]; String[] classpath = new
		 * String[paths.size()]; int i = 0; for (IPath path : paths) { urls[i] =
		 * path.toFile().toURI().toURL(); classpath[i] =
		 * path.toFile().getPath(); i++; }
		 */
		URI uri = URI.createURI(input.getURI().toASCIIString());
		Resource resource = new XMIResourceImpl(uri);
		resource.load(null);
		for (EObject object : resource.getContents()) {
			if (object instanceof FunctionBlockModel) {
				LOGGER.trace("found FunctionBlockModel {}", object);
				blockModelList.add(((FunctionBlockModel) object));
			}
		}
		return blockModelList;
	}

	/**
	 * Opens a warning window with the given message.
	 * 
	 * @param message
	 *            Warning message
	 */
	private int warn(String message) {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		dialog.setText("Warning");
		dialog.setMessage(message);
		return dialog.open();
	}

	/**
	 * Returns a Collection of currently running modules that are already
	 * resolved. Does not contain modules that haven't been resolved from their
	 * UUID yet.
	 * 
	 * @return collection of currently running modules to deploy on.
	 */
	private Collection<Module> getModuleCollection() {
		Collection<Module> collection = new ArrayList<Module>();
		Map<UUID, Module> map = manager.getMap();
		for (UUID id : map.keySet()) {
			Module m = map.get(id);
			if (m != null) {
				collection.add(m);
			}
		}
		return collection;
	}

	/**
	 * Returns table item representing a given function blockModel.
	 * 
	 * @param blockModel
	 *            The blockModel to find in the table
	 * @return item holding the blockModel
	 */
	private TableItem getItem(FunctionBlockModel blockModel) {
		UUID id = blockModel.getID();
		for (TableItem i : mapItemToBlockModel.keySet()) {
			if (mapItemToBlockModel.get(i).getID() == id) {
				return i;
			}
		}
		return null;
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
		manager.removeModuleManagerListener(this);
	}

	@Override
	public void moduleOnline(final UUID id) {
		LOGGER.entry(id);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				addID(id);
			}
		});
		LOGGER.exit();
	}

	// TODO: Was tun, wenn auf modul deployt werden soll, das offline ist?
	@Override
	public void moduleOffline(final UUID id) {
		LOGGER.entry(id);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				removeID(id);
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleResolved(final UUID id, final Module module) {

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				int index = idList.indexOf(id);
				if (index < 0) {
					addID(id);
				} else {
					String text = id.toString();
					text.concat(" : ");
					if (module.getName() != null) {
						text.concat(module.getName());
					}
					moduleCombo.setItem(index, text);

					// Falls das Modul schon einem FB zugeteilt wurde
					if (moduleConstraints.containsValue(id)) {
						// Überprüfe alle FB (können auch mehrere sein)
						for (FunctionBlockModel blockModel : moduleConstraints
								.keySet()) {
							// Setze Text neu.
							getItem(blockModel).setText(1, text);
						}
					}
				}
			}
		});
	}

	@Override
	public void serverOnline(final Map<UUID, Module> modules) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (serverButton != null) {
					serverButton.setText("Stop Server");
				}
				updateModulesButton.setEnabled(true);
				createButton.setEnabled(true);
				deployButton.setEnabled(true);
				moduleCombo.setToolTipText("");

				synchronized (DeployView.this) {
					while (!idList.isEmpty()) {
						removeID(idList.get(0)); // TODO: Unschön, aber geht
													// hoffentlich?
					}
					for (UUID moduleID : modules.keySet()) {
						addID(moduleID);
					}
				}
			}
		});
	}

	@Override
	public void serverOffline() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (serverButton != null) {
					serverButton.setText("Start Server");
				}
				updateModulesButton.setEnabled(false);
				createButton.setEnabled(false);
				deployButton.setEnabled(false);
				moduleCombo
						.setToolTipText(DeployViewTexts.SELECTMODULEOFFLINE_TOOLTIP);
				synchronized (DeployView.this) {
					while (!idList.isEmpty()) {
						removeID(idList.get(0)); // TODO: Unschön, aber geht
													// hoffentlich?
					}
				}
			}
		});
	}

	
	private void createSelectionListeners(){
		serverButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					@Override
					public void run() {
						if (DeployView.this.activator.isRunning()) {
							DeployView.this.activator.shutdownServer();
						} else {
							DeployView.this.activator.startServer();
						}
					}
				}.run();
			}
		});
		
		deployment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.blockModelSelected();
			}
		});
		
		updateModulesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.updateModules();
			}
		});
		
		updateBlocksButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.updateBlocks();
			}
		});
		
		moduleCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.moduleSelected();
			}
		});
		
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.create();
			}
		});
		
		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.deploy();
			}
		});
		
		constraintsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DeployView.this.saveConstraints();
			}
		});
	}
}
