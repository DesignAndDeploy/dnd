package edu.teco.dnd.eclipse.moduleView;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ModuleManagerListener;
import edu.teco.dnd.server.ServerManager;
import edu.teco.dnd.server.ServerState;
import edu.teco.dnd.server.ServerStateListener;

/**
 * Used to fill the table in {@link ModuleView} based on discovered Modules.
 */
// All non-private methods are simply a wrapper to execute the code in SWT's display thread and are therefore thread-
// safe
class ModuleTableUpdater implements ServerStateListener, ModuleManagerListener {
	private static final Logger LOGGER = LogManager.getLogger(ModuleTableUpdater.class);

	public static TypecastingWidgetDataStore<UUID> MODULE_UUID_STORE = new TypecastingWidgetDataStore<UUID>(UUID.class,
			"module uuid");

	private Table moduleTable = null;
	private ServerManager<?> serverManager = null;

	void setModuleTable(final Table moduleTable) {
		LOGGER.entry(moduleTable);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(moduleTable);
				ModuleTableUpdater.this.moduleTable = moduleTable;
				if (serverManager != null) {
					// Re-add as listener so that moduleTable gets filled
					final ModuleManager moduleManager = serverManager.getModuleManager();
					moduleManager.removeListener(ModuleTableUpdater.this);
					moduleManager.addListener(ModuleTableUpdater.this);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	void setServerManager(final ServerManager<?> newServerManager) {
		LOGGER.entry(newServerManager);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(newServerManager);
				if (serverManager != null) {
					serverManager.removeServerStateListener(ModuleTableUpdater.this);
					serverManager.getModuleManager().removeListener(ModuleTableUpdater.this);
					if (moduleTable != null) {
						moduleTable.removeAll();
					}
				}

				ModuleTableUpdater.this.serverManager = newServerManager;
				if (newServerManager != null) {
					newServerManager.addServerStateListener(ModuleTableUpdater.this);
					newServerManager.getModuleManager().addListener(ModuleTableUpdater.this);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleAdded(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(module);
				if (moduleTable == null || module == null) {
					return;
				}
				final int index = getIndex(moduleTable, module.getUUID());
				if (index < 0) {
					addModule(moduleTable, module);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleRemoved(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(module);
				if (moduleTable == null || module == null) {
					return;
				}
				final int index = getIndex(moduleTable, module.getUUID());
				if (index >= 0) {
					moduleTable.remove(index);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleUpdated(final ModuleInfo module) {
		LOGGER.entry(module);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(module);
				if (moduleTable == null || module == null) {
					return;
				}
				final int index = getIndex(moduleTable, module.getUUID());
				TableItem item = null;
				if (index < 0) {
					item = addModule(moduleTable, module);
				} else {
					item = moduleTable.getItem(index);
				}
				setModuleInfo(item, module);
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		switch (state) {
		case STOPPING:
		case STOPPED:
			serverOffline();
		}
	}

	private void serverOffline() {
		LOGGER.entry();
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry();
				if (moduleTable != null) {
					moduleTable.removeAll();
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	private TableItem addModule(final Table moduleTable, final ModuleInfo module) {
		LOGGER.entry(moduleTable, module);
		assert moduleTable != null;
		final TableItem item = new TableItem(moduleTable, SWT.NONE);
		setModuleInfo(item, module);
		return LOGGER.exit(item);
	}

	private void setModuleInfo(final TableItem item, final ModuleInfo moduleInfo) {
		LOGGER.entry(item, moduleInfo);
		assert item != null;
		assert moduleInfo != null;

		MODULE_UUID_STORE.store(item, moduleInfo.getUUID());
		item.setText(0, "" + moduleInfo.getUUID());
		item.setText(1, "" + moduleInfo.getName());
		item.setText(2, "" + moduleInfo.getLocation());
		LOGGER.exit();
	}

	private int getIndex(final Table moduleTable, final UUID moduleID) {
		LOGGER.entry(moduleTable, moduleID);
		assert moduleTable != null;
		assert moduleID != null;
		final TableItem[] items = moduleTable.getItems();
		for (int i = 0; i < items.length; i++) {
			if (moduleID.equals(getModuleID(items[i]))) {
				return LOGGER.exit(i);
			}
		}
		return LOGGER.exit(-1);
	}

	private UUID getModuleID(final TableItem item) {
		LOGGER.entry(item);
		assert item != null;
		return LOGGER.exit(MODULE_UUID_STORE.retrieve(item));
	}
}
