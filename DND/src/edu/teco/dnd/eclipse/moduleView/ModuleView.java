package edu.teco.dnd.eclipse.moduleView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class ModuleView extends ViewPart {
	private static final Logger LOGGER = LogManager.getLogger(ModuleView.class);

	@Override
	public void createPartControl(final Composite parent) {
		LOGGER.entry(parent);
		setLayout(parent);
		createStartStopButton(parent);
		createServerStatusLabel(parent);
		createModuleTable(parent);
		parent.pack();
		LOGGER.exit();
	}

	private void setLayout(final Composite parent) {
		parent.setLayout(new GridLayout(2, false));
	}

	private void createStartStopButton(final Composite parent) {
		final Button startStopButton = new Button(parent, SWT.NONE);
		startStopButton.setText(Messages.ModuleView_BUTTON_START_SERVER);
		startStopButton.setToolTipText(Messages.ModuleView_START_STOP_BUTTON_TOOLTIP);
	}

	private Label createServerStatusLabel(final Composite parent) {
		final Label serverStatusLabel = new Label(parent, SWT.NONE);

		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		serverStatusLabel.setLayoutData(gridData);

		serverStatusLabel.setText(Messages.ModuleView_SERVER_STOPPED);

		return serverStatusLabel;
	}

	private Table createModuleTable(final Composite parent) {
		final Table moduleTable = new Table(parent, SWT.SINGLE);

		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		moduleTable.setLayoutData(gridData);
		moduleTable.setLinesVisible(true);
		moduleTable.setHeaderVisible(true);
		moduleTable.setToolTipText(Messages.ModuleView_MODULE_TABLE_TOOLTIP);

		createColumn(moduleTable, Messages.ModuleView_COLUMN_MODULE_ID);
		createColumn(moduleTable, Messages.ModuleView_COLUMN_MODULE_NAME);
		createColumn(moduleTable, Messages.ModuleView_COLUMN_MODULE_LOCATION);

		for (final TableColumn column : moduleTable.getColumns()) {
			column.pack();
		}

		TableItem temp = new TableItem(moduleTable, SWT.NONE);
		temp.setText(new String[] { "foo", "bar", "baz" });

		return moduleTable;
	}

	private void createColumn(final Table table, final String name) {
		final TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(name);
	}

	@Override
	public void setFocus() {
		// not needed by this view
	}
}