package edu.teco.dnd.eclipse;

import org.eclipse.swt.widgets.Widget;

/**
 * This class is a wrapper for {@link Widget#getData()} and {@link Widget#setData(Object)} that does type checking.
 * 
 * @author Philipp Adolf
 */
public class TypecastingWidgetDataStore<T> {
	private final Class<T> cls;
	private final String key;

	/**
	 * Initializes a new TypecastingWidgetDataStore.
	 * 
	 * @param cls
	 *            the class of the data
	 * @param key
	 *            the key that should be used to store the ApplicationInformation
	 */
	public TypecastingWidgetDataStore(final Class<T> cls, final String key) {
		this.cls = cls;
		this.key = key;
	}

	/**
	 * Stores a value on the given widget.
	 * 
	 * @param widget
	 *            the widget the value should be stored on
	 * @param value
	 *            the value to store
	 */
	public void store(final Widget widget, final T value) {
		widget.setData(key, value);
	}

	/**
	 * Retrieves a value from a widget. If there is no value or the value has an incompatible type, null is returned.
	 * 
	 * @param widget
	 *            the widget to get the value from
	 * @return the value stored on the object or null if there is no value or the value has an incompatible type
	 */
	@SuppressWarnings("unchecked")
	public T retrieve(final Widget widget) {
		final Object object = widget.getData(key);
		if (object == null) {
			return null;
		}

		final Class<?> objClass = object.getClass();
		if (cls.isAssignableFrom(objClass)) {
			return (T) object;
		}

		return null;
	}
}
