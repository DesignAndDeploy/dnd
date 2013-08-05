package edu.teco.dnd.eclipse;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import edu.teco.dnd.deploy.DistributionGenerator;
import edu.teco.dnd.deploy.MinimalModuleCountEvaluator;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;

/**
 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung anwählen -
 * Verteilungsalgorithmus auswählen - Fest im Code einbinden? - Verteilung
 * erstellen lassen und anzeigen - Verteilung bestätigen
 * 
 */
public class ViewDeploy extends EditorPart implements ModuleManagerListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ViewDeploy.class);
	private Display display;
	private Activator activator;
	private ModuleManager manager;

	private ArrayList<UUID> idList = new ArrayList<UUID>();

	private Collection<FunctionBlock> functionBlocks;
	private Map<TableItem, FunctionBlock> mapItemToBlock;

	private Map<FunctionBlock, BlockTarget> mapBlockToTarget;

	private Button serverButton;
	private Button updateButton; // Button to update moduleCombo
	private Button createButton; // Button to create deployment
	private Button deployButton; // Button to deploy deployment
	private Button constraintsButton;
	private Label appName;
	private Label blockSpecifications;
	private Label blockLabel; // Block to edit specifications
	private Label module;
	private Label place;
	private Combo moduleCombo;
	private Text places;
	private Table deployment; // Table to show blocks and current deployment

	private int selectedIndex; // Index of selected field of moduleCombo
	private UUID selectedID;
	private TableItem selectedItem;
	private FunctionBlock selectedBlock; // Functionblock to edit specs
	/**
	 * Enthält für jeden Funktionsblock die UUID des Moduls, auf das er
	 * gewünscht ist, oder null, falls kein Modul vom User ausgewählt. Achtung:
	 * Kann UUIDs von Modulen enthalten, die nicht mehr laufen.
	 * 
	 * Vielleicht gut: Constraints auch speichern, wenn Anwendung geschlossen
	 * wird.
	 */
	private Map<FunctionBlock, UUID> moduleConstraints = new HashMap<FunctionBlock, UUID>();
	private Map<FunctionBlock, String> placeConstraints = new HashMap<FunctionBlock, String>();

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
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		parent.setLayout(layout);

		functionBlocks = new ArrayList<FunctionBlock>();
		mapItemToBlock = new HashMap<TableItem, FunctionBlock>();

		appName = new Label(parent, SWT.NONE);
		appName.pack();

		createServerButton(parent);
		createBlockSpecsLabel(parent);
		createDeploymentTable(parent);
		createUpdateButton(parent);
		createBlockLabel(parent);
		createCreateButton(parent);
		createModuleLabel(parent);
		createmoduleCombo(parent);
		createDeployButton(parent);
		createPlaceLabel(parent);
		createPlacesText(parent);
		createConstraintsButton(parent);

		loadBlocks(getEditorInput());
	}

	private void createServerButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		serverButton = new Button(parent, SWT.NONE);
		serverButton.setLayoutData(data);
		if (activator.isRunning()) {
			serverButton.setText("Stop server");
		} else {
			serverButton.setText("Start server");
		}
		serverButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					@Override
					public void run() {
						if (ViewDeploy.this.activator.isRunning()) {
							ViewDeploy.this.activator.shutdownServer();
						} else {
							ViewDeploy.this.activator.startServer();
						}
					}
				}.run();
			}
		});

	}

	private void createDeploymentTable(Composite parent) {
		GridData data = new GridData();
		data.verticalSpan = 4;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		deployment = new Table(parent, SWT.NONE);
		deployment.setLinesVisible(true);
		deployment.setHeaderVisible(true);
		deployment.setLayoutData(data);

		TableColumn column1 = new TableColumn(deployment, SWT.None);
		column1.setText("Function Block");
		TableColumn column2 = new TableColumn(deployment, SWT.NONE);
		column2.setText("Module");
		column2.setToolTipText("Deploy Block on this Module, if possible. No module selected means no constraint for deployment");
		TableColumn column3 = new TableColumn(deployment, SWT.NONE);
		column3.setText("place");
		column3.setToolTipText("Deploy Block at this place, if possible. No place selected means no constraint for deployment");
		TableColumn column4 = new TableColumn(deployment, SWT.NONE);
		column4.setText("Deployed on:");
		column4.setToolTipText("Module assigned to the Block by the deployment algorithm");
		TableColumn column5 = new TableColumn(deployment, SWT.NONE);
		column5.setText("Deployed at:");
		column5.setToolTipText("Place the Block will be deployed to");
		deployment.getColumn(0).pack();
		deployment.getColumn(1).pack();
		deployment.getColumn(2).pack();
		deployment.getColumn(3).pack();
		deployment.getColumn(4).pack();

		deployment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.blockSelected();
			}
		});
	}

	private void createUpdateButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateButton = new Button(parent, SWT.NONE);
		updateButton.setLayoutData(data);
		updateButton.setText("Update");
		updateButton.setToolTipText("Updates information on moduleCombo");
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.update();
			}
		});
		updateButton.pack();
	}

	private void createBlockSpecsLabel(Composite parent) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		blockSpecifications = new Label(parent, SWT.NONE);
		blockSpecifications.setText("Block Specifications:");
		blockSpecifications.setLayoutData(data);
		blockSpecifications.pack();
	}

	private void createCreateButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		createButton = new Button(parent, SWT.NONE);
		createButton.setLayoutData(data);
		createButton.setText("Create Deployment");
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.create();
			}
		});
	}

	private void createBlockLabel(Composite parent) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		blockLabel = new Label(parent, SWT.NONE);
		blockLabel.setText("(select block on the left)");
		blockLabel.setLayoutData(data);
		blockLabel.pack();
	}

	private void createDeployButton(Composite parent) {
		GridData data = new GridData();
		data.verticalSpan = 3;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;

		deployButton = new Button(parent, SWT.NONE);
		deployButton.setText("Deploy");
		deployButton
				.setToolTipText("Submit the created deployment to deploy your application");
		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.deploy();
			}
		});
		deployButton.setLayoutData(data);
	}

	private void createModuleLabel(Composite parent) {
		module = new Label(parent, SWT.NONE);
		module.setText("Module:");
		module.setToolTipText("Select a Module for this function block");
		module.pack();
	}

	private void createmoduleCombo(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.setLayoutData(data);

		moduleCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.moduleSelected();
			}
		});
		moduleCombo.setEnabled(false);
	}

	private void createPlaceLabel(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 2;
		place = new Label(parent, SWT.NONE);
		place.setLayoutData(data);
		place.setText("Place:");
		place.setToolTipText("Select a place for this function block");
		place.pack();
	}

	private void createPlacesText(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		places = new Text(parent, SWT.NONE);
		places.setToolTipText("Enter location for selected Function Block");
		places.setLayoutData(data);
		places.setEnabled(false);
	}

	private void createConstraintsButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		constraintsButton = new Button(parent, SWT.NONE);
		constraintsButton.setLayoutData(data);
		constraintsButton.setText("Save constraints");
		constraintsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ViewDeploy.this.saveConstraints();
			}
		});
		constraintsButton.setEnabled(false);
		constraintsButton.pack();
	}

	/**
	 * Invoked whenever the Update Button is pressed.
	 */
	private void update() {
		if (Activator.getDefault().isRunning()) {
			warn("Not implemented yet. \n Later: Will update information on moduleCombo");
		} else {
			warn("Server not running");
		}

	}

	/**
	 * Invoked whenever the Create Button is pressed.
	 */
	private void create() {
		Collection<Module> moduleCollection = getModuleCollection();
		if (functionBlocks.isEmpty() || moduleCollection.isEmpty()) {
			warn("No blocks / modules to distribute");
			return;
		}
		DistributionGenerator generator = new DistributionGenerator(
				new MinimalModuleCountEvaluator(),
				Collections.<Constraint> emptyList());
		Distribution dist = generator.getDistribution(functionBlocks,
				moduleCollection);
		if (dist.getMapping() == null) {
			warn("No valid deployment exists");
		} else {
			mapBlockToTarget = dist.getMapping();
			deployment.clearAll();
			for (FunctionBlock block : mapBlockToTarget.keySet()) {
				TableItem item = new TableItem(deployment, SWT.NONE);
				item.setText(0, block.getType());
				item.setText(1, mapBlockToTarget.get(block).getModule()
						.getName());
				item.setText(2, mapBlockToTarget.get(block).getModule()
						.getLocation());
			}
		}
	}

	/**
	 * Invoked whenever the Deploy Button is pressed.
	 */
	private void deploy() {
		if (mapBlockToTarget.isEmpty()) {
			warn("No deployment created yet");
			return;
		}
		// TODO: deploy map.
	}

	/**
	 * Invoked whenever a Function Block from the deploymentTable is selected.
	 */
	private void blockSelected() {
		moduleCombo.setEnabled(true);
		places.setEnabled(true);
		constraintsButton.setEnabled(true);
		TableItem[] items = deployment.getSelection();
		if (items.length == 1) {
			selectedItem = items[0];
			selectedBlock = mapItemToBlock.get(items[0]);
			blockLabel.setText(selectedBlock.getType());
			if (placeConstraints.containsKey(selectedBlock)) {
				places.setText(placeConstraints.get(selectedBlock));
			} else {
				places.setText("");
			}
		}
		selectedIndex = -1;
		selectedID = null;
	}

	/**
	 * Invoked whenever a Module from moduleCombo is selected. TODO: was jetzt
	 * damit machen?
	 */
	private void moduleSelected() {
		selectedIndex = moduleCombo.getSelectionIndex();
		selectedID = idList.get(selectedIndex);
	}

	private void saveConstraints() {
		String text = places.getText();
		if (text.isEmpty()){
			placeConstraints.remove(selectedBlock);
		}
		else{
			placeConstraints.put(selectedBlock, text);
		}
		selectedItem.setText(2, text);
		
		if (selectedID != null && selectedIndex > -1){
			moduleConstraints.put(selectedBlock, selectedID);
			String module = moduleCombo.getItem(selectedIndex);
			selectedItem.setText(1, module);
		}
		else{
			selectedItem.setText(1, "(no module assigned)");
			moduleConstraints.remove(selectedBlock);
		}
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
			LOGGER.trace("found combo entry for id {}", id);
		} else {
			LOGGER.debug("trying to remove nonexistant id {}", id);
		}
		LOGGER.exit();
	}

	/**
	 * Tries to load function blocks from input.
	 * 
	 * @param input
	 *            the input of the editor
	 */
	private void loadBlocks(IEditorInput input) {
		if (input instanceof FileEditorInput) {
			try {
				functionBlocks = loadInput((FileEditorInput) input);
			} catch (IOException e) {
				LOGGER.catching(e);
			} catch (InvalidFunctionBlockException e) {
				LOGGER.catching(e);
			}
		} else {
			LOGGER.error("Input is not a FileEditorInput {}", input);
		}
		for (FunctionBlock block : functionBlocks) {
			TableItem item = new TableItem(deployment, SWT.NONE);
			item.setText(0, block.getType());
			item.setText(1, "(no module assigned yet)");
			item.setText(3, "(not deployed yet)");
			mapItemToBlock.put(item, block);
		}
	}

	/**
	 * Loads the given data flow graph. The file given in the editor input must
	 * be a valid graph. It is loaded and converted into actual FunctionBlocks.
	 * 
	 * @param input
	 *            the input of the editor
	 * @return a collection of FunctionBlocks that were defined in the model
	 * @throws IOException
	 *             if reading fails
	 * @throws InvalidFunctionBlockException
	 *             if converting the model into actual FunctionBlocks fails
	 */
	private Collection<FunctionBlock> loadInput(final FileEditorInput input)
			throws IOException, InvalidFunctionBlockException {
		LOGGER.entry(input);
		Collection<FunctionBlock> blockList = new ArrayList<FunctionBlock>();
		appName.setText(input.getFile().getName().replaceAll("\\.diagram", ""));

		Set<IPath> paths = EclipseUtil.getAbsoluteBinPaths(input.getFile()
				.getProject());
		LOGGER.debug("using paths {}", paths);
		URL[] urls = new URL[paths.size()];
		String[] classpath = new String[paths.size()];
		int i = 0;
		for (IPath path : paths) {
			urls[i] = path.toFile().toURI().toURL();
			classpath[i] = path.toFile().getPath();
			i++;
		}
		URLClassLoader classLoader = new URLClassLoader(urls, getClass()
				.getClassLoader());
		URI uri = URI.createURI(input.getURI().toASCIIString());
		Resource resource = new XMIResourceImpl(uri);
		resource.load(null);
		for (EObject object : resource.getContents()) {
			if (object instanceof FunctionBlockModel) {
				LOGGER.trace("found FunctionBlock {}", object);
				blockList.add(((FunctionBlockModel) object)
						.createBlock(classLoader));
			}
		}
		return blockList;
	}

	/**
	 * Opens a warning window with the given message.
	 * 
	 * @param message
	 *            Warning message
	 */
	private void warn(String message) {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		dialog.setText("Warning");
		dialog.setMessage(message);
		dialog.open();
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
	 * Returns table item representing a given function block.
	 * @param block The block to find in the table
	 * @return item holding the block
	 */
	private TableItem getItem(FunctionBlock block) {
		UUID id = block.getID();
		for (TableItem i : mapItemToBlock.keySet()) {
			if (mapItemToBlock.get(i).getID() == id) {
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
	public void dispose(){
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
						for (FunctionBlock block : moduleConstraints.keySet()) {
							// Setze Text neu.
							getItem(block).setText(1, text);
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
				updateButton.setEnabled(true);
				createButton.setEnabled(true);
				deployButton.setEnabled(true);

				synchronized (ViewDeploy.this) {
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
				updateButton.setEnabled(false);
				createButton.setEnabled(false);
				deployButton.setEnabled(false);
				synchronized (ViewDeploy.this) {
					while (!idList.isEmpty()) {
						removeID(idList.get(0)); // TODO: Unschön, aber geht
													// hoffentlich?
					}
				}
			}
		});
	}

}
