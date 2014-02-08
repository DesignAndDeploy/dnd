package edu.teco.dnd.eclipse.preferences;

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
	 * 
	 * These Strings are only used internally and never displayed to the user, therefore they are hard coded here and
	 * weren't moved to the messages.propterties files.
	 */
	public static final String BEACON_PREFERENCE = "beaconInterval";

	/**
	 * String defining the name of the preference that stores the listen configurations.
	 * 
	 * These Strings are only used internally and never displayed to the user, therefore they are hard coded here and
	 * weren't moved to the messages.propterties files.
	 */
	public static final String LISTEN_PREFERENCE = "listen";

	/**
	 * String defining the name of the preference that stores the multicast configurations.
	 * 
	 * These Strings are only used internally and never displayed to the user, therefore they are hard coded here and
	 * weren't moved to the messages.propterties files.
	 */
	public static final String MULTICAST_PREFERENCE = "multicast";

	/**
	 * String defining the name of the preference that stores the announce configurations.
	 * 
	 * These Strings are only used internally and never displayed to the user, therefore they are hard coded here and
	 * weren't moved to the messages.propterties files.
	 */
	public static final String ANNOUNCE_PREFERENCE = "announce";

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
		setDescription(Messages.PreferencesNetwork_DESCRIPTION);
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
		lAddr.setText(Messages.PreferencesNetwork_ADDRESS_FOR_ECLIPSE);
		Text addr = new Text(parent, NONE);
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText(Messages.PreferencesNetwork_PORT_FOR_ECLIPSE);
		Text port = new Text(parent, NONE);
		port.setLayoutData(data2);

		List<TextCheck> addrAndPorts = new ArrayList<TextCheck>();
		addrAndPorts.add(new TextAddress(addr));
		addrAndPorts.add(new TextPort(port));

		PrefList prefList = new PrefList(LISTEN_PREFERENCE, "", parent, addrAndPorts); //$NON-NLS-1$ //$NON-NLS-2$
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
		lAddr.setText(Messages.PreferencesNetwork_ADDRESS_FOR_MULTICASTS);
		Text addr = new Text(parent, NONE);
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText(Messages.PreferencesNetwork_PORT_FOR_MULTICASTS);
		Text port = new Text(parent, NONE);
		port.setLayoutData(data2);

		Label lNetwork = new Label(parent, NONE);
		lNetwork.setText(Messages.PreferencesNetwork_NETWORK_FOR_MULTICASTS);
		Text network = new Text(parent, NONE);
		network.setToolTipText(Messages.PreferencesNetwork_NETWORK_INTERFACE);
		network.setLayoutData(data3);

		List<TextCheck> multi = new ArrayList<TextCheck>();
		multi.add(new TextAddress(addr));
		multi.add(new TextPort(port));
		multi.add(new TextNetwork(network));

		PrefList prefList = new PrefList(MULTICAST_PREFERENCE, "", parent, multi); //$NON-NLS-1$ //$NON-NLS-2$
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
		lAddr.setText(Messages.PreferencesNetwork_ADDRESS_ANNOUNCE);
		Text addr = new Text(parent, NONE);
		addr.setLayoutData(data1);

		Label lPort = new Label(parent, NONE);
		lPort.setText(Messages.PreferencesNetwork_PORT_ANNOUNCE);
		Text port = new Text(parent, NONE);
		port.setLayoutData(data2);

		List<TextCheck> content = new ArrayList<TextCheck>();
		content.add(new TextAddress(addr));
		content.add(new TextPort(port));

		PrefList prefList = new PrefList(ANNOUNCE_PREFERENCE, "", parent, content); //$NON-NLS-1$ //$NON-NLS-2$
		prefList.setPreferenceStore(getPreferenceStore());

		return prefList;
	}

	private IntegerFieldEditor initBeaconInterval(Composite parent) {
		IntegerFieldEditor editor =
				new IntegerFieldEditor(BEACON_PREFERENCE, Messages.PreferencesNetwork_BEACON_INTERVAL, parent); //$NON-NLS-1$
		editor.setValidRange(1, Integer.MAX_VALUE);
		editor.setPreferenceStore(getPreferenceStore());
		getPreferenceStore().setDefault(BEACON_PREFERENCE, UDPMulticastBeacon.DEFAULT_INTERVAL);
		return editor;
	}

}
