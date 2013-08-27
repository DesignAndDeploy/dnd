package edu.teco.dnd.eclipse.deployView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import edu.teco.dnd.server.ServerManager;

/**
 * This class is responsible for creating the graphical representations of the buttons, tables, text fields and so on.
 * It only provides the widgets to be used by the DeployView, not the functionality the user experiences while using them.
 * 
 * @author jung
 * 
 */
public class DeployViewGraphics {

	private Composite parent;

	/**
	 * Horizontal space between cells.
	 */
	public static final int HORIZONTAL_SPACE = 20;

	/**
	 * Vertical space between cells.
	 */
	public static final int VERTICAL_SPACE = 7;

	public DeployViewGraphics(Composite parent) {
		this.parent = parent;
	}

	protected void initializeParent() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.horizontalSpacing = HORIZONTAL_SPACE;
		layout.verticalSpacing = VERTICAL_SPACE;
		parent.setLayout(layout);
	}

	protected Label createAppNameLabel() {
		Label appName = new Label(parent, SWT.NONE);
		appName.setText("Loading application...");
		appName.pack();
		return appName;
	}

	protected Button createServerButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Button serverButton = new Button(parent, SWT.NONE);
		serverButton.setLayoutData(data);
		if (ServerManager.getDefault().isRunning()) {
			serverButton.setText("Stop server");
		} else {
			serverButton.setText("Start server");
		}
		return serverButton;

	}

	protected Label createBlockModelSpecsLabel() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		Label blockModelSpecifications = new Label(parent, SWT.NONE);
		blockModelSpecifications.setText("Block Specifications:");
		blockModelSpecifications.setToolTipText(DeployViewTexts.CONSTRAINTS_TOOLTIP);
		blockModelSpecifications.setLayoutData(data);
		blockModelSpecifications.pack();
		return blockModelSpecifications;
	}

	protected Table createDeploymentTable() {
		GridData data = new GridData();
		data.verticalSpan = 5;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		Table deployment = new Table(parent, SWT.NONE);
		deployment.setLinesVisible(true);
		deployment.setHeaderVisible(true);
		deployment.setLayoutData(data);

		TableColumn column0 = new TableColumn(deployment, SWT.None);
		column0.setText("Function Block");
		TableColumn column1 = new TableColumn(deployment, SWT.NONE);
		column1.setText("Module");
		column1.setToolTipText(DeployViewTexts.COLUMN1_TOOLTIP);
		TableColumn column2 = new TableColumn(deployment, SWT.NONE);
		column2.setText("Place");
		column2.setToolTipText(DeployViewTexts.COLUMN2_TOOLTIP);
		TableColumn column3 = new TableColumn(deployment, SWT.NONE);
		column3.setText("Deployed on:");
		column3.setToolTipText(DeployViewTexts.COLUMN3_TOOLTIP);
		TableColumn column4 = new TableColumn(deployment, SWT.NONE);
		column4.setText("Deployed at:");
		column4.setToolTipText(DeployViewTexts.COLUMN4_TOOLTIP);

		deployment.getColumn(0).pack();
		deployment.getColumn(1).pack();
		deployment.getColumn(2).pack();
		deployment.getColumn(3).pack();
		deployment.getColumn(4).pack();

		return deployment;
	}

	protected Button createUpdateModulesButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Button updateModulesButton = new Button(parent, SWT.NONE);
		updateModulesButton.setLayoutData(data);
		updateModulesButton.setText("Update Modules");
		updateModulesButton.setToolTipText(DeployViewTexts.UPDATEMODULES_TOOLTIP);
		updateModulesButton.pack();
		return updateModulesButton;
	}

	protected Label createBlockModelLabel() {
		Label blockModelLabel = new Label(parent, SWT.NONE);
		blockModelLabel.setText("Name:");
		blockModelLabel.pack();
		return blockModelLabel;
	}

	protected Text createBlockModelName() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Text blockModelName = new Text(parent, SWT.NONE);
		blockModelName.setLayoutData(data);
		blockModelName.setText("<select block on the left>");
		blockModelName.setToolTipText(DeployViewTexts.RENAMEBLOCK_TOOLTIP);
		blockModelName.setEnabled(false);
		blockModelName.pack();
		return blockModelName;

	}

	protected Button createUpdateBlocksButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Button updateBlocksButton = new Button(parent, SWT.NONE);
		updateBlocksButton.setLayoutData(data);
		updateBlocksButton.setText("Update Blocks");
		updateBlocksButton.setToolTipText(DeployViewTexts.UPDATEBLOCKS_TOOLTIP);
		updateBlocksButton.pack();
		return updateBlocksButton;
	}

	protected Label createModuleLabel() {
		Label module = new Label(parent, SWT.NONE);
		module.setText("Module:");
		module.setToolTipText(DeployViewTexts.SELECTMODULE_TOOLTIP);
		module.pack();
		return module;
	}

	protected Combo createModuleCombo() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		Combo moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.setLayoutData(data);
		moduleCombo.setToolTipText(DeployViewTexts.SELECTMODULE_TOOLTIP);
		moduleCombo.add("");
		moduleCombo.setEnabled(false);
		return moduleCombo;
	}

	protected Button createCreateButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		Button createButton = new Button(parent, SWT.NONE);
		createButton.setLayoutData(data);
		createButton.setText("Create Deployment");
		createButton.setToolTipText(DeployViewTexts.CREATE_TOOLTIP);
		return createButton;
	}

	protected Label createPlaceLabel() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 2;
		Label place = new Label(parent, SWT.NONE);
		place.setLayoutData(data);
		place.setText("Place:");
		place.setToolTipText(DeployViewTexts.SELECTPLACE_TOOLTIP);
		place.pack();
		return place;
	}

	protected Text createPlacesText() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		Text places = new Text(parent, SWT.NONE);
		places.setToolTipText(DeployViewTexts.SELECTPLACE_TOOLTIP);
		places.setLayoutData(data);
		places.setEnabled(false);
		return places;
	}

	protected Button createDeployButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 3;
		Button deployButton = new Button(parent, SWT.NONE);
		deployButton.setText("Deploy");
		deployButton.setToolTipText(DeployViewTexts.DEPLOY_TOOLTIP);
		deployButton.setLayoutData(data);
		return deployButton;
	}

	protected Button createConstraintsButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		Button constraintsButton = new Button(parent, SWT.NONE);
		constraintsButton.setLayoutData(data);
		constraintsButton.setText("Save constraints");
		constraintsButton.setEnabled(false);
		constraintsButton.pack();
		return constraintsButton;
	}
	
	protected StyledText createInformationText(){
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		StyledText infoText = new StyledText(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
		infoText.setLayoutData(data);
		infoText.setEditable(false);
		infoText.pack();
		return infoText;
	}
}
