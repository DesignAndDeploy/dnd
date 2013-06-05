package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestView {
	private Display display;
	private Shell shell;

	public TestView() {
		init();
	}

	public void buttonTest() {
		Button button = new Button(shell, SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Handle the selection event
				textTest();
				System.out.println("Called!");
			}
		});
		button.setLocation(20, 50);
		button.setText("Das ist ein Knopf");
		button.pack();
		startShell();
	}

	public void textTest() {
		Label label = new Label(shell, SWT.BORDER);
		label.setText("Hallo");
		label.setToolTipText("blubb");

		Text text = new Text(shell, SWT.NONE);
		text.setText("[insert random text here]");
		text.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
		text.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setLocation(0, 30);
		// set widgets size to their preferred size
		text.pack();
		label.pack();
	}

	private void init() {
		display = new Display();
		shell = new Shell(display);
	}

	private void startShell() {
		shell.open();
		// Create and check the event loop
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		System.out.println("fertig");
		init();
	}
}
