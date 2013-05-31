package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class Preferences extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

	PrefList addrAndPorts;
	PrefList multi;
	PrefList multiContent;
	
	
	public Preferences() {
		super(GRID);
		
	}

 	public void createFieldEditors() {
 		Composite parent = getFieldEditorParent();
	  
 		addrAndPorts = initAddrAndPorts(parent);
 		multi = initMulticast(parent);
 		multiContent = initMultiContent(parent);
 		
 		addField(addrAndPorts);
 		addField(multi);
 		addField(multiContent);
 		}

  	@Override
  	public void init(IWorkbench workbench) {
  		setPreferenceStore(Activator.getDefault().getPreferenceStore());
  		setDescription("Set your Network Preferences here");
  	}
  	
  	@Override
  	protected IPreferenceStore doGetPreferenceStore(){
  		return Activator.getDefault().getPreferenceStore();
  	}
  	
  	//tut noch nicht
  	public boolean performOK(){
  		storeValues();
  		System.out.println("OK gedrückt");
  		return true;
  	}
  	
  	//tut noch nicht
  	private void storeValues() {
  		IPreferenceStore store = getPreferenceStore();
  		System.out.println("Saving in progress...");
  		store.setValue("AddrAndPorts", "blu");
  	}  	
  	
  	
  	
  	private PrefList initAddrAndPorts(Composite parent){
  		Label lAddr = new Label(parent, NONE);
  		lAddr.setText("Address for Eclipse:");
  		Text addr = new Text(parent, NONE);
 		addr.setToolTipText("Address for Eclipse");
 		addr.setSize(20, 20);
 		
 		Label lPort = new Label(parent, NONE);
 		lPort.setText("Port for Eclipse:");
 		Text port= new Text(parent, NONE);
 		port.setToolTipText("Port for Eclipse");
 		
 		List<Text> addrAndPorts = new ArrayList<Text>();
 		addrAndPorts.add(addr);
 		addrAndPorts.add(port);
 		
 		return new PrefList("AddrAndPorts", "", parent, addrAndPorts);
  	}
  	
  	private PrefList initMulticast(Composite parent){
  		Label lAddr = new Label(parent, NONE);
  		lAddr.setText("Address for Multicast");
  		Text addr = new Text(parent, NONE);
 		addr.setToolTipText("Address for multicasts");
 		
 		Label lPort = new Label(parent, NONE);
 		lPort.setText("Port for Multicast:");
 		Text port= new Text(parent, NONE);
 		port.setToolTipText("Port for multicasts");
 		
 		Label lNetwork = new Label(parent, NONE);
 		lNetwork.setText("Network for Multicast");
 		Text network = new Text(parent, NONE);
 		network.setToolTipText("Network interface");
 		
 		List<Text> multi = new ArrayList<Text>();
 		multi.add(addr);
 		multi.add(port);
 		multi.add(network);
 		
  		return new PrefList("Multicast", "", parent, multi);
  	}
  	
  	private PrefList initMultiContent(Composite parent){
  		Label lAddr = new Label(parent, NONE);
  		lAddr.setText("Address to send via multicast:");
  		Text addr = new Text(parent, NONE);
 		addr.setToolTipText("Address for Eclipse");
 		addr.setSize(20, 20);
 		
 		Label lPort = new Label(parent, NONE);
 		lPort.setText("Port to send via multicast:");
 		Text port= new Text(parent, NONE);
 		port.setToolTipText("Port for Eclipse");
  		
  		List<Text> content = new ArrayList<Text>();
  		content.add(addr);
  		content.add(port);
  		
  		return new PrefList("MulticastContent", "", parent, content);
  	}

} 