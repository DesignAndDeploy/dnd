package edu.teco.dnd.eclipse.prefs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.teco.dnd.eclipse.Activator;
import edu.teco.dnd.network.UDPMulticastBeacon;

/**
 * This class manages the network preferences for the DND eclipse plugin.
 */
public class PreferencesNetwork extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * String defining the name of the preference that stores the beacon interval in seconds.
	 */
	public static final String BEACON_INTERVAL = "beaconInterval";

	PrefList addrAndPorts;
	PrefList multi;
	PrefList announce;
	int beaconInterval;

	public PreferencesNetwork() {
		super(GRID);

	}

	public void createFieldEditors() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		Composite parent = getFieldEditorParent();
		parent.setLayout(layout);

		addrAndPorts = initAddrAndPorts(parent);
		multi = initMulticast(parent);
		announce = initMultiContent(parent);

		addField(initBeaconInterval(parent));
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
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	private PrefList initAddrAndPorts(Composite parent) {
		GridData data1 = new GridData();
		data1.horizontalAlignment = GridData.FILL;
		data1.grabExcessHorizontalSpace = true;
		GridData data2 = new GridData();
		data2.horizontalAlignment = GridData.FILL;
		data2.grabExcessHorizontalSpace = true;

		Label lAddr = new Label(parent, NONE);
		lAddr.setText("Address for Eclipse:");
		Text addr = new Text(parent, NONE);
		addr.setToolTipText("Address for Eclipse");
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText("Port for Eclipse:");
		Text port = new Text(parent, NONE);
		port.setToolTipText("Port for Eclipse");
		port.setLayoutData(data2);

		List<TextCheck> addrAndPorts = new ArrayList<TextCheck>();
		addrAndPorts.add(new TextAddress(addr));
		addrAndPorts.add(new TextPort(port));

		PrefList prefList = new PrefList("listen", "", parent, addrAndPorts);
		prefList.setPreferenceStore(getPreferenceStore());

		return prefList;
	}

	private PrefList initMulticast(Composite parent) {
		GridData data1 = new GridData();
		data1.horizontalAlignment = GridData.FILL;
		data1.grabExcessHorizontalSpace = true;
		GridData data2 = new GridData();
		data2.horizontalAlignment = GridData.FILL;
		data2.grabExcessHorizontalSpace = true;
		GridData data3 = new GridData();
		data3.horizontalAlignment = GridData.FILL;
		data3.grabExcessHorizontalSpace = true;

		Label lAddr = new Label(parent, NONE);
		lAddr.setText("Address for Multicast");
		Text addr = new Text(parent, NONE);
		addr.setToolTipText("Address for multicasts");
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText("Port for Multicast:");
		Text port = new Text(parent, NONE);
		port.setToolTipText("Port for multicasts");
		port.setLayoutData(data2);

		Label lNetwork = new Label(parent, NONE);
		lNetwork.setText("Network for Multicast");
		Text network = new Text(parent, NONE);
		network.setToolTipText("Network interface");
		network.setLayoutData(data3);

		List<TextCheck> multi = new ArrayList<TextCheck>();
		multi.add(new TextAddress(addr));
		multi.add(new TextPort(port));
		multi.add(new TextNetwork(network));

		PrefList prefList = new PrefList("multicast", "", parent, multi);
		prefList.setPreferenceStore(getPreferenceStore());

		return prefList;
	}

	private PrefList initMultiContent(Composite parent) {
		GridData data1 = new GridData();
		data1.horizontalAlignment = GridData.FILL;
		data1.grabExcessHorizontalSpace = true;
		GridData data2 = new GridData();
		data2.horizontalAlignment = GridData.FILL;
		data2.grabExcessHorizontalSpace = true;

		Label lAddr = new Label(parent, NONE);
		lAddr.setText("Address to send via multicast:");
		Text addr = new Text(parent, NONE);
		addr.setToolTipText("Address for Eclipse");
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText("Port to send via multicast:");
		Text port = new Text(parent, NONE);
		port.setToolTipText("Port for Eclipse");
		port.setLayoutData(data2);

		List<TextCheck> content = new ArrayList<TextCheck>();
		content.add(new TextAddress(addr));
		content.add(new TextPort(port));

		PrefList prefList = new PrefList("announce", "", parent, content);
		prefList.setPreferenceStore(getPreferenceStore());

		return prefList;
	}

	private IntegerFieldEditor initBeaconInterval(Composite parent) {
		IntegerFieldEditor editor = new IntegerFieldEditor(BEACON_INTERVAL, "Beacon Interval", parent);
		editor.setPreferenceStore(getPreferenceStore());
		getPreferenceStore().setDefault(BEACON_INTERVAL, UDPMulticastBeacon.DEFAULT_INTERVAL);
		return editor;
	}

}
