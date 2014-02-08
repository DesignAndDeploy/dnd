package edu.teco.dnd.eclipse.deployEditor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * This class is responsible for creating the graphical representations of the buttons, tables, text fields and so on in
 * the DeployView. It only provides the widgets to be used by the DeployView, not the functionality the user experiences
 * while using. It is also responsible for changing the appearance and contents of text fields while the user operates.
 * 
 * @author jung
 * 
 */
public class DeployEditorGraphics {

	/**
	 * Key code for the 'enter' key.
	 */
	public static final int ENTER = 13;

	/**
	 * Index of the Block Name in the deployment table.
	 */
	public static final int BLOCKNAME = 0;

	/**
	 * Index of the module assigned by the user in the deployment table.
	 */
	public static final int USER_MODULE = 1;

	/**
	 * Index of the location assigned by the user in the deployment table.
	 */
	public static final int USER_LOCATION = 2;

	/**
	 * Index of the module assigned by the distribution in the deployment table.
	 */
	public static final int DIST_MODULE = 3;

	/**
	 * Index of the location assigned by the distribution in the deployment table.
	 */
	public static final int DIST_LOCATION = 4;

	/**
	 * Horizontal space between cells.
	 */
	public static final int HORIZONTAL_SPACE = 20;

	/**
	 * Vertical space between cells.
	 */
	public static final int VERTICAL_SPACE = 7;

	private Composite parent;

	private Label appNameLabel;
	private Button serverButton;
	private Label blockModelSpecsLabel;
	private Table deploymentTable;
	private Button updateModulesButton;
	private Label blockNameLabel;
	private Text blockNameText;
	private Button updateBlocksButton;
	private Label moduleLabel;
	private Combo moduleCombo;
	private Button distributeButton;
	private Label locationLabel;
	private Text locationsText;
	private Button deployButton;
	private Button saveConstraintsButton;
	private StyledText infoText;

	private boolean cancelDeploy;
	private TableItem selectedItem;
	private Map<TableItem, FunctionBlockModel> itemToBlock;
	private Map<FunctionBlockModel, TableItem> blockToItem;
	private List<String> infoTexts;

	public DeployEditorGraphics(Composite parent) {
		this.parent = parent;
		cancelDeploy = false;
		infoTexts = new LinkedList<String>();
		itemToBlock = new HashMap<TableItem, FunctionBlockModel>();
		blockToItem = new HashMap<FunctionBlockModel, TableItem>();
	}

