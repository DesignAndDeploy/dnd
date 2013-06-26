package edu.teco.dnd.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface FutureNotifier<V> extends Future<V> {
	boolean isSuccess();
	
	Throwable cause();
	
	void addListener(FutureListener<? extends FutureNotifier<? super V>> listener);
	
	void removeListener(FutureListener<? extends FutureNotifier<? super V>> listener);
	
	V getNow();
	
	void await() throws InterruptedException;
	
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
}
