package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>
 * An Input for a {@link FunctionBlock}. A FunctionBlock can receive objects from other FunctionBlock’s {@link Output}s
 * via Inputs. To define an input simply define a non-static field of this type. It will be initialized by the wrapper
 * code, so you should never assign the field directly.
 * </p>
 * 
 * <p>
 * In normal operation the input buffers all values it receives. Use {@link #popValue()} to get the first unprocessed
 * value (the Input works as a Queue). You can use {@link #setNewestOnly(boolean)} to not keep old values. In that case
 * the input will only keep the newest value it received.
 * </p>
 * 
 * <p>
 * Input is thread-safe for all operations.
 * </p>
 * 
 * @param <T>
 *            the type of values the Input can receive
 */
public class Input<T extends Serializable> implements OutputTarget<T> {
	private boolean newestOnly = false;

	// TODO: Maybe change to (Soft)Reference, but then a wrapper is needed for null
	private final Deque<T> values = new ArrayDeque<T>();

	/**
	 * Returns the oldest unprocessed value and removes it. If there is no value left, null will be returned.
	 * 
	 * @return the oldest unprocessed value or null if no value
	 * @see #hasMoreValues()
	 */
	public synchronized T popValue() {
		return values.poll();
	}

	/**
	 * Checks if there are unprocessed values left.
	 * 
	 * @return <code>true</code> if there are still values that can be retrieved via {@link #popValue()}
	 */
	public synchronized boolean hasMoreValues() {
		return !values.isEmpty();
	}

	/**
	 * Adds a new value to the input. If {@link #setNewestOnly(boolean)} was called with <code>true</code> before, any
	 * old values will be removed. The method is called <code>setValue</code> instead of something more fitting like
	 * <code>addValue</code> to comply to the {@link OutputTarget} interface.
	 * 
	 * @param value
	 *            the new value to be added
	 */
	public synchronized void setValue(T value) {
		// TODO check for null
		if (newestOnly) {
			values.clear();
		}
		values.add(value);
	}

	/**
	 * Switches between keeping all values and keeping only the newest value. By default the input keeps all values
	 * until they’re retrieved with {@link #popValue()}. If this method is called with <code>true</code>, only the
	 * newest value will be kept and older values will be discarded as soon as a newer one arrives.
	 * 
	 * @param state
	 *            if <code>true</code> only the newest value will kept in future. Also all values currently held will be
	 *            removed except for the newest one. If set to <code>false</code> this Input will keep all values in
	 *            future.
	 */
	public synchronized void setNewestOnly(final boolean state) {
		this.newestOnly = state;
		if (newestOnly && !values.isEmpty()) {
			final T value = values.peekLast();
			values.clear();
			values.add(value);
		}
	}
}
