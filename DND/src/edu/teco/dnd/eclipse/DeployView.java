package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
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
		Label label = new Label(parent, SWT.None);
		label.setText("Under Construction. Please be patient, our incredible DeployView will be here soon.");
		label.pack();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		parent.setLayout(layout);
		
		createApplicationTable(parent);
		createCreateButton(parent);
		createDeployButton(parent);		
		createDeploymentTable(parent);
	}

	@Override
	public void setFocus() {

	}
	
	private void createCreateButton(Composite parent){
		createButton = new Button(parent, SWT.NONE);
		createButton.setText("Create Deployment");
	}
	
	private void createDeployButton(Composite parent){
		
	}
	
	private void createApplicationTable(Composite parent){
		
	}
	
	private void createDeploymentTable(Composite parent){
		
	}

}
