package edu.teco.dnd.eclipse.appView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Button;

import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.server.DNDServerStateListener;

/**
 * Used to automatically enable/disable the update button of the application view based on the state of the server.
 * 
 * @author Philipp Adolf
 */
class UpdateButtonActivator implements DNDServerStateListener {
	private static final Logger LOGGER = LogManager.getLogger();

	private Button updateButton = null;

	/**
	 * Sets the button that will be enabled/disabled by this activator. This method is thread-safe.
	 * 
	 * @param updateButton
	 *            the button that should be handled. Can be null.
	 */
	synchronized void setUpdateButton(final Button updateButton) {
		LOGGER.debug("setting update button to {}", updateButton);
		this.updateButton = updateButton;
	}

	/**
	 * Sets the state of the button. This is run synchronously in SWT's event loop.
	 * 
	 * @param state
	 *            the state that should be set
	 */
	void setState(final boolean state) {
		LOGGER.entry(state);
		DisplayUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (UpdateButtonActivator.this) {
					if (updateButton == null) {
						LOGGER.debug("updateButton is null");
					} else {
						updateButton.setEnabled(state);
					}
				}
			}
		});
		LOGGER.exit();
	}

	@Override
	public void serverStarted(final ConnectionManager connectionManager, final UDPMulticastBeacon beacon) {
		setState(true);
	}

	@Override
	public void serverStopped() {
		setState(false);
	}
}
