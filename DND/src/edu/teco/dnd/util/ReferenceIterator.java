package edu.teco.dnd.util;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator iterates over a Collection of References and returns all referenced values that are not null. That is, if an entry in the
 * Collection is null it is skipped. If not the referenced value is retrieved. If it is null the entry is also skipped. If it is not the entry
 * is returned. remove is not supported by this implementation.
 * 
 * Note: Keeping this iterator when it still has more elements may result in one element of the Collection to be <em>not</em> garbage
 * collected as it will be reachable through this iterator.
 * 
 * @author Philipp Adolf
 */
public class ReferenceIterator<T> implements Iterator<T> {
	/**
	 * An iterator over References that is used as the source for this iterator.
	 */
	private final Iterator<Reference<T>> source;
	
	/**
	 * The next element (if one is cached) or null.
	 */
	private T next = null;
	
	/**
	 * Initializes a new ReferenceIterator using an Iterator over References.
	 * 
	 * @param source an Iterator over References to use as a source
	 */
	public ReferenceIterator(final Iterator<Reference<T>> source) {
		this.source = source;
	}
	
	/**
	 * Initializes a new ReferenceIterator that iterates over a collection
	 * 
	 * @param collection the collection to iterate over
	 */
	public ReferenceIterator(final Collection<Reference<T>> collection) {
		this(collection.iterator());
	}
	
	// if next is null the source iterator is searched for a Reference that returns a non-null value. If one is found next is set to that
	// value. If next is null and there are no more References in source, false is returned
	@Override
	public boolean hasNext() {
		while (next == null && source.hasNext()) {
			final Reference<T> reference = source.next();
			if (reference != null) {
				next = reference.get();
			}
		}
		return next != null;
	}

	// calls hasNext to look for the next element
	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final T result = next;
		next = null;
		return result;
	}

	/**
	 * Remove is not supported by this iterator.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
