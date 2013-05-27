package edu.teco.dnd.eclipse;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.TCPConnectionManager;

/**
 * ModuleView: Shows available modules.
 * @author jung
 *
 */
public class ModuleView extends ViewPart {
	private Label label;
    ConnectionManager manager;
    private Table moduleTable;
    private Map<UUID, TableItem> map = new HashMap<UUID, TableItem>();
                
     public ModuleView() {
         super();
         manager = Activator.getDefault().getConnectionManager();
     }
        
     public void setFocus() {
    	 label.setFocus();
     }
     
     public void init(IViewSite site, IMemento memento) throws PartInitException{
    	 super.init(site, memento);
    	 manager = Activator.getDefault().getConnectionManager();
     }     
     
     public void createPartControl(Composite parent) {
          label = new Label(parent, 0);
          label.setText("Modules");
          label.setToolTipText("Shows available modules");
          createModuleTable(parent);
     }
     
     /**
      * Creates a Table containing currently available modules.
      * @param parent Composite containing the table
      */
     private void createModuleTable(Composite parent){
    	 moduleTable = new Table(parent, 0);
         moduleTable.setLinesVisible(true);
         moduleTable.setHeaderVisible(true);
         
         TableColumn column = new TableColumn(moduleTable, SWT.None);
         column.setText("Available Modules");
         moduleTable.setToolTipText("Currently available modules");
         moduleTable.getColumn(0).pack();

         
         synchronized(this){
        	 //Set<UUID> modules = getModules();
        	 Collection<UUID> modules = manager.getConnectedModules();
         
         	for (UUID moduleID : modules){
        	 connectionEstablished(moduleID);
         	}
         }
         
     }
   
   
     public synchronized void connectionEstablished(UUID id){
    	 if(!map.containsKey(id)){
    		  TableItem item = new TableItem(moduleTable, SWT.NONE);
    		  item.setText(0, id.toString());
    		  map.put(id, item);
    	 }
    	
     }
     
     public synchronized void connectionClosed(UUID id){
    	 TableItem item = map.get(id);
    	 if (item != null){
    		 moduleTable.remove(moduleTable.indexOf(item));
    		 map.remove(id);
    	 }
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