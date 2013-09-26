package edu.teco.dnd.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.teco.dnd.eclipse.Activator;

/**
 * This class manages the general Preferences for the DND eclipse plugin.
 */

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.Preferences_GENERAL_SETTINGS);
	}

	@Override
	protected void createFieldEditors() {

	}

}