package edu.teco.dnd.eclipse.appView;

import java.util.Collection;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.server.ApplicationInformation;
import edu.teco.dnd.server.ApplicationManager;
import edu.teco.dnd.server.ApplicationManagerListener;
import edu.teco.dnd.server.BlockInformation;
import edu.teco.dnd.server.ServerState;
import edu.teco.dnd.server.ServerStateListener;

/**
 * This class is used to update the Tree used by {@link ApplicationView}.
 * 
 * @author Philipp Adolf
 */
// TODO: Add module names
class ApplicationTreeUpdater implements ServerStateListener, ApplicationManagerListener {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationTreeUpdater.class);

	public static final TypecastingWidgetDataStore<ApplicationInformation> APPLICATION_INFORMATION_STORE =
			new TypecastingWidgetDataStore<ApplicationInformation>(ApplicationInformation.class, "application");

	private ApplicationManager applicationManager = null;
	private volatile Tree applicationTree = null;

	/**
	 * Sets the ApplicationManager that should be used. This method is thread-safe and will update applicationTree if it
	 * has been set.
	 * 
	 * @param applicationManager
	 *            the new ApplicationManager to use
	 * @see #setApplicationTree(Tree)
	 */
	synchronized void setApplicationManager(final ApplicationManager applicationManager) {
		LOGGER.entry(applicationManager);
		if (this.applicationManager != null) {
			this.applicationManager.removeApplicationListener(this);
		}

		this.applicationManager = applicationManager;
		if (this.applicationManager != null) {
			this.applicationManager.addApplicationListener(this);

			fillTree(this.applicationManager.getApplications());
		} else {
			fillTree(Collections.<ApplicationInformation> emptyList());
		}
		LOGGER.exit();
	}

	/**
	 * Sets the Tree object that should be filled with the Applications. If an ApplicationManager has been set, the Tree
	 * is automatically filled. This method is thread-safe.
	 * 
	 * @param applicationTree
	 *            the Tree object that should be filled with the Application data
	 * @see #setApplicationManager(ApplicationManager)
	 */
	synchronized void setApplicationTree(final Tree applicationTree) {
		this.applicationTree = applicationTree;
		if (applicationManager == null) {
			fillTree(Collections.<ApplicationInformation> emptyList());
		} else {
			fillTree(applicationManager.getApplications());
		}
	}

	@Override
	public void applicationsResolved(final Collection<ApplicationInformation> apps) {
		LOGGER.entry(apps);
		fillTree(apps);
		LOGGER.exit();
	}

	@Override
	public synchronized void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		switch (state) {
		case STOPPING:
		case STOPPED:
			fillTree(Collections.<ApplicationInformation> emptyList());
		}
	}

	/**
	 * Fills the tree with the given ApplicationInformation. Old information is discarded first.
	 * 
	 * @param applications
	 *            the Applications that should be displayed
	 */
	private void fillTree(final Collection<ApplicationInformation> applications) {
		LOGGER.entry(applications);
		assert applications != null;

		final Tree localApplicationTree;
		synchronized (this) {
			localApplicationTree = applicationTree;
		}

		if (localApplicationTree == null) {
			LOGGER.debug("applicationTree is null");
		} else {
			DisplayUtil.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					localApplicationTree.removeAll();

					for (final ApplicationInformation application : applicationManager.getApplications()) {
						addApplication(localApplicationTree, application);
					}

					localApplicationTree.update();
				}
			});
		}
		LOGGER.exit();
	}

	/**
	 * Adds a single Application to the tree.
	 * 
	 * @param applicationInformation
	 *            the Application to add
	 */
	private void addApplication(final Tree tree, final ApplicationInformation applicationInformation) {
		LOGGER.entry(applicationInformation);
		assert tree != null;
		assert applicationInformation != null;

		LOGGER.debug("adding TableItem for {} to {}", applicationInformation, applicationTree);
		final TreeItem applicationItem = new TreeItem(tree, SWT.NONE);
		applicationItem.setText(0, applicationInformation.getName() + " (" + applicationInformation.getID().getUUID() + ")");
		APPLICATION_INFORMATION_STORE.store(applicationItem, applicationInformation);
		for (final ModuleID moduleID : applicationInformation.getModules()) {
			addModule(applicationItem, applicationInformation, moduleID);
		}

		LOGGER.exit();
	}

	/**
	 * Adds a single Module to an Application TreeItem.
	 * 
	 * @param applicationItem
	 *            the TreeItem of the Application this Module TreeItem should be added to
	 * @param applicationInformation
	 *            the ApplicationInformation for the Application
	 * @param moduleID
	 *            the ID of the Module that should be added by this method
	 */
	private void addModule(final TreeItem applicationItem, final ApplicationInformation applicationInformation,
			final ModuleID moduleID) {
		LOGGER.trace("adding TableItem for module {} to {}", moduleID, applicationItem);
		final TreeItem moduleItem = new TreeItem(applicationItem, SWT.NONE);
		moduleItem.setText("" + moduleID);
		for (final BlockInformation block : applicationInformation.getBlocks(moduleID)) {
			addBlock(moduleItem, applicationInformation, block);
		}
	}

	/**
	 * Adds a single FunctionBlock to an Module TreeItem.
	 * 
	 * @param moduleItem
	 *            the TreeItem of the Module this FunctionBlock TreeItem should be added to
	 * @param applicationInformation
	 *            the ApplicationInformation of the Application this block belongs to
	 * @param blockID
	 *            the ID of the FunctionBlock that should be added by this method
	 */
	private void addBlock(final TreeItem moduleItem, final ApplicationInformation applicationInformation,
			final BlockInformation block) {
		LOGGER.trace("adding TableItem for block {} to {}", block, moduleItem);
		final TreeItem blockItem = new TreeItem(moduleItem, SWT.NONE);
		blockItem.setText(block.getName() + " (" + block.getID() + ")");
	}
}
