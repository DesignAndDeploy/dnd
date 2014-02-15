package edu.teco.dnd.eclipse.appView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.server.ApplicationInformation;
import edu.teco.dnd.server.ApplicationManager;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * This class handles killing an Application when the kill button is pressed.
 * 
 * @author Philipp Adolf
 */
class KillButtonListener extends SelectionAdapter {
	private static final Logger LOGGER = LogManager.getLogger(KillButtonListener.class);

	private final TypecastingWidgetDataStore<ApplicationInformation> typecastingWidgetDataStore;
	private Tree applicationTree;
	private ApplicationManager applicationManager;

	/**
	 * Initializes a new KillButtonListener. The TypecastingWidgetDataStore given here must be compatible with the Tree
	 * containing the Application data.
	 * 
	 * @param typecastingWidgetDataStore
	 *            used to retrieve ApplicationInformation from the Tree
	 * @see ApplicationTreeUpdater#APPLICATION_INFORMATION_STORE
	 */
	public KillButtonListener(final TypecastingWidgetDataStore<ApplicationInformation> typecastingWidgetDataStore) {
		this.typecastingWidgetDataStore = typecastingWidgetDataStore;
	}

	/**
	 * Sets the Tree containing the Application information. This should be filled in by a
	 * {@link ApplicationTreeUpdater} and use a compatible {@link TypecastingWidgetDataStore} for storing the
	 * {@link ApplicationInformation}. This method is thread safe.
	 * 
	 * @param applicationTree
	 *            the Tree to use
	 */
	synchronized void setApplicationTree(final Tree applicationTree) {
		this.applicationTree = applicationTree;
	}

	/**
	 * Sets the ApplicationManager that should be used to kill applications. This method is thread safe.
	 * 
	 * @param applicationManager
	 *            the ApplicationManager to use
	 */
	synchronized void setApplicationManager(final ApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		LOGGER.entry(e);
		final ApplicationInformation selectedApplication = getSelectedApplication();
		if (selectedApplication == null) {
			LOGGER.exit();
			return;
		}

		final ApplicationID applicationID = selectedApplication.getID();
		final FutureNotifier<Void> killFutureNotifier = killApplication(applicationID);
		if (killFutureNotifier == null) {
			LOGGER.exit();
			return;
		}

		killFutureNotifier.addListener(new KillFutureListener(applicationID));
		LOGGER.exit();
	}

	private FutureNotifier<Void> killApplication(final ApplicationID applicationID) {
		LOGGER.entry(applicationID);
		FutureNotifier<Void> killFutureNotifier;
		synchronized (this) {
			if (applicationManager == null) {
				return LOGGER.exit(null);
			}

			killFutureNotifier = applicationManager.killApplication(applicationID);
		}
		return LOGGER.exit(killFutureNotifier);
	}

	private ApplicationInformation getSelectedApplication() {
		LOGGER.entry();
		final Tree tree = getTree();
		if (tree == null) {
			return LOGGER.exit(null);
		}

		final TreeItem[] selection = tree.getSelection();
		if (selection == null || selection.length != 1) {
			return LOGGER.exit(null);
		}

		final ApplicationInformation applicationInformation =
				typecastingWidgetDataStore.retrieve(getRoot(selection[0]));
		if (applicationInformation == null) {
			return LOGGER.exit(null);
		}
		return LOGGER.exit(applicationInformation);
	}

	private synchronized Tree getTree() {
		return applicationTree;
	}

	/**
	 * Returns the root of a TreeItem. That is, it travels along the Tree using {@link Tree#getParentItem()} until there
	 * is no parent.
	 * 
	 * @param item
	 *            the item of which to get the root
	 * @return the root of the item
	 */
	private TreeItem getRoot(TreeItem item) {
		TreeItem parent = null;
		do {
			parent = item.getParentItem();
			if (parent != null) {
				item = parent;
			}
		} while (parent != null);
		return item;
	}

	/**
	 * This is used to check the result of killing an Application.
	 * 
	 * @author Philipp Adolf
	 */
	private final class KillFutureListener implements FutureListener<FutureNotifier<Void>> {
		private final ApplicationID applicationID;

		private KillFutureListener(ApplicationID applicationID) {
			this.applicationID = applicationID;
		}

		@Override
		public void operationComplete(final FutureNotifier<Void> future) throws Exception {
			// TODO: add feedback to user
			if (future.isSuccess()) {
				LOGGER.debug("Successfully killed {}", applicationID);
			} else {
				LOGGER.debug("Failed to kill {}", applicationID);
			}
			synchronized (KillButtonListener.this) {
				applicationManager.update();
			}
		}
	}
}
