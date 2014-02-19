package edu.teco.dnd.util;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

/**
 * An Iterable that simply returns a {@link FieldIterator}.
 */
public class FieldIterable implements Iterable<Field> {
	private final JavaClass cls;

	public FieldIterable(final JavaClass cls) {
		this.cls = cls;
	}

	@Override
	public Iterator<Field> iterator() {
		try {
			return new FieldIterator(cls);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
