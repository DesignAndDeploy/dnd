package edu.teco.dnd.eclipse.moduleView;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ModuleManagerListener;

/**
 * Used to fill the table in {@link ModuleView} based on discovered Modules.
 */
// All non-private methods are simply a wrapper to execute the code in SWT's display thread and are therefore thread-
// safe
class ModuleTableUpdater implements ModuleManagerListener {
	private static final Logger LOGGER = LogManager.getLogger(ModuleTableUpdater.class);

	public static TypecastingWidgetDataStore<UUID> MODULE_UUID_STORE = new TypecastingWidgetDataStore<UUID>(UUID.class,
			"module uuid");

	private Table moduleTable = null;
	private ModuleManager moduleManager = null;

	void setModuleTable(final Table moduleTable) {
		LOGGER.entry(moduleTable);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(moduleTable);
				ModuleTableUpdater.this.moduleTable = moduleTable;
				if (moduleTable != null && moduleManager != null) {
					fillModuleTable(moduleTable, moduleManager.getMap());
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	void setModuleManager(final ModuleManager moduleManager) {
		LOGGER.entry(moduleManager);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(moduleManager);
				if (ModuleTableUpdater.this.moduleManager != null) {
					ModuleTableUpdater.this.moduleManager.removeModuleManagerListener(ModuleTableUpdater.this);
				}

				ModuleTableUpdater.this.moduleManager = moduleManager;
				if (moduleManager != null) {
					moduleManager.addModuleManagerListener(ModuleTableUpdater.this);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleOnline(final UUID id) {
		LOGGER.entry(id);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(id);
				if (moduleTable == null || id == null) {
					return;
				}
				final int index = getIndex(moduleTable, id);
				if (index < 0) {
					addModule(moduleTable, id);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleOffline(final UUID id, final ModuleInfo module) {
		LOGGER.entry(id, module);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(id, module);
				if (moduleTable == null || id == null) {
					return;
				}
				final int index = getIndex(moduleTable, id);
				if (index >= 0) {
					moduleTable.remove(index);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void moduleResolved(final UUID id, final ModuleInfo module) {
		LOGGER.entry(id, module);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(id, module);
				if (moduleTable == null || id == null) {
					return;
				}
				final int index = getIndex(moduleTable, id);
				TableItem item = null;
				if (index < 0) {
					item = addModule(moduleTable, id);
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
	public void serverOnline(final Map<UUID, ModuleInfo> modules) {
		LOGGER.entry(modules);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(modules);
				if (moduleTable == null) {
					return;
				}
				if (modules == null) {
					fillModuleTable(moduleTable, Collections.<UUID, ModuleInfo> emptyMap());
				} else {
					fillModuleTable(moduleTable, modules);
				}
				LOGGER.exit();
			}
		});
		LOGGER.exit();
	}

	@Override
	public void serverOffline() {
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

	private void fillModuleTable(final Table moduleTable, final Map<UUID, ModuleInfo> modules) {
		LOGGER.entry(moduleTable, modules);
		assert moduleTable != null;
		assert modules != null;
		moduleTable.removeAll();
		for (final Entry<UUID, ModuleInfo> module : modules.entrySet()) {
			final TableItem column = addModule(moduleTable, module.getKey());
			setModuleInfo(column, module.getValue());
		}
		LOGGER.exit();
	}

	private TableItem addModule(final Table moduleTable, final UUID moduleID) {
		LOGGER.entry(moduleTable, moduleID);
		assert moduleTable != null;
		final TableItem item = new TableItem(moduleTable, SWT.NONE);
		setModuleID(item, moduleID);
		return LOGGER.exit(item);
	}

	private void setModuleInfo(final TableItem item, final ModuleInfo moduleInfo) {
		LOGGER.entry(item, moduleInfo);
		assert item != null;
		if (moduleInfo != null) {
			setModuleID(item, moduleInfo.getUUID());
			item.setText(1, "" + moduleInfo.getName());
			item.setText(2, "" + moduleInfo.getLocation());
		}
		LOGGER.exit();
	}

	private void setModuleID(final TableItem item, final UUID moduleID) {
		LOGGER.entry(item, moduleID);
		assert item != null;
		item.setText(0, "" + moduleID);
		MODULE_UUID_STORE.store(item, moduleID);
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
