package edu.teco.dnd.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.teco.dnd.eclipse.Activator;

/**
 * The overall category for all preferences related to this plugin.
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