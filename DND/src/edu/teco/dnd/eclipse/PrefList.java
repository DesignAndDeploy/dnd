package edu.teco.dnd.eclipse;

import java.util.List;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * List for Preferences, later used for several network preferences. The List of preferences to be administrated by this
 * class is given to the constructor as a CheckText list containing Text fields. The user can enter values in these Text
 * fields and submit them via the interface provided by this class. Plus, PrefList checks if the values entered by the
 * user are valid and match their assigned formats.
 * 
 * @author jung
 * 
 */
public class PrefList extends ListEditor {
	private List<TextCheck> textList;
	private Composite parent;

	/**
	 * Constructor to create a new PrefList
	 * 
	 * @param name
	 *            Name of the Preference this PrefList works on
	 * @param labelText
	 *            (I'm not sure what this is)
	 * @param parent
	 *            Composite this PrefList is positioned in
	 * @param textList
	 *            CheckText List containing Text fields containing values for the PrefList
	 */
	public PrefList(String name, String labelText, Composite parent, List<TextCheck> textList) {
		super(name, labelText, parent);
		this.parent = parent;
		this.textList = textList;

	}

	protected String createList(String[] items) {
		final StringBuilder builder = new StringBuilder();
		for (final String item : items) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(item.replaceAll(" ", ""));
		}
		return builder.toString();
	}

	@Override
	protected String getNewInputObject() {
		StringBuilder builder = new StringBuilder();
		for (TextCheck text : textList) {
			if (!text.check()) {
				text.warn();
				return null;
			}
			builder.append(":");
			builder.append(text.getText());
		}
		return builder.toString().substring(1);
	}

	@Override
	protected String[] parseString(String stringList) {
		if (stringList.isEmpty()) {
			return new String[0];
		}
		return stringList.split(" ");
	}

	/**
	 * Standard functionality: Enable / Disable Up and Down depending on selectionIndex
	 * 
	 * Added functionality: show selected row in text fields above. Allows user to edit preferences that have been set
	 * before.
	 */
	@Override
	protected void selectionChanged() {
		if (parent != null) { // check necessary since parent is null first for
								// a short time
			org.eclipse.swt.widgets.List control = getListControl(parent);

			String[] select = control.getSelection();
			if (select.length == 1) {
				String[] entries = select[0].split(":");
				for (int i = 0; i < entries.length; i++) {
					textList.get(i).setText(entries[i]);
				}
			}

			if (control.getItems().length == 0) {
				getRemoveButton().setEnabled(false);
				getDownButton().setEnabled(false);
				getUpButton().setEnabled(false);
			}

			else {
				getRemoveButton().setEnabled(true);
				int index = control.getSelectionIndex();
				if (index == 0) {
					getUpButton().setEnabled(false);
				} else {
					getUpButton().setEnabled(true);
				}
				if (control.getItems().length == index + 1) {
					getDownButton().setEnabled(false);
				} else {
					getDownButton().setEnabled(true);
				}
			}
		}
	}
}
