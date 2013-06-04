package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class manages the network preferences for the DND eclipse plugin.
 */
public class PreferencesNetwork extends FieldEditorPreferencePage implements
 IWorkbenchPreferencePage {

	PrefList addrAndPorts;
	PrefList multi;
	PrefList announce;


 public PreferencesNetwork() {
	super(GRID);
	
}

	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();
  
		addrAndPorts = initAddrAndPorts(parent);
		multi = initMulticast(parent);
		announce = initMultiContent(parent);
		
		addField(addrAndPorts);
		addField(multi);
		addField(announce);
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
		
		PrefList prefList = new PrefList("listen", "", parent, addrAndPorts);
		prefList.setPreferenceStore(getPreferenceStore());
		
		return prefList;
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
		
		PrefList prefList = new PrefList("multicast", "", parent, multi);
		prefList.setPreferenceStore(getPreferenceStore());
		
		return prefList;
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
		
		PrefList prefList = new PrefList("announce", "", parent, content);
		prefList.setPreferenceStore(getPreferenceStore());
		
		return prefList;
	}

}
