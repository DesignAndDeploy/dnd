package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Testcode f�r Tabs in einem Fenster
 * 
 * @author alisa
 * 
 */
public class Tabs {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		shell.setLayout(new GridLayout());
		// SWT.BOTTOM to show at the bottom
		CTabFolder folder = new CTabFolder(shell, SWT.TOP);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		folder.setLayoutData(data);
		CTabItem cTabItem1 = new CTabItem(folder, SWT.NONE);
		cTabItem1.setText("Discover");
		CTabItem cTabItem2 = new CTabItem(folder, SWT.NONE);
		cTabItem2.setText("Deploy");

		Text text1 = new Text(folder, SWT.BORDER);
		text1.setText("Hier kommen Discover-Sachen hin");
		cTabItem1.setControl(text1);

		Text text2 = new Text(folder, SWT.BORDER);
		text2.setText("Hier kommt Deploy-Zeug hin");
		cTabItem2.setControl(text2);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
