package edu.teco.dnd.eclipse;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class manages the additional Preferences for this eclipse plugin.
 */

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General settings");
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor startServer = new BooleanFieldEditor("startServer", "Start server when plugin is loaded",
				NONE, getFieldEditorParent());
		startServer.setPreferenceStore(getPreferenceStore());
		addField(startServer);
	}

}