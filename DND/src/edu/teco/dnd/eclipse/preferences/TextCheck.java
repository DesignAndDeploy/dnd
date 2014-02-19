package edu.teco.dnd.eclipse.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Class to be inherited by Text wrappers so their content can be checked for validity before accessing it. Subclasses
 * should override the check() method check if the entry of their Text Field matches the required format. CheckText
 * itself can be used to wrap Text fields that don't require checking.
 * 
 * @author jung
 * 
 */
public abstract class TextCheck {
	private Text textField;
	private String warnMessage;

	/**
	 * Creates a new CheckText
	 * 
	 * @param text
	 *            TextField to be checked by this CheckText Object
	 */
	public TextCheck(Text text, String warnMessage) {
		this.textField = text;
		this.warnMessage = warnMessage;
	}

	/**
	 * Returns the String of the TextField.
	 * 
	 * @return String of Text field
	 */
	public String getText() {
		return textField.getText();
	}

	/**
	 * Sets content of Text field to the given text.
	 * 
	 * @param text
	 *            new text for the Text field
	 */
	public void setText(final String text) {
		textField.setText(text);
	}

	/**
	 * Checks if the string entered in the text field matches the format this Text field is supposed to contain. The
	 * default implementation in CheckText returns true by default, subclasses should implement their own algorithm to
	 * check if the text in their TextFields matches a given format.
	 * 
	 * @return true if string matches valid format, false if string has wrong format
	 */
	public abstract boolean check();

	/**
	 * Can be used to present a warning in case check() returned "false". This opens a simple window with a warning
	 * message, that may be clicked away by the user.
	 */
	public void warn() {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		dialog.setText(Messages.TextCheck_WARNING);
		dialog.setMessage(warnMessage);
		dialog.open();
	}

}
