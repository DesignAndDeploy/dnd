package edu.teco.dnd.util;

import java.util.EventListener;

public interface FutureListener<F extends FutureNotifier<?>> extends EventListener {
	void operationComplete(F future) throws Exception;
}
