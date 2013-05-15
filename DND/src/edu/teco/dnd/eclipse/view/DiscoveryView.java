package edu.teco.dnd.eclipse.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lime.AgentID;

import edu.teco.dnd.discover.Discover;
import edu.teco.dnd.discover.DiscoverListener;
import edu.teco.dnd.discover.KillAgent;
import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.module.Module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * This class is a view, that can be shown in eclipse as a plug-in. It has UI elements to discover modules and
 * applications in the network and display information about them.
 */
public class DiscoveryView extends ViewPart implements DiscoverListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.teco.dnd.eclipse.views.DiscoveryView";

	/**
	 * Logger for this class.
	 */
	private static Logger LOGGER = LogManager.getLogger(DiscoveryView.class);

	/**
	 * IP address that is used by current server.
	 */
	private String usedIPAddress = "";

	/** Stores all discovered modules */
	private static Map<AgentID, Module> modules = new HashMap<>();
	/** Stores all discovered applications */
	private static Map<Integer, String> applications = new HashMap<>();

	/* begin UI elements */
	private Composite parentView;

	private Composite upperComposite;
	private Button discoverButton;
	private Label errorOnDiscoveryNotificationLabel;
	private Combo ipDropDown;
	private Label ipAddressLabel;
	private Label restartEclipseNotificationLabel;

	private TabFolder lowerComposite;
	// tab for modules
	private TabItem modulesTab;
	private Composite modulesTabComposite;

	private Table modulesTable;
	private TableColumn moduleNameColumn;
	private TableColumn moduleLocationColumn;
	private TableColumn moduleAgentIDColumn;

	private Table isRunningApplicationsTable;
	private TableColumn isRunningApplicationNameColumn;
	private TableColumn isRunningApplicationIDColumn;

	// tab for applications
	private TabItem applicationsTab;
	private Composite applicationTabComposite;

	private Table applicationTable;
	private TableColumn applicationNameColumn;
	private TableColumn applicationIDColumn;
	private TableColumn applicationLocationColumn;

	private Table runningOnModulesTable;
	private TableColumn runningLocationColumn;
	private TableColumn runningModuleNameColumn;

	/** A map that relates running applications to their modules */
	private Map<Integer, Set<AgentID>> appToModuleMap = new HashMap<>();

	/** A map that relates modules to their running applications */
	private Map<AgentID, Set<Integer>> modulesToAppMap = new HashMap<>();

	/* end UI elements */

	/**
	 * Internal class that starts discovery.
	 */
	class DiscoverStarter implements SelectionListener {
		/**
		 * Redirects to {@link #startDiscovery(SelectionEvent)}
		 * 
		 * @param arg0
		 *            unused
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			startDiscovery(false);
		}

		/**
		 * Redirects to {@link #startDiscovery(SelectionEvent)}
		 * 
		 * @param arg0
		 *            unused
		 */
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			startDiscovery(false);
		}

		/**
		 * Starts discovery.
		 * 
		 * @param onlyApplications
		 *            whether to only scan for applications
		 */
		private void startDiscovery(boolean onlyApplications) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Discover button pressed.");
			}
			if (!onlyApplications) {
				modulesTable.removeAll();
				modulesTable.redraw();
			}
			applicationTable.removeAll();
			applicationTable.redraw();
			Discover discover = Discover.getSingleton();
			if (discover == null) {
				LOGGER.warn("Could not get Discover");
				errorOnDiscoveryNotificationLabel.setText(Messages.DiscoveryView_ErrorOnDiscover_Label);
				errorOnDiscoveryNotificationLabel.redraw();
				upperComposite.layout();
				upperComposite.redraw();
				return;
			}
			if (!onlyApplications) {
				LOGGER.debug("starting module discovery");
				discover.startModuleDiscovery();
			}

			LOGGER.debug("starting application discovery");
			discover.startApplicationDiscovery();
		}

		public void startApplicationDiscovery() {
			startDiscovery(true);
		}

	}

	/**
	 * Stores the selected IP in the preferences.
	 */
	class IPSelection implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent arg0) {
			if (arg0 == null) {
				LOGGER.warn("Got null as an ModifyEvent (UI bug)");
			} else if (!(arg0.widget instanceof Combo)) {
				LOGGER.warn("IPSelection can (currently) only be applied to Combo boxes.");
			} else {
				String selection = ((Combo) arg0.widget).getText();

				Activator.storeIPAddress(selection);
				if (!selection.equals(usedIPAddress)) {
					restartEclipseNotificationLabel.setText(Messages.DiscoveryView_RestartEclipse_Info);
					restartEclipseNotificationLabel.pack();
					upperComposite.pack();
					upperComposite.layout();
				} else {
					restartEclipseNotificationLabel.setText("");
					restartEclipseNotificationLabel.pack();
					upperComposite.pack();
					upperComposite.layout();
				}

			}
		}
	}

	abstract class GeneralTableListener implements SelectionListener, MouseListener {
		Integer id = Integer.MIN_VALUE;
		TableItem selection;
		Table table;
		boolean doubleclick = false;

		abstract void listen();

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			if (!doubleclick) {
				if (selected(arg0) != Integer.MIN_VALUE) {
					listen();
				}
			}
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (!doubleclick) {
				if (selected(arg0) != Integer.MIN_VALUE) {
					listen();
				}
			}
		}

		@Override
		public void mouseDoubleClick(MouseEvent arg0) {
			if (doubleclick) {
				if (selected(arg0) != Integer.MIN_VALUE) {
					listen();
				}
			}
		}

		@Override
		public void mouseDown(MouseEvent arg0) {
			// ignore
		}

		@Override
		public void mouseUp(MouseEvent arg0) {
			if (!doubleclick) {
				if (selected(arg0) != Integer.MIN_VALUE) {
					listen();
				}
			}
		}

		private Integer selected(TypedEvent arg0) {
			if (arg0 == null) {
				LOGGER.error("Event is null in {}", this.getClass().toString());
			} else if (!(arg0.widget instanceof Table)) {
				LOGGER.error("Event for non-Table in {}", this.getClass().toString());
			} else {
				table = (Table) arg0.widget;
				selection = table.getSelection()[0];
				try {
					id = Integer.valueOf(selection.getText(table.indexOf(applicationIDColumn)));
				} catch (NumberFormatException e) {
					LOGGER.warn("Table contianed incorrect application id.");
				}
			}
			return id;
		}
	}

	/**
	 * Kills application with double click.
	 */
	class KillStarter extends GeneralTableListener {
		public KillStarter() {
			doubleclick = true;
		}

		@Override
		void listen() {
			KillAgent.getSingleton().killApplication(id);
			// remove row
			table.remove(table.indexOf(selection));
			runningOnModulesTable.removeAll();
			packColumns(runningOnModulesTable.getColumns());
			runningOnModulesTable.pack();
		}
	}

	class AppModTableUpdater extends GeneralTableListener {

		@Override
		void listen() {
			if (id != Integer.MIN_VALUE) {
				runningOnModulesTable.removeAll();

				// TODO why is module null? Possibilities: 1) the AgentID doesn't exists 2) the Map modules is
				// empty
				for (AgentID id : appToModuleMap.get(this.id)) {// Integer.parseInt(selection.getText()))) {
					Module module = DiscoveryView.modules.get(id);
					if (module != null) {
						TableItem item = new TableItem(runningOnModulesTable, SWT.NONE);
						// module name
						item.setText(runningOnModulesTable.indexOf(runningModuleNameColumn), module
								.getModuleConfig().getName());
						// module location
						item.setText(runningOnModulesTable.indexOf(runningLocationColumn), module
								.getModuleConfig().getLocation());
					}
				}
				packColumns(runningOnModulesTable.getColumns());
				runningOnModulesTable.pack();
				runningOnModulesTable.layout();
				applicationTabComposite.layout();
				lowerComposite.pack();
				lowerComposite.redraw();
			}
		}
	}

	class ModAppTableUpdater extends GeneralTableListener {

		@Override
		void listen() {
			if (id != Integer.MIN_VALUE) {
				isRunningApplicationsTable.removeAll();
				// TODO: read agentID from this.table or this.selection
				AgentID agentID = null;
				for (Integer id : modulesToAppMap.get(agentID)) {
					TableItem item = new TableItem(isRunningApplicationsTable, SWT.NONE);
					// application name
					item.setText(isRunningApplicationsTable.indexOf(isRunningApplicationNameColumn),
							applications.get(id));
					// application id
					item.setText(isRunningApplicationsTable.indexOf(isRunningApplicationIDColumn), "" + id);

				}
				packColumns(isRunningApplicationsTable.getColumns());
				isRunningApplicationsTable.pack();
				isRunningApplicationsTable.layout();
				modulesTabComposite.layout();
				lowerComposite.pack();
				lowerComposite.redraw();
			}
		}
	}

	/**
	 * Creates the view.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parentView = parent;

		// memorizes IP address used on start
		usedIPAddress = Activator.getUsedIPAddress();

		createUI();

		LOGGER.debug("adding myself as discover listener");

		Discover discover = Discover.getSingleton();
		if (discover == null) {
			LOGGER.warn("could not register as discover listener");
		} else {
			discover.addListener(this);
		}
	}

	/**
	 * Creates the UI.
	 */
	private void createUI() {
		parentView.setLayout(new RowLayout(SWT.HORIZONTAL));

		/* Upper Composite */
		upperComposite = new Composite(parentView, SWT.NO_REDRAW_RESIZE);
		upperComposite.setLayout(new FillLayout());
		{
			{/* Discover button */
				discoverButton = new Button(upperComposite, SWT.PUSH);
				discoverButton.setText(Messages.DiscoveryView_Discover_BTN);
				discoverButton.setToolTipText(Messages.DiscoveryView_SearchAllModules_ToolTip);
				// Button control listener
				discoverButton.addSelectionListener(new DiscoverStarter());
			}
			{/* Error notification label */
				errorOnDiscoveryNotificationLabel = new Label(upperComposite, SWT.NONE | SWT.LEFT);
				errorOnDiscoveryNotificationLabel.setText("");
			}
			{/* IP address drop down combo */
				ipDropDown = new Combo(upperComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
				// adds all stored IPs, also the automatic choice.
				ipDropDown.add(Activator.AUTOMATIC_IP_ADDRESS);
				for (String s : Activator.getIPAddresses()) {
					ipDropDown.add(s);
				}
				ipDropDown.select(ipDropDown.indexOf(usedIPAddress));
				ipDropDown.addModifyListener(new IPSelection());
			}
			{/* Current IP address */
				ipAddressLabel = new Label(upperComposite, SWT.NONE | SWT.CENTER);
				ipAddressLabel.setText(Messages.DiscoveryView_CurrentlyUsed_Label + usedIPAddress);
			}
			{/* Restart notification label */
				restartEclipseNotificationLabel = new Label(upperComposite, SWT.WRAP | SWT.LEFT);
				restartEclipseNotificationLabel.setText("");
			}
		}

		upperComposite.layout();

		parentView.layout();

		/* Lower Composite */
		lowerComposite = new TabFolder(parentView, SWT.TOP);
		lowerComposite.setLayout(new FillLayout());
		{/* Tab for modules */
			modulesTab = new TabItem(lowerComposite, SWT.None);
			modulesTab.setText(Messages.DiscoveryView_AvailableModules_TabHead);

			modulesTabComposite = new Composite(lowerComposite, SWT.V_SCROLL);
			modulesTabComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			{// module table
				modulesTable = new Table(modulesTabComposite, SWT.BORDER | SWT.SINGLE);
				modulesTable.setHeaderVisible(true);
				modulesTable.setLayout(new FillLayout());

				// listeners
				// commented for being unable to fix the bug that exists with that
				// applicationTable.addMouseListener(new ModAppTableUpdater());

				{// name column
					moduleNameColumn = new TableColumn(modulesTable, SWT.NONE);
					moduleNameColumn.setText(Messages.DiscoveryView_Name_ColumnHead);
				}
				{// location column
					moduleLocationColumn = new TableColumn(modulesTable, SWT.NONE);
					moduleLocationColumn.setText(Messages.DiscoveryView_Location_ColumnHead);
				}
				{// AgentID column
					moduleAgentIDColumn = new TableColumn(modulesTable, SWT.NONE);
					moduleAgentIDColumn.setText(Messages.DiscoveryView_IdOfAgentModule_ColumnHead);
				}
				packColumns(modulesTable.getColumns());
				modulesTable.pack();
				modulesTable.layout();
			}
			{// application->module table (is running)
				isRunningApplicationsTable = new Table(modulesTabComposite, SWT.BORDER | SWT.SINGLE);
				isRunningApplicationsTable.setHeaderVisible(true);
				isRunningApplicationsTable.setLayout(new FillLayout());

				{// application name column
					isRunningApplicationNameColumn = new TableColumn(isRunningApplicationsTable, SWT.NONE);
					isRunningApplicationNameColumn.setText(Messages.DiscoveryView_Name_ColumnHead);
				}
				{// application location column
					// TODO fix text from location to ID
					isRunningApplicationIDColumn = new TableColumn(isRunningApplicationsTable, SWT.NONE);
					isRunningApplicationIDColumn.setText(Messages.DiscoveryView_NameOfApp_ColumnHeader);
				}

				packColumns(isRunningApplicationsTable.getColumns());
				isRunningApplicationsTable.pack();
				isRunningApplicationsTable.layout();
			}

			modulesTabComposite.layout();
			modulesTab.setControl(modulesTabComposite);
		}
		{/* Tab for applications */
			applicationsTab = new TabItem(lowerComposite, SWT.V_SCROLL);
			applicationsTab.setText(Messages.DiscoveryView_RunningApp_Label);

			applicationTabComposite = new Composite(lowerComposite, SWT.None);
			applicationTabComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			{// application table
				applicationTable = new Table(applicationTabComposite, SWT.BORDER | SWT.SINGLE);
				applicationTable.setHeaderVisible(true);
				applicationTable.setLayout(new FillLayout());

				// listeners
				applicationTable.addMouseListener(new KillStarter());
				// commented for being unable to fix the bug that exists with that
				// applicationTable.addMouseListener(new AppModTableUpdater());

				{// name column
					applicationNameColumn = new TableColumn(applicationTable, SWT.NONE);
					applicationNameColumn.setText(Messages.DiscoveryView_NameOfApp_ColumnHeader);
				}
				{// ID column
					applicationIDColumn = new TableColumn(applicationTable, SWT.NONE);
					applicationIDColumn.setText(Messages.DiscoveryView_IdOfApp_ColumnHead);
				}
				{// Location column
					applicationLocationColumn = new TableColumn(applicationTable, SWT.NONE);
					applicationLocationColumn.setText(Messages.DiscoveryView_LocationOfApp_ColumnHead);
				}

				packColumns(applicationTable.getColumns());
				applicationTable.pack();
				applicationTable.layout();
			}
			{// modules application runs on
				runningOnModulesTable = new Table(applicationTabComposite, SWT.BORDER | SWT.SINGLE);
				runningOnModulesTable.setHeaderVisible(true);
				runningOnModulesTable.setLayout(new FillLayout());
				{// module name column
					runningModuleNameColumn = new TableColumn(runningOnModulesTable, SWT.NONE);
					runningModuleNameColumn.setText(Messages.DiscoveryView_ModuleAppRunsOn_ColumnHead);
				}
				{// location column
					runningLocationColumn = new TableColumn(runningOnModulesTable, SWT.NONE);
					runningLocationColumn.setText(Messages.DiscoveryView_LocationOfModule_ColumnHead);
				}
				packColumns(runningOnModulesTable.getColumns());
				runningOnModulesTable.pack();
				runningOnModulesTable.layout();
			}
			applicationTabComposite.layout();
			applicationsTab.setControl(applicationTabComposite);
		}
		lowerComposite.layout();

		parentView.layout();
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		parentView.setFocus();
	}

	@Override
	public void modulesDiscovered(final Map<AgentID, Module> modules) {
		if (LOGGER.isDebugEnabled()) {
			for (Map.Entry<AgentID, Module> entry : modules.entrySet()) {
				LOGGER.trace("{}: {}", entry.getKey(), entry.getValue());
			}
		}
		if (modules != null) {
			DiscoveryView.modules = modules;
		} else {
			DiscoveryView.modules = new HashMap<>();
		}

		parentView.getDisplay().asyncExec(new Runnable() {
			public void run() {
				modulesTable.removeAll();
				isRunningApplicationsTable.removeAll();

				for (AgentID id : modules.keySet()) {
					TableItem item = new TableItem(modulesTable, SWT.NONE);
					item.setText(modulesTable.indexOf(moduleNameColumn), modules.get(id).getModuleConfig()
							.getName());
					item.setText(modulesTable.indexOf(moduleLocationColumn), modules.get(id)
							.getModuleConfig().getLocation());
					item.setText(modulesTable.indexOf(moduleAgentIDColumn), "" + id);
				}
				packColumns(modulesTable.getColumns());
				modulesTable.pack();
				modulesTable.layout();
				isRunningApplicationsTable.pack();
				isRunningApplicationsTable.layout();
				lowerComposite.pack();
				// lowerComposite.layout();
				lowerComposite.redraw();

				/* Workaround to update running applications when discover was performed from external */
				(new DiscoverStarter()).startApplicationDiscovery();
			}
		});
	}

	@Override
	public void applicationModulesDiscovered(final int appID, Map<AgentID, Long> modules) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.trace("For Application number {} the following modules were found", appID);
			for (Entry<AgentID, Long> entry : modules.entrySet()) {
				LOGGER.trace("{}: {}", entry.getKey(), entry.getValue());
			}
		}
		// app -> modules
		Set<AgentID> modulesFound = new HashSet<>();
		modulesFound.addAll(modules.keySet());
		appToModuleMap.put(appID, modulesFound);

		// modules -> app
		for (AgentID aID : modules.keySet()) {
			if (!modulesToAppMap.containsKey(appID)) {
				modulesToAppMap.put(aID, new HashSet<Integer>());
			}
			modulesToAppMap.get(aID).add(appID);
		}

	}

	@Override
	public void applicationsDiscovered(final Map<Integer, String> applications) {
		if (LOGGER.isDebugEnabled()) {
			for (Map.Entry<Integer, String> entry : applications.entrySet()) {
				LOGGER.debug("{}: {}", entry.getKey(), entry.getValue());
			}
		}
		if (applications != null) {
			DiscoveryView.applications = applications;
		} else {
			DiscoveryView.applications = new HashMap<>();
		}

		appToModuleMap = new HashMap<>();

		// UI stuff
		parentView.getDisplay().asyncExec(new Runnable() {
			public void run() {
				// clean application->module table
				runningOnModulesTable.removeAll();
				packColumns(runningOnModulesTable.getColumns());
				runningOnModulesTable.pack();
				runningOnModulesTable.layout();

				// clean module->application table
				isRunningApplicationsTable.removeAll();
				packColumns(isRunningApplicationsTable.getColumns());
				isRunningApplicationsTable.pack();
				isRunningApplicationsTable.layout();

				// clean and refill application table
				applicationTable.removeAll();
				for (Integer key : DiscoveryView.applications.keySet()) {
					TableItem item = new TableItem(applicationTable, SWT.NONE);
					// name
					item.setText(applicationTable.indexOf(applicationNameColumn),
							DiscoveryView.applications.get(key));
					// ID
					item.setText(applicationTable.indexOf(applicationIDColumn), "" + key);
				}
				packColumns(applicationTable.getColumns());
				applicationTable.pack();
				applicationTable.layout();
				applicationTabComposite.layout();
				// redraw
				lowerComposite.pack();
				lowerComposite.redraw();
			}
		});

		// discover all modules for each application
		Discover discover = Discover.getSingleton();
		for (Integer appID : DiscoveryView.applications.keySet()) {
			discover.startApplicationScan(appID);
		}
	}
}