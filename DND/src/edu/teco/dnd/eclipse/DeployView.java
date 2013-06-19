package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;

/**
 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung anwählen -
 * Verteilungsalgorithmus auswählen - Fest im Code einbinden? - Verteilung
 * erstellen lassen und anzeigen - Verteilung bestätigen
 * 
 */
public class DeployView extends ViewPart implements ConnectionListener,
		DNDServerStateListener {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleView.class);
	private Display display;
	private Activator activator;
	
	private Collection<Module> modules;
	private Collection<FunctionBlock> functionBlocks;
	
	private Button createButton; // Button to create deployment
	private Button deployButton; // Button to deploy deployment
	private Button updateButton; // Button to update moduleCombo
	private Label blockSpecifications;
	private Label block; // Block to edit specifications
	private Label module;
	private Label place;
	private Combo moduleCombo;
	private Combo places;
	private Table blockSpecs; // Table to specify deployment information (module
								// and place) for block
	private Table deployment; // Table to show blocks and current deployment

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		parent.setLayout(layout);
		
		modules = new ArrayList<Module>();
		functionBlocks = new ArrayList<FunctionBlock>();

		createDeploymentTable(parent);
		createUpdateButton(parent);
		createBlockSpecsLabel(parent);
		createCreateButton(parent);
		createBlockLabel(parent);
		createDeployButton(parent);
		createModuleLabel(parent);
		createmoduleComboCombo(parent);
		createPlaceLabel(parent);
		createPlacesCombo(parent);

		TableItem item2 = new TableItem(deployment, SWT.NONE);
		item2.setText(0, "FunctionBlocks");
		item2.setText(1, "moduleCombo");

		TableItem item3 = new TableItem(deployment, SWT.NONE);
		item3.setText(0, "another Block");
		item3.setText(1, "");
		item3.setText(2, "heater");
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		LOGGER.entry(site, memento);
		super.init(site, memento);
		activator = Activator.getDefault();
		display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
			LOGGER.trace(
					"Display.getCurrent() returned null, using Display.getDefault(): {}",
					display);
		}
		activator.addServerStateListener(this);
		LOGGER.exit();
	}

	private void createDeploymentTable(Composite parent) {
		GridData data = new GridData();
		data.verticalSpan = 4;
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
		TableColumn column3 = new TableColumn(deployment, SWT.NONE);
		column3.setText("place");
		deployment.getColumn(0).pack();
		deployment.getColumn(1).pack();
		deployment.getColumn(2).pack();

		deployment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = DeployView.this.deployment.getSelection();
				if (items.length == 1) {
					DeployView.this.block.setText(items[0].getText());
				}
			}
		});
	}

	private void createUpdateButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateButton = new Button(parent, SWT.NONE);
		updateButton.setLayoutData(data);
		updateButton.setText("Update");
		updateButton.setToolTipText("Updates information on moduleCombo");
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				Shell shell = new Shell(display);
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				dialog.setText("Warning");
				dialog.setMessage("Not implemented yet. Later: Will update information on moduleCombo");
				dialog.open();
			}
		});
		updateButton.pack();
	}

	private void createBlockSpecsLabel(Composite parent) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		blockSpecifications = new Label(parent, SWT.NONE);
		blockSpecifications.setText("Block Specifications:");
		blockSpecifications.setLayoutData(data);
		blockSpecifications.pack();
	}

	private void createCreateButton(Composite parent) {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		createButton = new Button(parent, SWT.NONE);
		createButton.setLayoutData(data);
		createButton.setText("Create Deployment");
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				Shell shell = new Shell(display);
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				dialog.setText("Warning");
				dialog.setMessage("No Deployment Algorithm available yet, still to be implemented");
				dialog.open();
			}
		});
	}

	private void createBlockLabel(Composite parent) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		block = new Label(parent, SWT.NONE);
		block.setText("(select block on the left)");
		block.setLayoutData(data);
		block.pack();
	}

	private void createDeployButton(Composite parent) {
		GridData data = new GridData();
		data.verticalSpan = 3;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;

		deployButton = new Button(parent, SWT.NONE);
		deployButton.setText("Deploy");
		deployButton
				.setToolTipText("Submit the created deployment to deploy your application");
		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				Shell shell = new Shell(display);
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				dialog.setText("Warning");
				dialog.setMessage("You have to create a possible deployment before deploying");
				dialog.open();
			}
		});
		deployButton.setLayoutData(data);
	}

	private void createModuleLabel(Composite parent) {
		module = new Label(parent, SWT.NONE);
		module.setText("Module:");
		module.setToolTipText("Select a Module for this function block");
		module.pack();
	}

	private void createmoduleComboCombo(Composite parent) {
		moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.add("Modul 1");

	}

	private void createPlaceLabel(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		place = new Label(parent, SWT.NONE);
		place.setLayoutData(data);
		place.setText("Place:");
		place.setToolTipText("Select a place for this function block");
		place.pack();
	}

	private void createPlacesCombo(Composite parent) {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		places = new Combo(parent, SWT.NONE);
		places.setLayoutData(data);
	}
	
	private void updateModules(){
		moduleCombo.removeAll();
		for(Module m : modules){
		}
	}

	@Override
	public void serverStarted(ConnectionManager connectionManager,
			UDPMulticastBeacon beacon) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionEstablished(UUID uuid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionClosed(UUID uuid) {
		// TODO Auto-generated method stub

	}

}
