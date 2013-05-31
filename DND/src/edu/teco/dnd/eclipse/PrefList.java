package edu.teco.dnd.eclipse;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PrefList extends ListEditor {
	private List<Text> textList;	
	private List<String> preferences = new ArrayList<String>();
	
	public PrefList(String name, String labelText, Composite parent, List<Text> textList){
		super(name, labelText, parent);
		this.textList = textList;
		
	}
	
	protected String createList(String[] items) {
		String str = "";
		for (int i = 0; i < items.length; i++){
			items[i].replaceAll(" ", "");
			str.concat(items[i]);
			str.concat(" ");
		}
		return str.trim();
	}

	@Override
	protected String getNewInputObject() {
		StringBuilder builder = new StringBuilder();
		
		for (Text text : textList){
			builder.append(":");
			builder.append(text.getText());
		}
		preferences.add(builder.toString().substring(1));
		return builder.toString().substring(1);
	}

	@Override
	protected String[] parseString(String stringList) {
		if (stringList.isEmpty()){
			return new String[0];
		}
		return stringList.split(" ");
	}
	
	/**
	 * Adds list of Texts for this PrefList
	 * @param textList List of Text fields to be added. ArrayList would be good.
	 */
	public void addList(List<Text> textList){
		this.textList = textList;
	}
	
	protected String[] listToArray(){
		String[] text = new String[preferences.size()];
		for (int i = 0; i < text.length; i++){
			text[i] = preferences.get(i);
		}
		return text;
	}
	
	@Override
	protected org.eclipse.swt.widgets.List getList(){
		return (org.eclipse.swt.widgets.List) preferences;
	}
}
