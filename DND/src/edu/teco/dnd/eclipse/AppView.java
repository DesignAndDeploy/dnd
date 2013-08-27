package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * View for the applications / running function blocks.
 * 
 * @author jung
 * 
 */
public class AppView extends ViewPart {
	private Label label;

	// Used to map FunctionBlocks on table items
	// private Map<UUID, TableItem> map = new HashMap<UUID, TableItem>();

	/**
	 * Creates a new Appview.
	 */
	public AppView() {
		super();
	}

	public void setFocus() {
		label.setFocus();
	}

	public void createPartControl(Composite parent) {
		label = new Label(parent, 0);
		label.setText("Applications");
		label.setToolTipText("Shows running function blocks");

		createFunctionBlockTable(parent);
	}

	private void createFunctionBlockTable(Composite parent) {
		Table functTable = new Table(parent, 0);
		functTable.setLinesVisible(true);
		functTable.setHeaderVisible(true);

		TableColumn column = new TableColumn(functTable, SWT.NONE);
		column.setText("Running Function Blocks");
		functTable.setToolTipText("Currently running function blocks");
		functTable.getColumn(0).pack();
	}

}
