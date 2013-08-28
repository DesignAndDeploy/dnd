package edu.teco.dnd.eclipse.deployView;

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
import edu.teco.dnd.server.ServerManager;

/**
 * This class is responsible for creating the graphical representations of the buttons, tables, text fields and so on.
 * It only provides the widgets to be used by the DeployView, not the functionality the user experiences while using. It
 * is also responsible for changing the appearance and contents of text fields while the user operates. them.
 * 
 * @author jung
 * 
 */
public class DeployViewGraphics {

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
	private Button createButton;
	private Label placeLabel;
	private Text placesText;
	private Button deployButton;
	private Button saveConstraintsButton;
	private StyledText infoText;

	private TableItem selectedItem;
	private Map<TableItem, FunctionBlockModel> itemToBlock;
	private Map<FunctionBlockModel, TableItem> blockToItem;
	private List<String> infoTexts;

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

	protected void initializeWidgets(DeployView view) {
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
		createCreateButton();
		createPlaceLabel();
		createPlacesText();
		createDeployButton();
		createConstraintsButton();
		createInformationText();

		createListeners(view);
	}

	protected Label createAppNameLabel() {
		appNameLabel = new Label(parent, SWT.NONE);
		appNameLabel.setText("Loading application...");
		appNameLabel.pack();
		return appNameLabel;
	}

	protected String getAppName() {
		return appNameLabel.getText();
	}

	protected void setAppName(String name) {
		appNameLabel.setText(name);
	}

