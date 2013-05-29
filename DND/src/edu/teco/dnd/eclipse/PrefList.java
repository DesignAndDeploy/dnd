package edu.teco.dnd.eclipse;


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

	public PrefList(String name, String labelText, Composite parent){
		super(name, labelText, parent);
	}
	
	@Override
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
		final String text = "";
		Display display = new Display();
		Shell shell = new Shell(display);
		final Text addr = new Text(shell, SWT.NONE);
		addr.setText("<Address>");
		addr.setToolTipText("IPv4 or IPv6 Address");
		final Text port = new Text(shell, SWT.NONE);
		port.setText("<Port>");
		port.setToolTipText("Number between 0 and some 65k");
		port.setLocation(0,20);
		addr.pack();
		port.pack();
			
		return "text";
	}

	@Override
	protected String[] parseString(String stringList) {
		if (stringList.isEmpty()){
			return new String[0];
		}
		return stringList.split(" ");
	}
	
	private String getUserInput(final Text addr, final Text port){
		String text = addr.getText();
		text.concat(":");
		text.concat(port.getText());
		return text;
	}


	
}