	protected void initializeParent() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.horizontalSpacing = HORIZONTAL_SPACE;
		layout.verticalSpacing = VERTICAL_SPACE;
		parent.setLayout(layout);
	}

	protected void initializeWidgets(final DeployEditor view) {
		createAppNameLabel();
		createServerButton();
		createBlockModelSpecsLabel();
		createDeploymentTable();
		createUpdateBlocksButton();
		createBlockNameLabel();
		createBlockNameText();
		createUpdateModulesButton();
		createModuleLabel();
		createModuleCombo();
		createDistributionButton();
		createLocationLabel();
		createLocationsText();
		createDeployButton();
		createConstraintsButton();
		createInformationText();

		createListeners(view);
	}

	private Label createAppNameLabel() {
		appNameLabel = new Label(parent, SWT.NONE);
		appNameLabel.setText(Messages.DEPLOYGRAPHICS_LOADING_APPLICATION);
		appNameLabel.pack();
		return appNameLabel;
	}

	protected String getAppName() {
		return appNameLabel.getText();
	}

	protected void setAppName(final String name) {
		appNameLabel.setText(name);
	}

	// Deployment Table

	private Table createDeploymentTable() {
		GridData data = new GridData();
		data.verticalSpan = 5;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		deploymentTable = new Table(parent, SWT.NONE);
		deploymentTable.setLinesVisible(true);
		deploymentTable.setHeaderVisible(true);
		deploymentTable.setLayoutData(data);

		TableColumn column0 = new TableColumn(deploymentTable, SWT.None);
		column0.setText(Messages.DEPLOYGRAPHICS_FUNCTIONBLOCK);
		TableColumn column1 = new TableColumn(deploymentTable, SWT.NONE);
		column1.setText(Messages.DEPLOYGRAPHICS_MODULE);
		column1.setToolTipText(Messages.DEPLOY_COLUMN1_TOOLTIP);
		TableColumn column2 = new TableColumn(deploymentTable, SWT.NONE);
		column2.setText(Messages.DEPLOYGRAPHICS_LOCATION);
		column2.setToolTipText(Messages.DEPLOY_COLUMN2_TOOLTIP);
		TableColumn column3 = new TableColumn(deploymentTable, SWT.NONE);
		column3.setText(Messages.DEPLOYGRAPHICS_DISTRIBUTED_ON);
		column3.setToolTipText(Messages.DEPLOY_COLUMN3_TOOLTIP);
		TableColumn column4 = new TableColumn(deploymentTable, SWT.NONE);
		column4.setText(Messages.DEPLOYGRAPHICS_DISTRIBUTED_AT);
		column4.setToolTipText(Messages.DEPLOY_COLUMN4_TOOLTIP);

		deploymentTable.getColumn(BLOCKNAME).pack();
		deploymentTable.getColumn(USER_MODULE).pack();
		deploymentTable.getColumn(USER_LOCATION).pack();
		deploymentTable.getColumn(DIST_MODULE).pack();
		deploymentTable.getColumn(DIST_LOCATION).pack();

		return deploymentTable;
	}

	// BUTTONS: Server Button

	private Button createServerButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		serverButton = new Button(parent, SWT.NONE);
		serverButton.setLayoutData(data);
		if (Activator.getDefault().getServerManager().isRunning()) {
			serverButton.setText(Messages.DEPLOYGRAPHICS_STOP_SERVER);
		} else {
			serverButton.setText(Messages.DEPLOYGRAPHICS_START_SERVER);
		}
		return serverButton;

	}

	// Update Blocks Button

	private Button createUpdateBlocksButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateBlocksButton = new Button(parent, SWT.NONE);
		updateBlocksButton.setLayoutData(data);
		updateBlocksButton.setText(Messages.DEPLOYGRAPHICS_UPDATE_BLOCKS);
		updateBlocksButton.setToolTipText(Messages.DEPLOY_UPDATEBLOCKS_TOOLTIP);
		updateBlocksButton.pack();
		return updateBlocksButton;
	}

	// Update Modules Button

	private Button createUpdateModulesButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateModulesButton = new Button(parent, SWT.NONE);
		updateModulesButton.setLayoutData(data);
		updateModulesButton.setText(Messages.DEPLOYGRAPHICS_UPDATE_MODULES);
		updateModulesButton.setToolTipText(Messages.DEPLOY_UPDATEMODULES_TOOLTIP);
		updateModulesButton.pack();
		return updateModulesButton;
	}

	// Create Distribution Button

	private Button createDistributionButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		distributeButton = new Button(parent, SWT.NONE);
		distributeButton.setLayoutData(data);
		distributeButton.setText(Messages.DEPLOYGRAPHICS_CREATE_DISTRIBUTION);
		distributeButton.setToolTipText(Messages.DEPLOY_CREATEDISTRIBUTION_TOOLTIP);
		return distributeButton;
	}

	// Deploy Button

	private Button createDeployButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 3;
		deployButton = new Button(parent, SWT.NONE);
		deployButton.setText(Messages.DEPLOYGRAPHICS_DEPLOYBUTTON_TEXT);
		deployButton.setToolTipText(Messages.DEPLOY_DEPLOYDISTRIBUTION_TOOLTIP);
		deployButton.setEnabled(false);
		deployButton.setLayoutData(data);
		return deployButton;
	}

	// BLOCK SPECIFICATIONS: Block Name

	private Label createBlockModelSpecsLabel() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		blockModelSpecsLabel = new Label(parent, SWT.NONE);
		blockModelSpecsLabel.setText(Messages.DEPLOYGRAPHICS_BLOCK_SPECS);
		blockModelSpecsLabel.setToolTipText(Messages.DEPLOY_CONSTRAINTS_TOOLTIP);
		blockModelSpecsLabel.setLayoutData(data);
		blockModelSpecsLabel.pack();
		return blockModelSpecsLabel;
	}

	private Label createBlockNameLabel() {
		blockNameLabel = new Label(parent, SWT.NONE);
		blockNameLabel.setText(Messages.DEPLOYGRAPHICS_BLOCK_NAME);
		blockNameLabel.pack();
		return blockNameLabel;
	}

	private Text createBlockNameText() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		blockNameText = new Text(parent, SWT.NONE);
		blockNameText.setLayoutData(data);
		blockNameText.setText(Messages.DEPLOYGRAPHICS_SELECT_BLOCK);
		blockNameText.setToolTipText(Messages.DEPLOY_RENAMEBLOCK_TOOLTIP);
		blockNameText.setEnabled(false);
		blockNameText.pack();
		return blockNameText;

	}

	protected String getBlockNameText() {
		return blockNameText.getText();
	}

	// ModuleInfo Combo

	private Label createModuleLabel() {
		moduleLabel = new Label(parent, SWT.NONE);
		moduleLabel.setText(Messages.DEPLOYGRAPHICS_MODULE); //$NON-NLS-1$
		moduleLabel.setToolTipText(Messages.DEPLOY_SELECTMODULE_TOOLTIP);
		moduleLabel.pack();
		return moduleLabel;
	}

	private Combo createModuleCombo() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.setLayoutData(data);
		moduleCombo.setToolTipText(Messages.DEPLOY_SELECTMODULE_TOOLTIP);
		moduleCombo.add(Messages.DEPLOYGRAPHICS_EMPTYSTRING);
		moduleCombo.setEnabled(false);
		return moduleCombo;
	}

	protected int getModuleComboIndex() {
		return moduleCombo.getSelectionIndex();
	}

	protected String getItemFromModuleCombo(final int index) {
		return moduleCombo.getItem(index);
	}

	protected void selectInModuleCombo(final int index) {
		moduleCombo.select(index);
	}

	protected void addToModuleCombo(final String text) {
		moduleCombo.add(text);
	}

	protected void setItemToModuleCombo(final int index, final String text) {
		moduleCombo.setItem(index, text);
	}

	protected void removeFromModuleCombo(final int index) {
		moduleCombo.remove(index);
	}

	// locations Text

	private Label createLocationLabel() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 2;
		locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setLayoutData(data);
		locationLabel.setText(Messages.DEPLOYGRAPHICS_LOCATION); //$NON-NLS-1$
		locationLabel.setToolTipText(Messages.DEPLOY_SELECTLOCATION_TOOLTIP);
		locationLabel.pack();
		return locationLabel;
	}

	private Text createLocationsText() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		locationsText = new Text(parent, SWT.NONE);
		locationsText.setToolTipText(Messages.DEPLOY_SELECTLOCATION_TOOLTIP);
		locationsText.setLayoutData(data);
		locationsText.setEnabled(false);
		return locationsText;
	}

	protected String getLocationsText() {
		return locationsText.getText();
	}

	// Constraints Button

	private Button createConstraintsButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		saveConstraintsButton = new Button(parent, SWT.NONE);
		saveConstraintsButton.setLayoutData(data);
		saveConstraintsButton.setText(Messages.DEPLOYGRAPHICS_SAVE_CONSTRAINTS);
		saveConstraintsButton.setEnabled(false);
		saveConstraintsButton.pack();
		return saveConstraintsButton;
	}

	// InfoText

	private StyledText createInformationText() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		infoText = new StyledText(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		infoText.setLayoutData(data);
		infoText.setEditable(false);
		infoText.pack();
		return infoText;
	}

	protected void addNewInfoText(final String text) {
		String newText = text;
		infoTexts.add(newText);
		newText = newText.concat(Messages.DEPLOYGRAPHICS_NEWLINE);
		newText = newText.concat(infoText.getText());
		infoText.setText(newText);
		limitInfoText();
	}

	protected void replaceInfoText(final String text) {
		infoTexts.clear();
		infoTexts.add(text);
		infoText.setText(text);
		limitInfoText();
	}

	private void limitInfoText() {
		while (infoText.getLineCount() > 20 && !infoTexts.isEmpty()) {
			String oldText = infoTexts.remove(0);
			int newInfoLength = infoText.getText().length() - 1 - oldText.length();
			infoText.setText(infoText.getText().substring(0, newInfoLength));
		}
	}

	/**
	 * From down here: Methods concerning more than one widget.
	 */

	/**
	 * Adds a new item representing a block model to the deployment Table.
	 * 
	 * @param blockName
	 *            Name of the Block
	 * @param position
	 *            (optional) assigned Position
	 * @param model
	 *            the model represented by the item.
	 * @return the created item.
	 */
	protected TableItem createDeploymentItem(final String blockName, final String position,
			final FunctionBlockModel model) {
		TableItem item = new TableItem(deploymentTable, SWT.NONE);
		item.setText(BLOCKNAME, blockName == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : blockName); //$NON-NLS-1$
		item.setText(USER_LOCATION, position == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : position); //$NON-NLS-1$
		addItemAndModel(item, model);
		return item;
	}

	protected void disposeDeploymentItem(final FunctionBlockModel model) {
		TableItem item = blockToItem.get(model);
		removeItemAndModel(item, model);
		item.dispose();
	}

	/**
	 * Replaces the BlockName and user-assigned position of an item.
	 * 
	 * @param item
	 *            Item to modify
	 * @param newName
	 *            new BlockName (can be null / the old one)
	 * @param newPosition
	 *            new Position (can be null / the old one)
	 */
	protected void replaceBlock(final String newName, final String newLocation, final FunctionBlockModel oldModel,
			final FunctionBlockModel newModel) {
		TableItem item = blockToItem.get(oldModel);
		item.setText(BLOCKNAME, newName == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : newName); //$NON-NLS-1$
		item.setText(USER_LOCATION, newLocation == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : newLocation); //$NON-NLS-1$
		removeItemAndModel(item, oldModel);
		addItemAndModel(item, newModel);
	}

	protected void displayConstraints(final String blockName, final String module, final String location) {
		selectedItem.setText(BLOCKNAME, blockName == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : blockName); //$NON-NLS-1$
		selectedItem.setText(USER_MODULE, module == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : module); //$NON-NLS-1$
		selectedItem.setText(USER_LOCATION, location == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : location); //$NON-NLS-1$
	}

	protected void modifyDistributionInfo(final FunctionBlockModel model, final String module, final String location) {
		TableItem item = blockToItem.get(model);
		item.setText(DIST_MODULE, module == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : module); //$NON-NLS-1$
		item.setText(DIST_LOCATION, location == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : location); //$NON-NLS-1$
	}

	protected void distributionCreated() {
		deployButton.setEnabled(true);
		addNewInfoText(Messages.DEPLOYGRAPHICS_DISTRIBUTION_CREATED);
	}

	protected void deploymentStarted() {
		deployButton.setText(Messages.DEPLOYGRAPHICS_CANCEL_DEPLOYMENT);
		cancelDeploy = true;
	}

	/**
	 * Called after a deployment is finished.
	 * 
	 * @param success
	 *            true if deploying finished successful, false if not.
	 */
	protected void deploymentFinished(final boolean success) {
		if (success) {
			addNewInfoText(Messages.DEPLOYGRAPHICS_DEPLOYMENT_COMPLETE);
		} else {
			addNewInfoText(Messages.DEPLOYGRAPHICS_DEPLOYMENT_FAILED);
		}
		cancelDeploy = false;
		deployButton.setEnabled(false);
		deployButton.setText(Messages.DEPLOYGRAPHICS_DEPLOYBUTTON_TEXT); //$NON-NLS-1$
	}

	protected void resetDeployment() {
		for (TableItem item : deploymentTable.getItems()) {
			item.setText(DIST_MODULE, Messages.DEPLOYGRAPHICS_EMPTYSTRING); //$NON-NLS-1$
			item.setText(DIST_LOCATION, Messages.DEPLOYGRAPHICS_EMPTYSTRING); //$NON-NLS-1$
		}
		deployButton.setEnabled(false);
	}

	protected void moduleRenamed(final FunctionBlockModel model, final String text) {
		TableItem item = blockToItem.get(model);
		item.setText(USER_MODULE, text);
	}

	protected void blockSelected(final boolean modulesAvailable) {
		if (modulesAvailable) {
			moduleCombo.setEnabled(true);
		}
		locationsText.setEnabled(true);
		saveConstraintsButton.setEnabled(true);
		blockNameText.setEnabled(true);

		TableItem[] items = deploymentTable.getSelection();
		if (items.length == 1) {
			selectedItem = items[0];
		}
	}

	protected FunctionBlockModel getSelectedBlock() {
		if (selectedItem == null || !itemToBlock.containsKey(selectedItem)) {
			return null;
		}
		FunctionBlockModel model = itemToBlock.get(selectedItem);
		return model;
	}

	protected void updateBlockSelection(final String newPosition, final String newBlockName, final int moduleSelection) {
		locationsText.setText(newPosition == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : newPosition); //$NON-NLS-1$
		blockNameText.setText(newBlockName == null ? Messages.DEPLOYGRAPHICS_EMPTYSTRING : newBlockName); //$NON-NLS-1$
		if (moduleSelection > -1) {
			selectInModuleCombo(moduleSelection);
		}
	}

	protected void resetBlockSelection() {
		selectedItem = null;
		locationsText.setText(Messages.DEPLOYGRAPHICS_EMPTYSTRING); //$NON-NLS-1$
		blockNameText.setText(Messages.DEPLOYGRAPHICS_SELECT_BLOCK); //$NON-NLS-1$
		blockNameText.setEnabled(false);
		moduleCombo.setEnabled(false);
		locationsText.setEnabled(false);
		saveConstraintsButton.setEnabled(false);
	}

	protected void serverOnline() {
		if (serverButton != null) {
			serverButton.setText(Messages.DEPLOYGRAPHICS_STOP_SERVER); //$NON-NLS-1$
		}
		updateModulesButton.setEnabled(true);
		distributeButton.setEnabled(true);
		moduleCombo.setToolTipText(Messages.DEPLOYGRAPHICS_EMPTYSTRING); //$NON-NLS-1$
		if (infoText != null && infoTexts != null) {
			addNewInfoText(Messages.DEPLOYGRAPHICS_SERVER_ONLINE);
		}
	}

	protected void serverOffline() {
		if (serverButton != null) {
			serverButton.setText(Messages.DEPLOYGRAPHICS_START_SERVER); //$NON-NLS-1$
		}
		updateModulesButton.setEnabled(false);
		distributeButton.setEnabled(false);
		moduleCombo.setToolTipText(Messages.DEPLOY_SELECTMODULEOFFLINE_TOOLTIP);
		if (infoText != null && infoTexts != null) {
			addNewInfoText(Messages.DEPLOYGRAPHICS_SERVER_OFFLINE);
		}
	}

	private void addItemAndModel(final TableItem item, final FunctionBlockModel model) {
		itemToBlock.put(item, model);
		blockToItem.put(model, item);
	}

	private void removeItemAndModel(final TableItem item, final FunctionBlockModel model) {
		itemToBlock.remove(item);
		blockToItem.remove(model);
	}

	private void createListeners(final DeployEditor view) {
		serverButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.toggleServer();
			}
		});

		deploymentTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.blockModelSelected();
			}
		});

		updateModulesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.updateModules();
			}
		});

		updateBlocksButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.updateBlocks();
			}
		});

		moduleCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.moduleSelected();
			}
		});

		distributeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.distribute();
			}
		});

		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cancelDeploy) {
					DeployEditorProgress.cancelDeploying();
					deploymentFinished(false);
				} else {
					view.deploy();
				}
			}
		});

		saveConstraintsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				view.saveConstraints();
			}
		});

		blockNameText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == ENTER) {
					view.saveConstraints();
				}
			}
		});

		locationsText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == ENTER) {
					view.saveConstraints();
				}
			}
		});
	}
}
