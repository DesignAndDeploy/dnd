package edu.teco.dnd.eclipse;

import java.util.ArrayList;
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

import edu.teco.dnd.module.Module;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ModuleManagerListener;
import edu.teco.dnd.server.ServerManager;

/**
 * ModuleView: Shows available modules, Start / Stop Server.
 * 
 * @author jung
 * 
 */
public class ModuleView extends ViewPart implements ModuleManagerListener {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleView.class);

	private Composite parent;
	private Button button;
	private Label serverStatus;
	private Table moduleTable;

	private ServerManager serverManager;
	private Activator activator;
	private ModuleManager manager;
	private Map<UUID, TableItem> map = new HashMap<UUID, TableItem>();

	private Display display;

	public ModuleView() {
		super();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		LOGGER.entry(site, memento);
		super.init(site, memento);
		activator = Activator.getDefault();
		serverManager = ServerManager.getDefault();
		display = Display.getCurrent();
		manager = serverManager.getModuleManager();
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace("Display.getCurrent() returned null, using Display.getDefault(): {}", display); //$NON-NLS-1$
		}
		manager.addModuleManagerListener(this);
		LOGGER.exit();
	}

	@Override
	public void dispose() {
		manager.removeModuleManagerListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.parent.setLayout(layout);

		createStartButton();
		createServerInfo();
		createModuleTable();
	}

	/**
	 * Creates a Button that starts the Server when pressed
	 * 
	 * @param parent
	 *            Composite containing the button
	 */
	private void createStartButton() {
		button = new Button(parent, SWT.NONE);
		if (serverManager.isRunning()) {
			button.setText(Messages.ModuleView_STOP_SERVER);
		} else {
			button.setText(Messages.ModuleView_START_SERVER);
		}
		button.setToolTipText(Messages.ModuleView_START_SERVER_TOOLTIP);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ServerManager.getDefault().isRunning()) {
					ModuleView.this.serverStatus.setText(Messages.ModuleView_STOPPING_SERVER);
					ModuleView.this.activator.shutdownServer();
				} else {
					ModuleView.this.serverStatus.setText(Messages.ModuleView_STARTING_SERVER);
					ModuleView.this.activator.startServer();
				}
			}
		});

	}

	private void createServerInfo() {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.horizontalAlignment = GridData.FILL;

		serverStatus = new Label(parent, 0);
		if (serverManager.isRunning()) {
			serverStatus.setText(Messages.ModuleView_SERVER_RUNNING);
		} else {
			serverStatus.setText(Messages.ModuleView_SERVER_DOWN);
		}
		serverStatus.setLayoutData(gridData);
	}

	/**
	 * Creates a Table containing currently available modules.
	 * 
	 * @param parent
	 *            Composite containing the table
	 */
	private void createModuleTable() {
		GridData grid = new GridData();
		grid.horizontalSpan = 2;
		grid.verticalAlignment = GridData.FILL;
		grid.horizontalAlignment = GridData.FILL;
		grid.grabExcessHorizontalSpace = true;
		grid.grabExcessVerticalSpace = true;

		moduleTable = new Table(parent, 0);
		moduleTable.setLinesVisible(true);
		moduleTable.setHeaderVisible(true);
		moduleTable.setLayoutData(grid);

		TableColumn column1 = new TableColumn(moduleTable, SWT.None);
		column1.setText(Messages.ModuleView_MODULE_ID);
		TableColumn column2 = new TableColumn(moduleTable, SWT.None);
		column2.setText(Messages.ModuleView_MODULE_NAME);
		TableColumn column3 = new TableColumn(moduleTable, SWT.None);
		column3.setText(Messages.ModuleView_LOCATION);
		moduleTable.setToolTipText(Messages.ModuleView_CURRENTLY_AVAILABLE_MODULES);
		/**
		 * Collection<UUID> modules = getModules();
		 * 
		 * for (UUID moduleID : modules) { addID(moduleID); }
		 **/
		moduleTable.getColumn(0).pack();
		moduleTable.getColumn(1).pack();
		moduleTable.getColumn(2).pack();
	}

	/**
	 * Adds a Module ID to the table.
	 * 
	 * @param id
	 *            the ID to add
	 */
	private synchronized void addID(final UUID id) {
		LOGGER.entry(id);
		if (!map.containsKey(id)) {
			LOGGER.trace("id {} is new, adding", id); //$NON-NLS-1$
			TableItem item = new TableItem(moduleTable, SWT.NONE);
			item.setText(0, id.toString());
			map.put(id, item);

		} else {
			LOGGER.debug("trying to add existing id {}", id); //$NON-NLS-1$
		}
		LOGGER.exit();
	}

	/**
	 * Removes a Module ID from the table.
	 * 
	 * @param id
	 *            the ID to remove
	 */
	private synchronized void removeID(final UUID id) {
		LOGGER.entry(id);
		TableItem item = map.get(id);
		if (item != null) {
			LOGGER.trace("found item {} for id {}", item, id); //$NON-NLS-1$
			moduleTable.remove(moduleTable.indexOf(item));
			map.remove(id);
		} else {
			LOGGER.debug("trying to remove nonexistant id {}", id); //$NON-NLS-1$
		}
		LOGGER.exit();
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

	@Override
	public void moduleOffline(final UUID id, Module module) {
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

				if (!map.containsKey(id)) {
					addID(id);
				}

				TableItem item = map.get(id);
				if (module.getName() != null) {
					item.setText(1, module.getName());
				}
				if (module.getLocation() != null) {
					item.setText(2, module.getLocation());
				}
			}
		});
	}

	@Override
	public void serverOnline(final Map<UUID, Module> modules) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (serverStatus != null && button != null) {
					serverStatus.setText(Messages.ModuleView_SERVER_RUNNING); //$NON-NLS-1$
					button.setText(Messages.ModuleView_STOP_SERVER); //$NON-NLS-1$
				}

				synchronized (ModuleView.this) {
					for (UUID id : new ArrayList<UUID>(map.keySet())) {
						removeID(id);
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
				synchronized (ModuleView.this) {
					for (UUID id : new ArrayList<UUID>(map.keySet())) {
						removeID(id);
					}
				}
				if (serverStatus != null && button != null) {
					serverStatus.setText(Messages.ModuleView_SERVER_DOWN); //$NON-NLS-1$
					button.setText(Messages.ModuleView_START_SERVER); //$NON-NLS-1$
				}
			}
		});
	}

}