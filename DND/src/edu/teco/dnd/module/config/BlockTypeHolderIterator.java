package edu.teco.dnd.module.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Iterates over a given {@link BlockTypeHolder} and recursively all children of it. Does not support removing elements.
 */
public class BlockTypeHolderIterator implements Iterator<BlockTypeHolder> {
	/**
	 * Queue for BlockTypeHolders that have not yet been visited.
	 */
	private final Queue<BlockTypeHolder> holders = new LinkedList<BlockTypeHolder>();

	/**
	 * Creates a new iterator with a given root.
	 * 
	 * @param root
	 *            the root BlockTypeHolder
	 */
	public BlockTypeHolderIterator(final BlockTypeHolder root) {
		holders.add(root);
	}

	@Override
	public boolean hasNext() {
		return !holders.isEmpty();
	}

	@Override
	public BlockTypeHolder next() {
		if (holders.isEmpty()) {
			throw new NoSuchElementException();
		}

		final BlockTypeHolder next = holders.remove();
		holders.addAll(next.getChildren());
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}