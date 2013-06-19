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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.deploy.DistributionGenerator;
import edu.teco.dnd.deploy.MinimalModuleCountEvaluator;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;

/**
 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung anwählen -
 * Verteilungsalgorithmus auswählen - Fest im Code einbinden? - Verteilung
 * erstellen lassen und anzeigen - Verteilung bestätigen
 * 
 */
public class DeployView extends EditorPart implements ConnectionListener,
		DNDServerStateListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleView.class);
	private Display display;
	private Activator activator;
	private ConnectionManager manager;

	private Collection<Module> modules;
	private Collection<FunctionBlock> functionBlocks;

	private Map<FunctionBlock, BlockTarget> map;

	private Button createButton; // Button to create deployment
	private Button deployButton; // Button to deploy deployment
	private Button updateButton; // Button to update moduleCombo
	private Label appName;
	private Label blockSpecifications;
	private Label block; // Block to edit specifications
	private Label module;
	private Label place;
	private Combo moduleCombo;
	private Combo places;
	private Table deployment; // Table to show blocks and current deployment

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		parent.setLayout(layout);

		modules = new ArrayList<Module>();
		functionBlocks = new ArrayList<FunctionBlock>();

		appName = new Label(parent, SWT.NONE);
		appName.pack();
		
		loadBlocks(getEditorInput());
		
		createUpdateButton(parent);
		createBlockSpecsLabel(parent);
		createDeploymentTable(parent);
		createCreateButton(parent);
		createBlockLabel(parent);
		createDeployButton(parent);
		createModuleLabel(parent);
		createmoduleComboCombo(parent);
		createPlaceLabel(parent);
		createPlacesCombo(parent);

		TableItem item2 = new TableItem(deployment, SWT.NONE);
		item2.setText(0, "FunctionBlocks");
		item2.setText(1, "moduleCombo");

		TableItem item3 = new TableItem(deployment, SWT.NONE);
		item3.setText(0, "another Block");
		item3.setText(1, "");
		item3.setText(2, "heater");
	}

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
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace(
					"Display.getCurrent() returned null, using Display.getDefault(): {}",
					display);
		}
		activator.addServerStateListener(this);
		LOGGER.exit();
		map = new HashMap<FunctionBlock, BlockTarget>();
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

		TableColumn column = new TableColumn(deployment, SWT.None);
		column.setText("Function Block");
		TableColumn column2 = new TableColumn(deployment, SWT.NONE);
		column2.setText("Module");
		TableColumn column3 = new TableColumn(deployment, SWT.NONE);
		column3.setText("place");
		deployment.getColumn(0).pack();
		deployment.getColumn(1).pack();
		deployment.getColumn(2).pack();

		deployment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = DeployView.this.deployment.getSelection();
				if (items.length == 1) {
					DeployView.this.block.setText(items[0].getText());
				}
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
				DeployView.this.update();
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
				DeployView.this.create();
			}
		});
	}

	private void createBlockLabel(Composite parent) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		block = new Label(parent, SWT.NONE);
		block.setText("(select block on the left)");
		block.setLayoutData(data);
		block.pack();
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
				DeployView.this.deploy();
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

	private void createmoduleComboCombo(Composite parent) {
		moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.add("Modul 1");
		moduleCombo.add("Modul 3");
	}

	private void createPlaceLabel(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		place = new Label(parent, SWT.NONE);
		place.setLayoutData(data);
		place.setText("Place:");
		place.setToolTipText("Select a place for this function block");
		place.pack();
	}

	private void createPlacesCombo(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		places = new Combo(parent, SWT.NONE);
		places.setLayoutData(data);
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

		moduleCombo.removeAll();
		places.removeAll();
		for (Module m : modules) {
			moduleCombo.add(m.getName());
			places.add(m.getLocation());
		}
	}

	/**
	 * Invoked whenever the Create Button is pressed.
	 */
	private void create() {
		if (functionBlocks.isEmpty() || modules.isEmpty()) {
			warn("No blocks / modules to distribute");
			return;
		}
		DistributionGenerator generator = new DistributionGenerator(
				new MinimalModuleCountEvaluator(),
				Collections.<Constraint> emptyList());
		Distribution dist = generator.getDistribution(functionBlocks, modules);
		if (dist.getMapping() == null) {
			warn("No valid deployment exists");
		} else {
			map = dist.getMapping();
			deployment.clearAll();
			for (FunctionBlock block : map.keySet()) {
				TableItem item = new TableItem(deployment, SWT.NONE);
				item.setText(0, block.getType());
				item.setText(1, map.get(block).getModule().getName());
				item.setText(2, map.get(block).getModule().getLocation());
			}
		}
	}

	/**
	 * Invoked whenever the Deploy Button is pressed.
	 */
	private void deploy() {
		if (map.isEmpty()) {
			warn("No deployment created yet");
			return;
		}
		// TODO: deploy map.
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
//		for (EObject object : resource.getContents()) {
//			if (object instanceof FunctionBlockModel) {
//				blockList.add(((FunctionBlockModel) object)
//						.createBlock(classLoader));
//			}
//		}
		return blockList;
	}

	@Override
	public void connectionEstablished(UUID uuid) {

		manager = activator.getConnectionManager();
		if (manager == null) {
			return;
		}
		/**
		 * TODO: - Modul zur moduleCombo hinzufügen - Modul zur ModuleCollection
		 * hinzufügen - ggf Ort des Moduls zur placesCombo hinzufügen
		 */
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionClosed(UUID uuid) {
		/**
		 * TODO: - Prüfen, ob Modul in moduleCombo - Falls ja: löschen - Prüfen
		 * ob Modul in ModulCollection: - falls ja: löschen - ggf Ort löschen,
		 * falls kein anderes Modul den Ort hat.
		 */

		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarted(ConnectionManager connectionManager,
			UDPMulticastBeacon beacon) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopped() {
		// TODO Auto-generated method stub

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

}
