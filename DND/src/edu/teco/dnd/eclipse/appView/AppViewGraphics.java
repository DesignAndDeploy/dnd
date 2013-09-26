package edu.teco.dnd.eclipse.appView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.teco.dnd.server.ServerManager;

/**
 * This class is responsible for creating the graphical representations of the buttons, tables, text fields and so on in
 * the AppView. It only provides the widgets to be used by the AppView, not the functionality the user experiences
 * while using. It is also responsible for changing the appearance and contents of text fields while the user operates.
 * 
 * @author jung
 * 
 */
public class AppViewGraphics {

	/**
	 * Horizontal space between cells.
	 */
	public static final int HORIZONTAL_SPACE = 10;

	/**
	 * Vertical space between cells.
	 */
	public static final int VERTICAL_SPACE = 5;
	
	private Composite parent;
	
	protected void initParent(final Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		layout.horizontalSpacing = HORIZONTAL_SPACE;
		layout.verticalSpacing = VERTICAL_SPACE;
		this.parent.setLayout(layout);
	}

	protected Button createUpdateAppsButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Button updateAppsButton = new Button(parent, SWT.NONE);
		updateAppsButton.setLayoutData(data);
		updateAppsButton.setText(Messages.AppViewGraphics_UPDATE);
		updateAppsButton.setEnabled(ServerManager.getDefault().isRunning());
		return updateAppsButton;
	}
	
	protected Button createKillAppButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		Button killAppButton = new Button(parent, SWT.NONE);
		killAppButton.setLayoutData(data);
		killAppButton.setText(Messages.AppViewGraphics_KILL_APPLICATION);
		killAppButton.setEnabled(false);
		return killAppButton;

	}

	protected Button createSortButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		Button sortButton = new Button(parent, SWT.NONE);
		sortButton.setText(AppView.SORT_MOD);
		sortButton.setEnabled(false);
		return sortButton;

	}

	protected Label createSelectedLabel() {
		Label selectedLabel = new Label(parent, SWT.NONE);
		selectedLabel.setText(Messages.AppView_APPLICATION);
		return selectedLabel;
	}

	protected Label createSelectedInfoLabel() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		Label selectedInfoLabel = new Label(parent, SWT.NONE);
		selectedInfoLabel.setText(Messages.AppViewGraphics_DO_SELECT);
		return selectedInfoLabel;
	}

	protected Table createAppTable() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		Table appTable = new Table(parent, SWT.NONE);
		appTable.setHeaderVisible(true);
		appTable.setLinesVisible(true);
		appTable.setLayoutData(data);
		TableColumn column0 = new TableColumn(appTable, SWT.NONE);
		column0.setText(Messages.AppView_APPLICATIONS);
		appTable.getColumn(AppView.APP_INDEX).pack();
		TableColumn column1 = new TableColumn(appTable, SWT.NONE);
		column1.setText(Messages.AppViewGraphics_UUID);
		appTable.getColumn(AppView.UUID_INDEX).pack();
		return appTable;
	}

	protected Table createBlockTable() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;

		Table blockTable = new Table(parent, 0);
		blockTable.setLayoutData(data);
		blockTable.setLinesVisible(true);
		blockTable.setHeaderVisible(true);

		TableColumn column0 = new TableColumn(blockTable, SWT.NONE);
		column0.setText(Messages.AppViewGraphics_FUNCTION_BLOCK);
		blockTable.setToolTipText(Messages.AppViewGraphics_CURRENTLY_RUNNING_BLOCKS);
		blockTable.getColumn(AppView.BLOCK_INDEX).pack();
		
		TableColumn column1 = new TableColumn(blockTable, SWT.NONE);
		column1.setText(Messages.AppViewGraphics_TYPE);
		blockTable.getColumn(AppView.TYPE_INDEX).pack();

		TableColumn column2 = new TableColumn(blockTable, SWT.NONE);
		column2.setText(Messages.AppView_ON_MODULE);
		blockTable.getColumn(AppView.BLOCK_INFO_INDEX).pack();
		return blockTable;
	}
}
