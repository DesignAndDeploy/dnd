package edu.teco.dnd.util;

import java.util.EventListener;

/**
 * A listener for {@link FutureNotifier}.
 * 
 * @author Philipp Adolf
 */
public interface FutureListener<F extends FutureNotifier<?>> extends EventListener {
	/**
	 * This method is called by a {@link FutureNotifier} when the Future finishes.
	 * 
	 * @param future
	 *            the Future that finished
	 */
	void operationComplete(F future) throws Exception;
}
