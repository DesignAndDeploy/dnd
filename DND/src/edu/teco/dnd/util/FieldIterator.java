package edu.teco.dnd.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

/**
 * Iterates over the {@link Field}s of a {@link JavaClass}.
 */
public class FieldIterator implements Iterator<Field> {
	final Queue<Field> fields = new LinkedList<Field>();

	public FieldIterator(final JavaClass cls) throws ClassNotFoundException {
		for (JavaClass currentClass = cls; currentClass != null; currentClass = currentClass.getSuperClass()) {
			fields.addAll(Arrays.asList(cls.getFields()));
		}
	}

	@Override
	public boolean hasNext() {
		return !fields.isEmpty();
	}

	@Override
	public Field next() {
		return fields.remove();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
