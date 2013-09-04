package edu.teco.dnd.eclipse.appView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.discover.ApplicationInformation;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.server.ApplicationManager;
import edu.teco.dnd.server.ApplicationManagerListener;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ServerManager;

/**
 * View for the applications / running function blocks.
 * 
 * @author jung
 * 
 */
public class AppView extends ViewPart implements ApplicationManagerListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AppView.class);

	/**
	 * Horizontal space between cells.
	 */
	public static final int HORIZONTAL_SPACE = 10;

	/**
	 * Vertical space between cells.
	 */
	public static final int VERTICAL_SPACE = 5;

	public static final String SORT_APP = "Sort by Apps";

	public static final String SORT_MOD = "Sort by Modules";

	/**
	 * Indicates that the applications are currently sorted by themselves.
	 */
	public static final int SORTED_BY_APPS = 0;

	/**
	 * Indicates that the applications are currently sorted by modules.
	 */
	public static final int SORTED_BY_MODULES = 1;

	/**
	 * Index of the Application column in the appTable.
	 */
	public static final int APP_INDEX = 0;

	/**
	 * Index of the UUID column in the appTable.
	 */
	public static final int UUID_INDEX = 1;

	private Composite parent;

	private Label selectedLabel;
	private Label selectedInfoLabel;
	private Button updateAppsButton;
	private Button killAppButton;
	private Button sortButton; // Sort by App or Module
	private Table appTable;
	private Table blockTable;

	private Display display;
	private ApplicationManager appManager;

	private Map<TableItem, ApplicationInformation> itemToApp;
	private Map<ApplicationInformation, TableItem> appToItem;
	private ApplicationInformation selectedApp;
	private int sorted;

	// Used to map FunctionBlocks on table items
	// private Map<UUID, TableItem> map = new HashMap<UUID, TableItem>();

	/**
	 * Creates a new Appview.
	 */
	public AppView() {
		super();
	}

	public void setFocus() {
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		LOGGER.entry(site, memento);
		super.init(site, memento);
		display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace("Display.getCurrent() returned null, using Display.getDefault(): {}", display);
		}
		appManager = ServerManager.getDefault().getApplicationManager();
		appManager.addApplicationListener(this);

		itemToApp = new HashMap<TableItem, ApplicationInformation>();
		appToItem = new HashMap<ApplicationInformation, TableItem>();
		sorted = SORTED_BY_APPS;
		LOGGER.exit();
	}

	@Override
	public void dispose() {
		appManager.removeApplicationListener(this);
	}

	public void createPartControl(Composite parent) {
		initParent(parent);

		createUpdateAppsButton();
		createKillAppButton();
		createSortButton();
		createSelectedLabel();
		createSelectedInfoLabel();
		createAppTable();
		createBlockTable();
	}

	private void initParent(final Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		layout.horizontalSpacing = HORIZONTAL_SPACE;
		layout.verticalSpacing = VERTICAL_SPACE;
		this.parent.setLayout(layout);
	}

	private void createUpdateAppsButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateAppsButton = new Button(parent, SWT.NONE);
		updateAppsButton.setLayoutData(data);
		updateAppsButton.setText("Update Applications");
		updateAppsButton.setEnabled(ServerManager.getDefault().isRunning());
		updateAppsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppView.this.updateAppList();
			}
		});
	}

	private void createKillAppButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		killAppButton = new Button(parent, SWT.NONE);
		killAppButton.setLayoutData(data);
		killAppButton.setText("Kill Application");
		killAppButton.setEnabled(false);
		killAppButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppView.this.killApp();
			}
		});
	}

	private void createSortButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		sortButton = new Button(parent, SWT.NONE);
		sortButton.setText(SORT_MOD);
		sortButton.setEnabled(false);
		sortButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppView.this.sort();
			}
		});
	}

	private void createSelectedLabel() {
		selectedLabel = new Label(parent, SWT.NONE);
		selectedLabel.setText("Application:");
	}

	private void createSelectedInfoLabel() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		selectedInfoLabel = new Label(parent, SWT.NONE);
		selectedInfoLabel.setText("<select application>");
	}

	private void createAppTable() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		appTable = new Table(parent, SWT.NONE);
		appTable.setHeaderVisible(true);
		appTable.setLinesVisible(true);
		appTable.setLayoutData(data);
		TableColumn column0 = new TableColumn(appTable, SWT.NONE);
		column0.setText("Applications:");
		appTable.getColumn(APP_INDEX).pack();
		TableColumn column1 = new TableColumn(appTable, SWT.NONE);
		column1.setText("UUID:");
		appTable.getColumn(UUID_INDEX).pack();

		appTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppView.this.appSelected();
			}
		});
	}

	private void createBlockTable() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;

		blockTable = new Table(parent, 0);
		blockTable.setLayoutData(data);
		blockTable.setLinesVisible(true);
		blockTable.setHeaderVisible(true);

		TableColumn column1 = new TableColumn(blockTable, SWT.NONE);
		column1.setText("Running Function Blocks");
		blockTable.setToolTipText("Currently running function blocks");
		blockTable.getColumn(0).pack();

		TableColumn column2 = new TableColumn(blockTable, SWT.NONE);
		column2.setText("On Module:");
		blockTable.getColumn(1).pack();
	}

	/**
	 * Updates Information on which applications are running on a module.
	 */
	private synchronized void updateAppList() {
		clearMaps();
		appTable.removeAll();
		blockTable.removeAll();
		appManager.updateAppInfo();
		sorted = SORTED_BY_APPS;
		sortButton.setText(SORT_MOD);
	}

	/**
	 * Kills an Application.
	 */
	private void killApp() {
		if (selectedApp != null) {
			removeApp(selectedApp);
			selectedApp = null;
			blockTable.removeAll();
			// TODO: Kill app, remove from table. update?
		}
	}

	private void sort() {
		if (sorted == SORTED_BY_APPS) {
			sortButton.setText(SORT_APP);
			sorted = SORTED_BY_MODULES;
			Map<UUID, Collection<ApplicationInformation>> maap = appManager.getModulesToApps();
			for (UUID id : maap.keySet()) {
				Map<UUID, Module> idToModule = ServerManager.getDefault().getModuleManager().getMap();
				if (idToModule.containsKey(id)){
					System.out.println("Module: " + idToModule.get(id).getName());
				}
				else{
					System.out.println("Module: ");
				}
				for (ApplicationInformation info : maap.get(id)) {
					System.out.println(info.getName());
				}
			}
		} else if (sorted == SORTED_BY_MODULES) {
			sorted = SORTED_BY_APPS;
			sortButton.setText(SORT_MOD);
		}

		// TODO: Sort by Application / Module.
		/**
		 * If sorted by app: Table on the left displays all applications. If one selected: show modules and Blocks. If
		 * sorted by module: Table on the left displays all modules. If one selected: show all apps and Blocks.
		 */
	}

	private void appSelected() {
		blockTable.removeAll();

		TableItem[] items = appTable.getSelection();
		if (items.length == 1) {
			TableItem selectedItem = items[0];
			selectedApp = itemToApp.get(selectedItem);
			selectedInfoLabel.setText(selectedApp.getName());
			ModuleManager m = ServerManager.getDefault().getModuleManager();
			Map<UUID, Module> uuidToModule = m.getMap();

			Map<UUID, Collection<UUID>> modToBlocks = selectedApp.getBlocksRunningOn();

			for (UUID module : modToBlocks.keySet()) {
				String moduleText = module.toString();
				if (uuidToModule.containsKey(module)) {
					moduleText = uuidToModule.get(module).getName().concat(" : " + moduleText);
				}
				for (UUID block : modToBlocks.get(module)) {
					TableItem item = new TableItem(blockTable, SWT.NONE);
					item.setText(
							0,
							selectedApp.getBlockType(block) == null ? block.toString() : selectedApp
									.getBlockType(block));
					item.setText(1, moduleText);
				}
			}

			// TODO: Resolve Block UUIDs to Function Blocks to be able to access their names - or is the type
			// sufficient?

			killAppButton.setEnabled(true);
		}
	}

	@Override
	public void serverOnline() {
		LOGGER.entry();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (updateAppsButton != null) {
					updateAppsButton.setEnabled(true);
					sortButton.setEnabled(true);
					updateAppList();
				}
			}
		});
		LOGGER.exit();
	}

	@Override
	public void serverOffline() {
		LOGGER.entry();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				killAppButton.setEnabled(false);
				updateAppsButton.setEnabled(false);
				sortButton.setEnabled(false);
				appToItem.clear();
				itemToApp.clear();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void applicationsResolved(final Collection<ApplicationInformation> apps) {
		LOGGER.entry();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ApplicationInformation app : apps) {
					TableItem item = new TableItem(appTable, SWT.NONE);
					addAppAndItem(app, item);
					item.setText(APP_INDEX, app.getName());
					item.setText(UUID_INDEX, app.getAppId().toString());
				}
			}
		});
		LOGGER.exit();
	}

	private void addAppAndItem(ApplicationInformation app, TableItem item) {
		appToItem.put(app, item);
		itemToApp.put(item, app);
	}

	private void removeApp(ApplicationInformation app) {
		itemToApp.remove(appToItem.get(app));
		appToItem.remove(app);
	}

	private void clearMaps() {
		appToItem.clear();
		itemToApp.clear();
	}
}
