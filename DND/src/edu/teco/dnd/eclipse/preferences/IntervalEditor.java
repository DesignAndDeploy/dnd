package edu.teco.dnd.eclipse.preferences;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This class represents an editor for the interval for beacons to send out. It basically is an IntegerFieldEditor that
 * doesn't allow 0 as a value.
 * 
 * @author jung
 * 
 */
public class IntervalEditor extends IntegerFieldEditor {

	public IntervalEditor(String beaconInterval, String string, Composite parent) {
		super(beaconInterval, string, parent);
	}

	@Override
	public boolean isValid() {
		return (super.isValid() && getIntValue() > 0);
	}

	@Override
	protected void refreshValidState() {
		super.refreshValidState();
		if (super.isValid()) {
			if (getIntValue() <= 0) {
				fireStateChanged(IS_VALID, true, false);
			} else if (getIntValue() > 0) {
				fireStateChanged(IS_VALID, false, true);
			}
		}
	}

}
