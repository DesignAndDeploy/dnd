package edu.teco.dnd.eclipse;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.network.TCPConnectionManager;

/**
 * Discovery View: Shows available modules and running function blocks in relation to each other.
 * @author jung
 *
 */
public class DiscoveryView extends ViewPart {
        private Label label;
        //TCPConnectionManager manager;
        
        public DiscoveryView() {
                super();
                //TODO: TCPConnectionManager initialisieren sobald lauffähig, dann modules darüberholen
        }
     public void setFocus() {
                label.setFocus();
        }
     
     public void createPartControl(Composite parent) {
                label = new Label(parent, 0);
                label.setText("Discovery");
                label.setToolTipText("Shows available modules and running function blocks");
        
               createModuleTable(parent);
               createFunctionBlockTable(parent);
     }
     
     /**
      * Creates a Table containing currently available modules.
      * @param parent Composite containing the table
      */
     private void createModuleTable(Composite parent){

         Table moduleTable = new Table(parent, 0);
         moduleTable.setLinesVisible(true);
         moduleTable.setHeaderVisible(true);
         
         TableColumn column = new TableColumn(moduleTable, SWT.None);
         column.setText("Available Modules");
         moduleTable.setToolTipText("Currently available modules");
         moduleTable.getColumn(0).pack();

         Set<UUID> modules = getModules();
         
         for (UUID module : modules){
         	TableItem item = new TableItem(moduleTable, SWT.NONE);
         	item.setText(0, module.toString());
         }
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
     
    /**
      * Used to get the Set of currently running modules. Now random, to be replaced by TCPConnectionManager later on.
      * Can even be destroyed as soon as TCPConnectionManager available and replaced by one line.
      * @return currently running modules
      */
     private Set<UUID> getModules(){
    	 //später: return manager.getConnectedModules(); bis dahin:
    	 Set<UUID> modules = new HashSet<UUID>();
    	 for (int i=0; i < 6; i++){
    		 modules.add(UUID.randomUUID());
    	 }    	 
    	 return modules;
     }
}