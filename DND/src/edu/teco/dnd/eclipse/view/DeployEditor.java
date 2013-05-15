package edu.teco.dnd.eclipse.view;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lime.AgentID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.deploy.DeployListener;
import edu.teco.dnd.deploy.DeploymentAgent;
import edu.teco.dnd.deploy.DistributionAlgorithm;
import edu.teco.dnd.deploy.EvaluationStrategy;
import edu.teco.dnd.deploy.MaximalBlockNumberEvaluation;
import edu.teco.dnd.deploy.MinimalBlockNumberEvaluation;
import edu.teco.dnd.discover.Discover;
import edu.teco.dnd.discover.DiscoverListener;
import edu.teco.dnd.eclipse.EclipseUtil;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * This class is an editor for function block diagrams. It loads a diagram with it's function blocks and tries
 * to distribute it. The editor has UI elements to discover modules on the network, select a strategy for
 * distributing, deploying a successfully loaded application and show a distribution.
 */
public class DeployEditor extends EditorPart implements DiscoverListener, DeployListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.teco.dnd.eclipse.views.DeployEditor";

	/**
	 * Logger for this class.
	 */
	private static Logger LOGGER = LogManager.getLogger(DeployEditor.class);

	/**
	 * The strategy used to distribute.
	 */
	private EvaluationStrategy strategy = new MinimalBlockNumberEvaluation();

	/**
	 * All blocks that could be loaded from the diagram.
	 */
	private Collection<FunctionBlock> blocks = null;

	/** Holds all discovered modules */
	private Collection<Module> modules;

	/**
	 * Holds the current distribution.
	 */
	private Map<FunctionBlock, Module> distribution;

	/**
	 * Holds the name of the currently opened application.
	 */
	private String appName = null;

	/**
	 * Holds the paths of used classes
	 */
	private String[] classpath = null;

	/**
	 * Whether application was loaded.
	 */
	private boolean loaded = false;

	/* begin UI elements */
	private Composite parentView;
	// upper
	private Composite upperComposite;
	private Button discoverButton;
	private Combo strategyDropDown;
	private Button deployButton;
	private ProgressBar progressBar;
	// middle
	private Composite middleComposite;
	private Label textLabel;
	// lower
	private Composite lowerComposite;
	private Table distributionTable;
	private TableColumn blockTypeColumn;
	private TableColumn blockIDColumn;
	private TableColumn moduleNameColumn;
	private TableColumn moduleLocationColumn;

	/* end UI elements */

	/**
	 * Class for drop down menu.
	 */
	class EvaluationStrategyChanger implements ModifyListener {

		private final String STRATEGY_MAX_BLOCK_NUMBER = Messages.DeployEditor_MaxModuleStategy_Anouncement;
		private final String STRATEGY_MIN_BLOCK_NUMBER = Messages.DeployEditor_MinModulesStrategy_Anouncement;

		/**
		 * Returns all known strategies
		 * 
		 * @return all known strategies
		 */
		String[] getAllEvaluationStrategies() {
			return new String[] { STRATEGY_MIN_BLOCK_NUMBER, STRATEGY_MAX_BLOCK_NUMBER };
		}

		@Override
		public void modifyText(ModifyEvent arg0) {
			if (arg0 == null) {
				LOGGER.warn("Got null as an ModifyEvent (UI bug)");
			} else if (!(arg0.widget instanceof Combo)) {
				LOGGER.warn("EvaluationStrategyChanger can (currently) only be applied to Combo boxes.");
			} else {
				String selection = ((Combo) arg0.widget).getText();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Drop down menu: " + selection);
				}
				// set evaluation strategy
				if (STRATEGY_MAX_BLOCK_NUMBER.equals(selection)) {
					strategy = new MaximalBlockNumberEvaluation();
				} else if (STRATEGY_MIN_BLOCK_NUMBER.equals(selection)) {
					strategy = new MinimalBlockNumberEvaluation();
				} else {
					LOGGER.error("U832 |-|4X025!!1!one - drop down menu has choices that are not implemented!");
				}

			}
		}

	}

	/**
	 * Class to start deploying.
	 */
	class DeployStarter implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			selected(arg0);
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			selected(arg0);
		}

		private void selected(SelectionEvent arg0) {
			if (loaded && distribution != null && appName != null && classpath != null) {
				LOGGER.info("Deploying...");
				inform("Deploying...");
				progressBar.setState(SWT.NORMAL);
				DeploymentAgent agent = DeploymentAgent.createAgent(distribution, appName, generateAppID(),
						classpath);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Adding myself as deploy listener");
				}
				agent.addListener(DeployEditor.this);
			} else {
				LOGGER.info("Couldn't deploy application! (Maybe blocks or modules couldn't be loaded)");
				inform(Messages.DeployEditor_DeploymentFailed);
			}
		}

		private Integer generateAppID() {
			return new Random().nextInt();
		}
	}

	/**
	 * Class for discover button.
	 */
	class RediscoverStarter implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			selected(arg0);
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			selected(arg0);
		}

		private void selected(SelectionEvent arg0) {
			distributionTable.removeAll();
			if (blocks != null) {
				Discover.getSingleton().startModuleDiscovery();
			} else {
				LOGGER.info("Reload useless, diagram is corrupted.");
				inform(Messages.DeployEditor_DiagrammCorrupt);
			}
		}
	}

	/**
	 * Creates the UI elements and loads the given input diagram. Then starts module discovery, which will
	 * later on create content in table.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parentView = parent;
		parentView.setLayout(new RowLayout(SWT.VERTICAL));

		createUI();

		/* Reading input/blocks from input */
		LOGGER.info("Loading blocks...");
		inform(Messages.DeployEditor_LoadingBlocks);
		loadBlocks(getEditorInput());
		if (blocks.isEmpty()) {
			LOGGER.info("Empty Diagram loaded");
			inform(Messages.DeployEditor_EmptyDiagram);
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			for (FunctionBlock block : blocks) {
				LOGGER.debug(block);
			}
		}

		/* Starts loading modules thread */
		LOGGER.info("Discovering...");
		inform(Messages.DeployEditor_DiscoverStarter);
		discoverStarter();
	}

	/**
	 * Creates the UI elements
	 */
	private void createUI() {
		{/* Upper Composite */
			upperComposite = new Composite(parentView, SWT.NONE);
			upperComposite.setLayout(new RowLayout());

			{/* Reload button */
				discoverButton = new Button(upperComposite, SWT.PUSH);
				discoverButton.setText(Messages.DeployEditor_DiscoverButton);
				discoverButton.setToolTipText(Messages.DeployEditor_ReloadDistr_Tooltip);
				// Button control listener
				discoverButton.addSelectionListener(new RediscoverStarter());
				discoverButton.pack();
			}
			{/* Evaluation strategy drop down combo */
				strategyDropDown = new Combo(upperComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
				// adds all known evaluation startegies
				EvaluationStrategyChanger changer = new EvaluationStrategyChanger();
				for (String s : changer.getAllEvaluationStrategies()) {
					strategyDropDown.add(s);
				}
				strategyDropDown.select(strategyDropDown
						.indexOf(Messages.DeployEditor_MinModulesStrategy_Anouncement));
				strategyDropDown.addModifyListener(changer);
				strategyDropDown.pack();
			}
			{/* Deploy button */
				deployButton = new Button(upperComposite, SWT.PUSH);
				deployButton.setText(Messages.DeployEditor_Deploy_Btn);
				deployButton.setToolTipText(Messages.DeployEditor_DeployOpenDiagram_ToolTip);
				// Button control listener
				deployButton.addSelectionListener(new DeployStarter());
				deployButton.pack();
			}
			{/* Progress bar indicating progress (d'oh!) */
				progressBar = new ProgressBar(upperComposite, SWT.SMOOTH | SWT.HORIZONTAL);
				progressBar.setMinimum(0);
				// will be set by update calls
				progressBar.setMaximum(1);
				progressBar.setState(SWT.PAUSED);
				progressBar.pack();
			}
			upperComposite.layout();
		}
		{/* Middle Composite */
			middleComposite = new Composite(parentView, SWT.NO_REDRAW_RESIZE);
			middleComposite.setLayout(new FillLayout());
			{/* Label to write test output into */
				textLabel = new Label(middleComposite, SWT.SHADOW_NONE | SWT.CENTER);
				textLabel.setText("");
				textLabel.pack();
			}
			middleComposite.layout();
		}
		{/* Lower Composite */
			lowerComposite = new Composite(parentView, SWT.NO_REDRAW_RESIZE);
			lowerComposite.setLayout(new FillLayout());

			{// table for distribution strategy
				distributionTable = new Table(parentView, SWT.BORDER | SWT.SINGLE);
				distributionTable.setHeaderVisible(true);
				distributionTable.setLayout(new FillLayout());
				{// function block name
					blockIDColumn = new TableColumn(distributionTable, SWT.NONE);
					blockIDColumn.setText(Messages.DeployEditor_IdOfBlock_columnHeader);
				}
				{// function block name
					blockTypeColumn = new TableColumn(distributionTable, SWT.NONE);
					blockTypeColumn.setText(Messages.DeployEditor_TypeOfBlock_ColumnHeader);
				}
				{// Module name column
					moduleNameColumn = new TableColumn(distributionTable, SWT.NONE);
					moduleNameColumn.setText(Messages.DeployEditor_NameOfModule_ColumnHeader);
				}
				{// Module location column
					moduleLocationColumn = new TableColumn(distributionTable, SWT.NONE);
					moduleLocationColumn.setText(Messages.DeployEditor_LocationOfModule_ColumnHeader);
				}
				packColumns(distributionTable.getColumns());
				distributionTable.layout();
			}
			lowerComposite.layout();
		}
		parentView.pack();
	}

	/**
	 * Casts IEditorInput to FileEditorInput for {@link #loadInput(FileEditorInput)}. Displays respective
	 * errors.
	 * 
	 * @param input
	 *            the input of the editor
	 */
	private void loadBlocks(IEditorInput input) {
		if (input instanceof FileEditorInput) {
			try {
				blocks = loadInput((FileEditorInput) input);
			} catch (
					IOException | InvalidFunctionBlockException e) {
				LOGGER.catching(e);
				inform(Messages.DeployEditor_ErrorReadingFile_Inform);
			}
		} else {
			LOGGER.error("Input is not a FileEditorInput {}", input);
			inform(Messages.DeployEditor_UnsupportedFile_Inform);
		}
	}

	/**
	 * Loads the given data flow graph. The file given in the editor input must be a valid graph. It is loaded
	 * and converted into actual FunctionBlocks.
	 * 
	 * @param input
	 *            the input of the editor
	 * @return a collection of FunctionBlocks that were defined in the model
	 * @throws IOException
	 *             if reading fails
	 * @throws InvalidFunctionBlockException
	 *             if converting the model into actual FunctionBlocks fails
	 */
	private Collection<FunctionBlock> loadInput(final FileEditorInput input) throws IOException,
			InvalidFunctionBlockException {
		LOGGER.entry(input);
		Collection<FunctionBlock> blocks = new ArrayList<>();
		appName = input.getFile().getName();
		appName = appName.replaceAll("\\.diagram", "");

		Set<IPath> paths = EclipseUtil.getAbsoluteBinPaths(input.getFile().getProject());
		LOGGER.debug("using paths {}", paths);
		URL[] urls = new URL[paths.size()];
		classpath = new String[paths.size()];
		int i = 0;
		for (IPath path : paths) {
			urls[i] = path.toFile().toURI().toURL();
			classpath[i] = path.toFile().getPath();
			i++;
		}
		URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
		URI uri = URI.createURI(input.getURI().toASCIIString());
		Resource resource = new XMIResourceImpl(uri);
		resource.load(null);
		for (EObject object : resource.getContents()) {
			if (object instanceof FunctionBlockModel) {
				blocks.add(((FunctionBlockModel) object).createBlock(classLoader));
			}
		}

		return blocks;
	}

	/**
	 * Adds discover listener and discovers modules.
	 */
	private void discoverStarter() {
		Discover discover = Discover.getSingleton();
		LOGGER.trace("adding listener");
		discover.addListener(this);
		LOGGER.trace("starting discovery");
		discover.startModuleDiscovery();
	}

	/**
	 * Continues activities of {@link #createPartControl(Composite)} after modules where discovered.
	 */
	private void continueCreatePartControl() {
		if (modules.isEmpty()) {
			LOGGER.info("No modules found.");
			inform(Messages.DeployEditor_NoModules_Inform);
		} else {
			acquireDistribution();

			if (distribution == null) {
				LOGGER.info("Couldn't distribute blocks. (Maybe not enough modules available?)");
				inform(Messages.DeployEditor_CouldNotDistribute_Inform);
			} else {
				fillTable();
				LOGGER.info("Loaded application");
				inform(Messages.DeployEditor_LoadApp_Inform);
				loaded = true;
			}
		}
	}

	/**
	 * Acquires a distribution. Prints message, that it does.
	 */
	private void acquireDistribution() {
		LOGGER.info("Creating distribution...");
		inform(Messages.DeployEditor_CreatingDistribution_Inform);
		distribution = new DistributionAlgorithm(strategy).getDistribution(blocks, modules);
	}

	/**
	 * Fills the table with content.
	 */
	private void fillTable() {
		distributionTable.removeAll();
		for (FunctionBlock block : distribution.keySet()) {
			TableItem item = new TableItem(distributionTable, SWT.NONE);
			// ID
			item.setText(distributionTable.indexOf(blockIDColumn), block.getID());
			// type
			item.setText(distributionTable.indexOf(blockTypeColumn), block.getClass().getSimpleName());
			ModuleConfig config = distribution.get(block).getModuleConfig();
			// module name
			item.setText(distributionTable.indexOf(moduleNameColumn), config.getName());
			// module location
			item.setText(distributionTable.indexOf(moduleLocationColumn), config.getLocation());
		}
		packColumns(distributionTable.getColumns());
		distributionTable.pack();
		lowerComposite.pack();
		lowerComposite.redraw();
	}

	/**
	 * Displays the given string at the notification label.
	 * 
	 * @param s
	 *            string to display
	 */
	private void inform(String s) {
		textLabel.setText(s);
		textLabel.pack();
		textLabel.redraw();
		middleComposite.pack();
		middleComposite.redraw();
	}

	/**
	 * Packs all columns.
	 * 
	 * @param columns
	 *            columns to pack
	 */
	private void packColumns(TableColumn[] columns) {
		for (TableColumn c : columns) {
			c.pack();
		}
	}

	@Override
	public void updateDeployStatus(final int classesLoaded, final int blocksStarted) {
		LOGGER.entry(classesLoaded, blocksStarted);
		parentView.getDisplay().asyncExec(new Runnable() {
			public void run() {
				LOGGER.entry(classesLoaded, blocksStarted);
				int maximum = distribution.size();
				progressBar.setMaximum(maximum);
				if (blocksStarted >= 0 && blocksStarted <= maximum) {
					LOGGER.debug("setting {}/{}", maximum, blocksStarted);
					progressBar.setSelection(blocksStarted);
					// did deploy finish
					if (blocksStarted == maximum) {
						inform(Messages.DeployEditor_Deployed_Inform);
						progressBar.setEnabled(false);
						distributionTable.removeAll();
					}
					progressBar.redraw();
				} else {
					LOGGER.debug("Update method supported strange values: {}, {} for maximum {}",
							classesLoaded, blocksStarted, maximum);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void deployError(String message) {
		inform(Messages.DeployEditor_DeployError_Inform);
		progressBar.setState(SWT.ERROR);
		LOGGER.debug("Received a error notification while deploying.");
		inform(Messages.DeployEditor_CouldNotDeploy);
		distributionTable.removeAll();
	}

	@Override
	public void modulesDiscovered(final Map<AgentID, Module> modules) {
		if (LOGGER.isDebugEnabled()) {
			for (Map.Entry<AgentID, Module> entry : modules.entrySet()) {
				LOGGER.trace("{}: {}", entry.getKey(), entry.getValue());
			}
		}
		if (modules != null) {
			this.modules = modules.values();
		} else {
			this.modules = new HashSet<>();
		}

		parentView.getDisplay().asyncExec(new Runnable() {
			public void run() {
				distributionTable.removeAll();
				continueCreatePartControl();
			}

		});
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deploy editor initialised");
		}
		loaded = false;
		setSite(site);
		setInput(input);
	}

	/* Methods not really used, standard implementation. */

	@Override
	public void setFocus() {
		parentView.setFocus();
	}

	@Override
	public void applicationModulesDiscovered(final int appID, Map<AgentID, Long> modules) {
		// not needed
	}

	@Override
	public void applicationsDiscovered(final Map<Integer, String> applications) {
		// not needed
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// not needed
	}

	@Override
	public void doSaveAs() {
		// not allowed
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void dispose() {
		Discover.getSingleton().removeListener(this);
	}
}
