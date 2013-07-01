package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.discover.ModuleQuery;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * ModuleView: Shows available modules, Start / Stop Server.
 * 
 * @author jung
 * 
 */
public class ViewModule extends ViewPart implements ConnectionListener,
		DNDServerStateListener {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ViewModule.class);
	private ModuleQuery query;

	private Button button;
	private Label serverStatus;
	private Table moduleTable;

	private Activator activator;
	private ConnectionManager manager;
	private Map<UUID, TableItem> map = new HashMap<UUID, TableItem>();

	private Display display;

	public ViewModule() {
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
		display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace(
					"Display.getCurrent() returned null, using Display.getDefault(): {}",
					display);
		}
		activator.addServerStateListener(this);
		LOGGER.exit();
	}

	@Override
	public void dispose() {
		LOGGER.entry();
		if (manager != null) {
			manager.removeConnectionListener(this);
		}
		activator.removeServerStateListener(this);
		LOGGER.exit();
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		createStartButton(parent);
		createServerInfo(parent);
		createModuleTable(parent);
	}

	/**
	 * Creates a Button that starts the Server when pressed
	 * 
	 * @param parent
	 *            Composite containing the button
	 */
	private void createStartButton(Composite parent) {
		button = new Button(parent, SWT.NONE);
		if (activator.isRunning()) {
			button.setText("Stop Server");
		} else {
			button.setText("Start Server");
		}
		button.setToolTipText("Start / Stop the server. duh.");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					@Override
					public void run() {
						if (ViewModule.this.activator.isRunning()) {
							ViewModule.this.serverStatus
									.setText("Stopping server…");
							ViewModule.this.activator.shutdownServer();
						} else {
							ViewModule.this.serverStatus
									.setText("Starting server…");
							ViewModule.this.activator.startServer();
						}
					}
				}.run();
			}
		});

	}

	private void createServerInfo(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.horizontalAlignment = GridData.FILL;

		serverStatus = new Label(parent, 0);
		if (activator.isRunning()) {
			serverStatus.setText("Server running");
		} else {
			serverStatus.setText("Server down");
		}
		serverStatus.setLayoutData(gridData);
	}

	/**
	 * Creates a Table containing currently available modules.
	 * 
	 * @param parent
	 *            Composite containing the table
	 */
	private void createModuleTable(Composite parent) {
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

		TableColumn column = new TableColumn(moduleTable, SWT.None);
		column.setText("Available Modules");
		TableColumn column2 = new TableColumn(moduleTable, SWT.None);
		column2.setText("ModuleName");
		moduleTable.setToolTipText("Currently available modules");
		/**
		 * Collection<UUID> modules = getModules();
		 * 
		 * for (UUID moduleID : modules) { addID(moduleID); }
		 **/
		moduleTable.getColumn(0).pack();
		moduleTable.getColumn(1).pack();
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
			LOGGER.trace("id {} is new, adding", id);
			TableItem item = new TableItem(moduleTable, SWT.NONE);
			item.setText(0, id.toString());
			map.put(id, item);
			query.getModuleInfo(id).addListener(new ModuleInfoListener(item));
			
		} else {
			LOGGER.debug("trying to add existing id {}", id);
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
			LOGGER.trace("found item {} for id {}", item, id);
			moduleTable.remove(moduleTable.indexOf(item));
			map.remove(id);
		} else {
			LOGGER.debug("trying to remove nonexistant id {}", id);
		}
		LOGGER.exit();
	}

	public void connectionEstablished(final UUID id) {
		LOGGER.entry(id);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				addID(id);
			}
		});
		LOGGER.exit();
	}

	public void connectionClosed(final UUID id) {
		LOGGER.entry(id);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				removeID(id);
			}
		});
		LOGGER.exit();
	}

	/**
	 * Convenience method to return a few random numbers representing imaginary
	 * modules, used for testing
	 * 
	 * @return some random module IDs
	 */
	@SuppressWarnings("unused")
	private Collection<UUID> getModules() {
		// später: return manager.getConnectedModules(); bis dahin:
		Collection<UUID> modules = new HashSet<UUID>();
		for (int i = 0; i < 6; i++) {
			modules.add(UUID.randomUUID());
		}
		return modules;
	}

	@Override
	public void serverStarted(ConnectionManager connectionManager,
			UDPMulticastBeacon beacon) {
		System.out.println("server läuft");
		query = new ModuleQuery(connectionManager);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				serverStatus.setText("Server running");
				button.setText("Stop Server");
				manager = activator.getConnectionManager();

				synchronized (ViewModule.this) {
					for (UUID id : new ArrayList<UUID>(map.keySet())) {
						removeID(id);
					}
					manager.addConnectionListener(ViewModule.this);
					Collection<UUID> modules = manager.getConnectedModules();

					for (UUID moduleID : modules) {
						addID(moduleID);
					}
				}
			}
		});
	}

	@Override
	public void serverStopped() {
		System.out.println("Server läuft nicht mehr");

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (ViewModule.this) {
					for (UUID id : new ArrayList<UUID>(map.keySet())) {
						removeID(id);
					}
				}
				if (serverStatus != null && button != null) {
					serverStatus.setText("Server down");
					button.setText("Start Server");
				}
			}
		});
		if (manager != null) {
			manager.removeConnectionListener(this);
		}
	}

	private class ModuleInfoListener implements
			FutureListener<FutureNotifier<Module>>, Runnable {
		private TableItem item;
		private String moduleName;

		public ModuleInfoListener(TableItem item) {
			this.item = item;
		}

		@Override
		public void operationComplete(FutureNotifier<Module> future) {
			if (future.isSuccess()) {
				moduleName = future.getNow().getName();
				display.asyncExec(this);
			}
		}

		@Override
		public void run() {
			item.setText(1, moduleName);
		}

	}

}