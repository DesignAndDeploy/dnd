package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PrefList extends ListEditor {
	private List<Text> textList;
	private List<String> preferences = new ArrayList<String>();
	private Composite parent;

	public PrefList(String name, String labelText, Composite parent, List<Text> textList) {
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

		for (Text text : textList) {
			builder.append(":");
			builder.append(text.getText());
		}
		//TODO: Überprüfen, ob eingegebene Daten sinnvoll sind
		preferences.add(builder.toString().substring(1)); //wahrscheinlich unnötig
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
	 * Adds list of Texts for this PrefList
	 * 
	 * @param textList
	 *            List of Text fields to be added. ArrayList would be good.
	 */
	public void addList(List<Text> textList) {
		this.textList = textList;
	}

	/**
	 * Wahrscheinlich unnötig.
	 * @return
	 */
	protected String[] listToArray() {
		String[] text = new String[preferences.size()];
		for (int i = 0; i < text.length; i++) {
			text[i] = preferences.get(i);
		}
		return text;
	}
	
	/**
	 * Standard functionality: Enable / Disable Up and Down depending on selectionIndex
	 * Added functionality: show selected row in text fields above. Allows to edit preferences that have been set before.
	 */
	@Override
	protected void selectionChanged(){
		if (parent != null){
			org.eclipse.swt.widgets.List control = getListControl(parent);
			
			String[] select = control.getSelection();
			if (select.length == 1){
				String[] entries = select[0].split(":");
				for (int i = 0; i < entries.length; i++){
					textList.get(i).setText(entries[i]);
				}
			}
			if (control.getItems().length == 0){
				getRemoveButton().setEnabled(false);
				getDownButton().setEnabled(false);
				getUpButton().setEnabled(false);
			}
			else{
				getRemoveButton().setEnabled(true);
				int index = control.getSelectionIndex();
				if (index == 0){
					getUpButton().setEnabled(false);
				}
				else{
					getUpButton().setEnabled(true);
				}
				if (control.getItems().length == index + 1){
					getDownButton().setEnabled(false);
				}
				else{
					getDownButton().setEnabled(true);
				}
			}
		}
	}
}
