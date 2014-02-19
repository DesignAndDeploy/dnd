package edu.teco.dnd.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory that adds a unique number to each Thread's name. This factory is thread safe.
 */
public class IndexedThreadFactory implements ThreadFactory {
	private final String prefix;
	private final AtomicInteger index;

	/**
	 * Initializes a new IndexedThreadFactory that names threads by appending the current index to <code>prefix</code>
	 * and then incrementing the index. The index will start at <code>start</code>.
	 * 
	 * @param prefix
	 *            the prefix for the Threads' names
	 * @param start
	 *            the index of the first Thread that will be created
	 */
	public IndexedThreadFactory(final String prefix, final int start) {
		this.prefix = prefix;
		this.index = new AtomicInteger(start);
	}

	/**
	 * Initializes a new IndexedThreadFactory that starts the index at 0.
	 * 
	 * @param prefix
	 *            the prefix for the Thread's names
	 */
	public IndexedThreadFactory(final String prefix) {
		this(prefix, 0);
	}

	@Override
	public Thread newThread(final Runnable r) {
		return new Thread(r, prefix + index.getAndIncrement());
	}
}