	protected Button createServerButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		serverButton = new Button(parent, SWT.NONE);
		serverButton.setLayoutData(data);
		if (ServerManager.getDefault().isRunning()) {
			serverButton.setText("Stop server");
		} else {
			serverButton.setText("Start server");
		}
		return serverButton;

	}

	protected void setServerButtonText(String text) {
		serverButton.setText(text);
	}

	protected Label createBlockModelSpecsLabel() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		blockModelSpecsLabel = new Label(parent, SWT.NONE);
		blockModelSpecsLabel.setText("Block Specifications:");
		blockModelSpecsLabel.setToolTipText(DeployViewTexts.CONSTRAINTS_TOOLTIP);
		blockModelSpecsLabel.setLayoutData(data);
		blockModelSpecsLabel.pack();
		return blockModelSpecsLabel;
	}

	protected Table createDeploymentTable() {
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
		column0.setText("Function Block");
		TableColumn column1 = new TableColumn(deploymentTable, SWT.NONE);
		column1.setText("Module");
		column1.setToolTipText(DeployViewTexts.COLUMN1_TOOLTIP);
		TableColumn column2 = new TableColumn(deploymentTable, SWT.NONE);
		column2.setText("Place");
		column2.setToolTipText(DeployViewTexts.COLUMN2_TOOLTIP);
		TableColumn column3 = new TableColumn(deploymentTable, SWT.NONE);
		column3.setText("Deployed on:");
		column3.setToolTipText(DeployViewTexts.COLUMN3_TOOLTIP);
		TableColumn column4 = new TableColumn(deploymentTable, SWT.NONE);
		column4.setText("Deployed at:");
		column4.setToolTipText(DeployViewTexts.COLUMN4_TOOLTIP);

		deploymentTable.getColumn(BLOCKNAME).pack();
		deploymentTable.getColumn(USER_MODULE).pack();
		deploymentTable.getColumn(USER_LOCATION).pack();
		deploymentTable.getColumn(DIST_MODULE).pack();
		deploymentTable.getColumn(DIST_LOCATION).pack();

		return deploymentTable;
	}

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
	protected TableItem createDeploymentItem(String blockName, String position, FunctionBlockModel model) {
		TableItem item = new TableItem(deploymentTable, SWT.NONE);
		item.setText(BLOCKNAME, blockName == null ? "" : blockName);
		item.setText(USER_LOCATION, position == null ? "" : position);
		addItemAndModel(item, model);
		return item;
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
	protected void replaceDeploymentItem(String newName, String newLocation, FunctionBlockModel oldModel, FunctionBlockModel newModel) {
		TableItem item = blockToItem.get(oldModel);
		item.setText(BLOCKNAME, newName == null ? "" : newName);
		item.setText(USER_LOCATION, newLocation == null ? "" : newLocation);
		removeItemAndModel(item, oldModel);
		addItemAndModel(item, newModel);
	}

	protected void displayConstraints(String blockName, String module, String location){
		selectedItem.setText(BLOCKNAME, blockName == null ? "" : blockName);
		selectedItem.setText(USER_MODULE, module == null ? "" : module);
		selectedItem.setText(USER_LOCATION, location == null ? "" : location);
	}
	
	protected void modifyDistributionInfo(FunctionBlockModel model, String module, String location){
		TableItem item = blockToItem.get(model);
		item.setText(DIST_MODULE, module == null ? "" : module);
		item.setText(DIST_LOCATION, location == null ? "" : location);
	}

	protected void removeIDfromItem(FunctionBlockModel model){
		TableItem item = blockToItem.get(model);
		item.setText(USER_MODULE, "");
		item.setText(DIST_MODULE, "");
	}
	
	protected void disposeDeploymentItem(TableItem item) {
		FunctionBlockModel block = itemToBlock.get(item);
		removeItemAndModel(item, block);
		item.dispose();
	}
	
	protected void disposeDeploymentItem(FunctionBlockModel model){
		TableItem item = blockToItem.get(model);
		removeItemAndModel(item, model);
		item.dispose();
	}

	protected TableItem[] getDeploymentSelection() {
		TableItem[] items = deploymentTable.getSelection();
		if (items.length == 1){
			selectedItem = items[0];
		}
		return deploymentTable.getSelection();
	}

	protected TableItem[] getDeploymentItems() {
		return deploymentTable.getItems();
	}

	protected Button createUpdateModulesButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateModulesButton = new Button(parent, SWT.NONE);
		updateModulesButton.setLayoutData(data);
		updateModulesButton.setText("Update Modules");
		updateModulesButton.setToolTipText(DeployViewTexts.UPDATEMODULES_TOOLTIP);
		updateModulesButton.pack();
		return updateModulesButton;
	}

	protected void setUpdateModulesButtonEnabled(boolean enabled) {
		updateModulesButton.setEnabled(enabled);
	}

	protected Label createBlockNameLabel() {
		blockNameLabel = new Label(parent, SWT.NONE);
		blockNameLabel.setText("Name:");
		blockNameLabel.pack();
		return blockNameLabel;
	}

	protected Text createBlockNameText() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		blockNameText = new Text(parent, SWT.NONE);
		blockNameText.setLayoutData(data);
		blockNameText.setText("<select block on the left>");
		blockNameText.setToolTipText(DeployViewTexts.RENAMEBLOCK_TOOLTIP);
		blockNameText.setEnabled(false);
		blockNameText.pack();
		return blockNameText;

	}

	protected void setBlockNameEnabled(boolean enable) {
		blockNameText.setEnabled(enable);
	}

	protected void setBlockNameText(String text) {
		blockNameText.setText(text);
	}

	protected String getBlockNameText() {
		return blockNameText.getText();
	}

	protected Button createUpdateBlocksButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		updateBlocksButton = new Button(parent, SWT.NONE);
		updateBlocksButton.setLayoutData(data);
		updateBlocksButton.setText("Update Blocks");
		updateBlocksButton.setToolTipText(DeployViewTexts.UPDATEBLOCKS_TOOLTIP);
		updateBlocksButton.pack();
		return updateBlocksButton;
	}

	protected Label createModuleLabel() {
		moduleLabel = new Label(parent, SWT.NONE);
		moduleLabel.setText("Module:");
		moduleLabel.setToolTipText(DeployViewTexts.SELECTMODULE_TOOLTIP);
		moduleLabel.pack();
		return moduleLabel;
	}

	protected Combo createModuleCombo() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		moduleCombo = new Combo(parent, SWT.NONE);
		moduleCombo.setLayoutData(data);
		moduleCombo.setToolTipText(DeployViewTexts.SELECTMODULE_TOOLTIP);
		moduleCombo.add("");
		moduleCombo.setEnabled(false);
		return moduleCombo;
	}

	protected int getModuleComboIndex() {
		return moduleCombo.getSelectionIndex();
	}

	protected String getItemFromModuleCombo(int index) {
		return moduleCombo.getItem(index);
	}

	protected void selectInModuleCombo(int index) {
		moduleCombo.select(index);
	}

	protected void setModuleComboEnabled(boolean enable) {
		moduleCombo.setEnabled(enable);
	}

	protected void addToModuleCombo(String text) {
		moduleCombo.add(text);
	}

	protected void setItemToModuleCombo(int index, String text) {
		moduleCombo.setItem(index, text);
	}

	protected void removeFromModuleCombo(int index) {
		moduleCombo.remove(index);
	}

	protected Button createCreateButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		createButton = new Button(parent, SWT.NONE);
		createButton.setLayoutData(data);
		createButton.setText("Create Deployment");
		createButton.setToolTipText(DeployViewTexts.CREATE_TOOLTIP);
		return createButton;
	}

	protected void setCreateButtonEnabled(boolean enable) {
		createButton.setEnabled(enable);
	}

	protected Label createPlaceLabel() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 2;
		placeLabel = new Label(parent, SWT.NONE);
		placeLabel.setLayoutData(data);
		placeLabel.setText("Place:");
		placeLabel.setToolTipText(DeployViewTexts.SELECTPLACE_TOOLTIP);
		placeLabel.pack();
		return placeLabel;
	}

	protected Text createPlacesText() {
		GridData data = new GridData();
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalAlignment = SWT.FILL;
		placesText = new Text(parent, SWT.NONE);
		placesText.setToolTipText(DeployViewTexts.SELECTPLACE_TOOLTIP);
		placesText.setLayoutData(data);
		placesText.setEnabled(false);
		return placesText;
	}

	protected void setPlacesTextEnabled(boolean enable) {
		placesText.setEnabled(enable);
	}

	protected void setPlacesText(String text) {
		placesText.setText(text);
	}

	protected String getPlacesText() {
		return placesText.getText();
	}

	protected Button createDeployButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalSpan = 3;
		deployButton = new Button(parent, SWT.NONE);
		deployButton.setText("Deploy");
		deployButton.setToolTipText(DeployViewTexts.DEPLOY_TOOLTIP);
		deployButton.setLayoutData(data);
		return deployButton;
	}

	protected void setDeployButtonEnabled(boolean enable) {
		deployButton.setEnabled(enable);
	}

	protected Button createConstraintsButton() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		saveConstraintsButton = new Button(parent, SWT.NONE);
		saveConstraintsButton.setLayoutData(data);
		saveConstraintsButton.setText("Save constraints");
		saveConstraintsButton.setEnabled(false);
		saveConstraintsButton.pack();
		return saveConstraintsButton;
	}

	protected void setConstraintsButtonEnabled(boolean enable) {
		saveConstraintsButton.setEnabled(enable);
	}

	protected StyledText createInformationText() {
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

	protected void addNewInfoText(String text) {
		infoTexts.add(text);
		text = text.concat("\n");
		text = text.concat(infoText.getText());
		infoText.setText(text);
		limitInfoText();
	}

	protected void replaceInfoText(String text) {
		infoTexts.clear();
		infoTexts.add(text);
		infoText.setText(text);
		limitInfoText();
	}

	protected void limitInfoText() {
		while (infoText.getLineCount() > 20 && !infoTexts.isEmpty()) {
			String oldText = infoTexts.remove(0);
			int newInfoLength = infoText.getText().length() - 1 - oldText.length();
			infoText.setText(infoText.getText().substring(0, newInfoLength));
		}
	}

	/**
	 * From down here: Methods concerning more than one widget.
	 */

	protected void moduleRenamed(FunctionBlockModel model, String text){
		TableItem item = blockToItem.get(model);
		item.setText(USER_MODULE, text);
	}
	
	protected void resetDeployment(){
		deployButton.setEnabled(false);
		for (TableItem item : deploymentTable.getItems()) {
			item.setText(DIST_MODULE, "");
			item.setText(DIST_LOCATION, "");
		}
	}
	
	protected void blockSelected(){
		setPlacesTextEnabled(true);
		setConstraintsButtonEnabled(true);
		setBlockNameEnabled(true);
		
		TableItem[] items = deploymentTable.getSelection();
		if (items.length == 1){
			selectedItem = items[0];
		}
	}
	
	protected void addItemAndModel(TableItem item, FunctionBlockModel model) {
		itemToBlock.put(item, model);
		blockToItem.put(model, item);
	}
	
	protected void removeItemAndModel(TableItem item, FunctionBlockModel model){
		itemToBlock.remove(item);
		blockToItem.remove(model);
	}
	
	protected FunctionBlockModel getSelectedBlock(){
		TableItem[] items = getDeploymentSelection();
		FunctionBlockModel model = null;
		if (items.length == 1) {
			model = itemToBlock.get(items[0]);
		}
		return model;
	}
	
	protected void resetBlockSelection() {
		selectedItem = null;
		placesText.setText("");
		blockNameText.setText("<select block on the left>");
		blockNameText.setEnabled(false);
		moduleCombo.setEnabled(false);
		placesText.setEnabled(false);
		saveConstraintsButton.setEnabled(false);
	}

	protected void serverOnline() {
		if (serverButton != null) {
			serverButton.setText("Stop Server");
		}
		updateModulesButton.setEnabled(true);
		createButton.setEnabled(true);
		moduleCombo.setToolTipText("");
		if (infoText != null && infoTexts != null) {
			addNewInfoText("Server online.");
		}
	}

	protected void serverOffline() {
		if (serverButton != null) {
			serverButton.setText("Start Server");
		}
		updateModulesButton.setEnabled(false);
		createButton.setEnabled(false);
		moduleCombo.setToolTipText(DeployViewTexts.SELECTMODULEOFFLINE_TOOLTIP);
		if (infoText != null && infoTexts != null) {
			addNewInfoText("Server offline.");
		}
	}

	private void createListeners(DeployView view) {
		serverButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().toggleServer();
			}
		});

		deploymentTable.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().blockModelSelected();
			}
		});

		updateModulesButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().updateModules();
			}
		});

		updateBlocksButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().updateBlocks();
			}
		});

		moduleCombo.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().moduleSelected();
			}
		});

		createButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().create();
			}
		});

		deployButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().deploy();
			}
		});

		saveConstraintsButton.addSelectionListener(new DeploySelectionAdapter(view) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getView().saveConstraints();
			}
		});

		blockNameText.addKeyListener(new DeployKeyAdapter(view) {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == ENTER) {
					getView().saveConstraints();
				}
			}
		});

		placesText.addKeyListener(new DeployKeyAdapter(view) {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == ENTER) {
					getView().saveConstraints();
				}
			}
		});
	}
}
