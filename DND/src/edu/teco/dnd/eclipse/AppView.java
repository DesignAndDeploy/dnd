package edu.teco.dnd.eclipse;


import java.util.Collection;

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
import edu.teco.dnd.server.ApplicationManager;
import edu.teco.dnd.server.ApplicationManagerListener;
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
	
	private Composite parent;

	private Label selectedLabel;
	private Button updateAppsButton;
	private Button killAppButton;
	private Button sortButton; //Sort by App or Module
	private Table appTable;
	private Table blockTable;
	
	private Display display;
	private ApplicationManager appManager;
	
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
		LOGGER.exit();
	}
	
	@Override
	public void dispose(){
		appManager.removeApplicationListener(this);
	}
	
	public void createPartControl(Composite parent) {
		initParent(parent);

		createUpdateAppsButton();
		createKillAppButton();
		createSortButton();
		createAppTable();
		createBlockTable();
	}

	private void initParent(final Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.horizontalSpacing = HORIZONTAL_SPACE;
		layout.verticalSpacing = VERTICAL_SPACE;
		this.parent.setLayout(layout);
	}

	private void createUpdateAppsButton(){
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateAppsButton = new Button(parent, SWT.NONE);
		updateAppsButton.setLayoutData(data);
		updateAppsButton.setText("Update Applications");
		updateAppsButton.setEnabled(ServerManager.getDefault().isRunning());
		updateAppsButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				AppView.this.updateAppList();
			}
		});
	}
	
	private void createKillAppButton(){
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
	
	private void createSortButton(){
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		sortButton = new Button(parent, SWT.NONE);
		sortButton.setText(SORT_APP);
		sortButton.setEnabled(false);
		sortButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				AppView.this.sort();
			}
		});
	}
	
	private void createAppTable(){
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
		appTable.getColumn(0).pack();
		
		appTable.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				AppView.this.appSelected();
			}
		});
		
	}

	private void createBlockTable() {
		GridData data = new GridData();
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
	 * Updates list of currently running applications.
	 */
	/**
	 * Updates Information on which applications are running on a module.
	 * @return
	 */
	private synchronized void updateAppList(){
		//TODO: Remove outdated apps.
		appManager.updateAppInfo();
	}

	
	/**
	 * Kills an Application.
	 */
	private void killApp(){
		//TODO: Kill app, remove from table. update?
	}
	
	private void sort(){
		if (SORT_APP.equals(sortButton.getText())){
			sortButton.setText(SORT_MOD);
		}
		else if (SORT_MOD.equals(sortButton.getText())){
			sortButton.setText(SORT_APP);
		}
		//TODO: Sort by Application / Module.
		/**
		 * If sorted by app: Table on the left displays all applications. If one selected: show modules and Blocks.
		 * If sorted by module: Table on the left displays all modules. If one selected: show all apps and Blocks.
		 */
	}
	
	private void appSelected(){
		TableItem[] items = appTable.getSelection();
		if (items.length == 1){
			TableItem selectedItem = items[0];
			selectedLabel.setText(selectedItem.getText(0));
		}
		killAppButton.setEnabled(true);
	}

	@Override
	public void serverOnline() {
		LOGGER.entry();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				updateAppsButton.setEnabled(true);
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
			}
		});
		LOGGER.exit();
	}

	@Override
	public void applicationsResolved(Collection<ApplicationInformation> apps) {
		System.out.println("bla");
		for (ApplicationInformation info : apps){
			System.out.println(info.getName());
		}
		//TODO: Display changes.
		
	}

}
