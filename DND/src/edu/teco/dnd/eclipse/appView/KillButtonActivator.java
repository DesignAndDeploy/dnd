package edu.teco.dnd.eclipse.appView;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is used to dynamically enable/disable the "kill application" button based on whether or not an Application
 * is selected.
 * 
 * @author Philipp Adolf
 */
class KillButtonActivator extends SelectionAdapter {
	private final Button killButton;

	KillButtonActivator(final Button killButton) {
		this.killButton = killButton;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final Object source = e.getSource();
		if (!(source instanceof Tree)) {
			return;
		}

		final Tree tree = (Tree) source;
		final TreeItem[] selection = tree.getSelection();

		killButton.setEnabled(selection != null && selection.length == 1);
	}
}
