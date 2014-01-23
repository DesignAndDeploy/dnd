package edu.teco.dnd.eclipse.moduleView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.eclipse.TypecastingWidgetDataStore;

class StartStopButtonListener extends SelectionAdapter {
	private static final Logger LOGGER = LogManager.getLogger(StartStopButtonListener.class);

	private final TypecastingWidgetDataStore<ServerAction> store;

	StartStopButtonListener(final TypecastingWidgetDataStore<ServerAction> store) {
		this.store = store;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		LOGGER.entry(e);
		final Object source = e.getSource();
		if (!(source instanceof Widget)) {
			LOGGER.exit();
			return;
		}

		final Widget widget = (Widget) source;
		final ServerAction action = store.retrieve(widget);
		if (action == ServerAction.START) {
			Activator.getDefault().startServer();
		} else if (action == ServerAction.STOP) {
			Activator.getDefault().shutdownServer();
		}
	}
}
