package edu.teco.dnd.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An extension of Java's Future that has the ability to notify listeners when the Future is finished.
 */
public interface FutureNotifier<V> extends Future<V> {
	/**
	 * Returns <code>true</code> if the Future has finished and did so successfully.
	 * 
	 * @return <code>true</code> if the Future finished successfully
	 * @see #isDone()
	 */
	boolean isSuccess();

	/**
	 * If the Future has finished, but failed and the cause is known, this method can be used to get the cause.
	 * 
	 * @return the reason why the Future failed if known, <code>null</code> otherwise
	 */
	Throwable cause();

	/**
	 * Adds a listener for this Future. If the Future has already finished
	 * {@link FutureListener#operationComplete(FutureNotifier)} is called immediately, otherwise it is called as soon as
	 * the Future finishes.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	void addListener(FutureListener<? extends FutureNotifier<? super V>> listener);

	/**
	 * Removes a listener from this Future. Fails silently if the listener has not been registered before calling this
	 * method.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	void removeListener(FutureListener<? extends FutureNotifier<? super V>> listener);

	/**
	 * Tries to return the result immediately. Returns <code>null</code> if the Future has not finished yet.
	 * 
	 * @return the result of this Future if it has finished or <code>null</code> otherwise
	 */
	V getNow();

	/**
	 * Awaits the completion of the Future.
	 * 
	 * @throws InterruptedException
	 *             if the Thread gets interrupted
	 */
	void await() throws InterruptedException;

	/**
	 * Awaits the completion of the Future for a given time.
	 * 
	 * @param timeout
	 *            the time to wait
	 * @param unit
	 *            the unit for <code>timeout</code>
	 * @return <code>true</code> if the Future completed in the given time, false otherwise
	 * @throws InterruptedException
	 *             if the Thread gets interrupted
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
}
