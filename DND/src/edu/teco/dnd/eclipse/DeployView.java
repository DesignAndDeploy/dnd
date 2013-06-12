package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung anwählen -
 * Verteilungsalgorithmus auswählen - Fest im Code einbinden? - Verteilung
 * erstellen lassen und anzeigen - Verteilung bestätigen
 * 
 */
public class DeployView extends ViewPart {

	private Button createButton; // Button to create deployment - checkboxes for different deployments?
	private Button deployButton; // Button to deploy deployment
	private Table applications; // Table to show all applications by name.
	// Optional functionality: click app - get information on app
	private Table deployment; // Table to show created deployment

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		parent.setLayout(layout);
		
		createApplicationTable(parent);
		createCreateButton(parent);
		Label label = new Label(parent, SWT.NONE);
		label.setText("Deployment:");
		label.pack();
		createDeployButton(parent);
		createDeploymentTable(parent);
		
		TableItem item1 = new TableItem(applications, SWT.NONE);
		item1.setText(0, "Under Construction.");
		TableItem item = new TableItem(applications, SWT.NONE);
		item.setText(0, "Will contain list of applications");
		
		TableItem item2 = new TableItem(deployment, SWT.NONE);
		item2.setText(0, "FunctionBlocks");
		item2.setText(1, "Modules");
	}

	@Override
	public void setFocus() {

	}
	
	private void createCreateButton(Composite parent){
		createButton = new Button(parent, SWT.NONE);
		createButton.setText("Create Deployment");
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				Shell shell = new Shell(display);
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText("Warning");
				dialog.setMessage("No Deployment Algorithm available yet, still to be implemented");
				dialog.open();
			}
		});
	}
	
	private void createDeployButton(Composite parent){
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		
		deployButton = new Button(parent, SWT.NONE);
		deployButton.setText("Deploy");
		deployButton.setToolTipText("Submit the created deployment to deploy your application");
		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				Shell shell = new Shell(display);
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText("Warning");
				dialog.setMessage("You have to create a possible deployment before deploying");
				dialog.open();
			}
		});
		deployButton.setLayoutData(data);
	}
	
	private void createApplicationTable(Composite parent){
		GridData data = new GridData();
		data.verticalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		applications = new Table(parent, SWT.NONE);
		applications.setLinesVisible(true);
		applications.setHeaderVisible(true);
		applications.setLayoutData(data);
		
		TableColumn column = new TableColumn(applications, SWT.None);
		column.setText("Applications");
		applications.getColumn(0).pack();
	}
	
	private void createDeploymentTable(Composite parent){
		GridData data = new GridData();
		data.verticalSpan = 1;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		deployment = new Table(parent, SWT.NONE);
		deployment.setLinesVisible(true);
		deployment.setHeaderVisible(true);
		deployment.setLayoutData(data);
		
		TableColumn column = new TableColumn(deployment, SWT.None);
		column.setText("Function Block");
		TableColumn column2 = new TableColumn(deployment, SWT.NONE);
		column2.setText("Module");
		deployment.getColumn(0).pack();
		deployment.getColumn(1).pack();
	}

}
