package edu.teco.dnd.eclipse;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class Preferences extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  public Preferences() {
    super(GRID);

  }

  public void createFieldEditors() {
	  Composite parent = getFieldEditorParent();
	  addField(new PrefList("AddrAndPorts", "Addresses and Ports for Eclipse", parent));
	  addField(new PrefList("Multicast", "Multicast Addresses", parent));
	  addField(new PrefList("MulticastContent", "Announce via Multicast", parent));
	  addField(new StringFieldEditor("MySTRING2", "A &text preference:",
		        getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("Set your Network Preferences here");
  }

} 