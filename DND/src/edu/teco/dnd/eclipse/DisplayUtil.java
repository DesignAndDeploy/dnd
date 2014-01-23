package edu.teco.dnd.eclipse;

import org.eclipse.swt.widgets.Display;

/**
 * Provides a utility method that can be used to retrieve the {@link Display} to use.
 * 
 * @author Philipp Adolf
 */
public final class DisplayUtil {
	private DisplayUtil() {
	}

	/**
	 * Returns the display to use. This is either the active Display, or if that is null, the default Display.
	 * 
	 * @return the display to use. May be null (iff {@link Display#getCurrent()} and {@link Display#getDefault()} both
	 *         return null.
	 */
	public static final Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}
}
