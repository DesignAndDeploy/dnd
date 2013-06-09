package edu.teco.dnd.eclipse;

import java.util.List;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * List for Preferences, later used for several network preferences. The List of
 * preferences to be administrated by this class is given to the constructor as
 * a List of Text fields. The user can enter values in these text fields and
 * submit them via the interface provided by this class. Plus, PrefList checks
 * if the values entered by the user are valid. Therefore it is important that
 * the first Text field given to the PrefList is meant to hold IP Addresses.
 * 
 * @author jung
 * 
 */
public class PrefList extends ListEditor {
	private List<Text> textList;
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
	 *            List of Text fields containing values for the PrefList. Note:
	 *            It is important that the first Text field in this List should
	 *            be used for IP Addresses. Else PrefList won't approved the
	 *            values entered by user.
	 */
	public PrefList(String name, String labelText, Composite parent,
			List<Text> textList) {
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
		// TODO: Überprüfen, ob eingegebene Daten sinnvoll sind. Update: IP wird
		// auf allgemeines Format geprüft.
		// Hier wird schon davon ausgegangen, dass bei allen PrefLists das erste
		// Textfeld eine IP Addresse erhält! doof? zu speziell?
		if (!isIP(textList.get(0).getText())) {
			warnWrongIP();
			return null;
		}
		for (Text text : textList) {
			builder.append(":");
			builder.append(text.getText());
			isIP(text.getText());
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
	 * Standard functionality: Enable / Disable Up and Down depending on
	 * selectionIndex Added functionality: show selected row in text fields
	 * above. Allows to edit preferences that have been set before.
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

	/**
	 * Checks if given String has valid IP format *
	 * 
	 * @param string
	 *            String to check
	 * @return
	 */
	private boolean isIP(String string) {
		String[] numbers = string.split("\\.");
		if (numbers.length != 4) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			int number = -1;
			try {
				number = Integer.parseInt(numbers[i]);
			} catch (NumberFormatException e) {
			}

			if (number < 0 || number > 255) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Open Window to warn user about invalid IP format
	 */
	private void warnWrongIP() {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		dialog.setText("Warning");
		dialog.setMessage("Invalid IP Address");
		dialog.open();
	}
}
