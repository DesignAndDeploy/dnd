package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.network.ConnectionManager;

public class FunctionBlocksView extends ViewPart {
	 private Label label;
     ConnectionManager manager;
     
     public FunctionBlocksView() {
             super();
             manager = Activator.getDefault().getConnectionManager();
     }
  public void setFocus() {
             label.setFocus();
     }
  
  public void createPartControl(Composite parent) {
             label = new Label(parent, 0);
             label.setText("Modules");
             label.setToolTipText("Shows available modules");
     
            createFunctionBlockTable(parent);
  }
	
  
  private void createFunctionBlockTable(Composite parent){
 	 Table functTable = new Table(parent, 0);
 	 functTable.setLinesVisible(true);
 	 functTable.setHeaderVisible(true);
 	 
 	 TableColumn column = new TableColumn(functTable, SWT.NONE);
 	 column.setText("Running Function Blocks");
 	 functTable.setToolTipText("Currently running function blocks");
 	 functTable.getColumn(0).pack();
  }
}
