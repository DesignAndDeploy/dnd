package edu.teco.dnd.eclipse.moduleView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Button;

import edu.teco.dnd.eclipse.DisplayUtil;
import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.server.ServerManager;
import edu.teco.dnd.server.ServerState;
import edu.teco.dnd.server.ServerStateListener;

class StartStopButtonActivator implements ServerStateListener {
	private static final Logger LOGGER = LogManager.getLogger(StartStopButtonActivator.class);

	public static final TypecastingWidgetDataStore<ServerAction> SERVER_ACTION_STORE =
			new TypecastingWidgetDataStore<ServerAction>(ServerAction.class, "server action");

	private Button startStopButton = null;
	private ServerManager serverManager = null;

	synchronized void setStartStopButton(final Button startStopButton) {
		LOGGER.entry(startStopButton);
		this.startStopButton = startStopButton;
		if (serverManager != null) {
			serverStateChanged(serverManager.getState(), null, null);
		}
		LOGGER.exit();
	}

	synchronized void setServerManager(final ServerManager serverManager) {
		LOGGER.entry(serverManager);
		if (serverManager != null) {
			serverManager.removeServerStateListener(this);
		}

		this.serverManager = serverManager;
		if (serverManager != null) {
			serverManager.addServerStateListener(this);
		}
		LOGGER.exit();
	}

	@Override
	public void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		LOGGER.entry(state, connectionManager, beacon);
		Button button = null;
		synchronized (this) {
			button = startStopButton;
		}
		if (button == null) {
			LOGGER.exit();
			return;
		}

		ButtonChanger buttonChanger = null;
		switch (state) {
		case STOPPED:
			buttonChanger =
					new ButtonChanger(button, Messages.StartStopButtonActivator_BUTTON_START_SERVER, ServerAction.START);
			break;

		case STARTING:
			buttonChanger =
					new ButtonChanger(button, Messages.StartStopButtonActivator_BUTTON_SERVER_STARTING,
							ServerAction.NONE);
			break;

		case RUNNING:
			buttonChanger =
					new ButtonChanger(button, Messages.StartStopButtonActivator_BUTTON_STOP_SERVER, ServerAction.STOP);
			break;

		case STOPPING:
			buttonChanger =
					new ButtonChanger(button, Messages.StartStopButtonActivator_BUTTON_SERVER_STOPPING,
							ServerAction.NONE);
			break;

		default:
			buttonChanger =
					new ButtonChanger(button, Messages.StartStopButtonActivator_BUTTON_ERROR, ServerAction.NONE);
			break;
		}

		DisplayUtil.getDisplay().syncExec(buttonChanger);
		LOGGER.exit();
	}

	private static class ButtonChanger implements Runnable {
		private final Button button;
		private final String text;
		private final ServerAction action;

		private ButtonChanger(final Button button, final String text, final ServerAction action) {
			this.button = button;
			this.text = text;
			this.action = action;
		}

		@Override
		public void run() {
			button.setText(text);
			button.setEnabled(action != ServerAction.NONE);
			SERVER_ACTION_STORE.store(button, action);
		}
	}
}
