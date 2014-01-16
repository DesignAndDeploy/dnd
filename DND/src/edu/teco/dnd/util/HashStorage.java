package edu.teco.dnd.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Stores objects based on their hash. No check for collisions is made. If an object is stored for a given hash and
 * a second object is added that has the same hash, the second one is discard. All objects are stored as
 * {@link WeakReference}s so that they may be garbage collected. All operations are thread safe, however no guarantees
 * as to which object is stored are made in the case of concurrent puts. The objects must not be modified in a way that
 * would change their Hash while they are stored.
 * 
 * @author Philipp Adolf
 */
public class HashStorage<T> {
	private final Map<Hash, WeakReference<T>> storage = new HashMap<Hash, WeakReference<T>>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final HashAlgorithm<? super T> algorithm;
	
	public HashStorage(final HashAlgorithm<? super T> algorithm) {
		this.algorithm = algorithm;
	}
	
	/**
	 * Returns an object with the given hash, if one was stored and it has not been garbage collected yet.
	 * Returns null otherwise.
	 * 
	 * @param hash the hash to look up
	 * @return an object with the given hash, if one is stored
	 */
	public T get(final Hash hash) {
		lock.readLock().lock();
		try {
			final WeakReference<T> reference = storage.get(hash);
			if (reference != null) {
				return reference.get();
			} else {
				return null;
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Adds an object to the store. If there already is an object with the same hash, that object is returned.
	 * Otherwise the object given as a parameter is stored and returned again. {@code null} can be stored if
	 * the HashAlgorithm given to the constructor supports hashing it.
	 * 
	 * @param data the object to add
	 * @return an object with the same Hash as the one that was given
	 */
	public T putIfAbsent(final T data) {
		final Hash hash = algorithm.getHash(data);
		T oldData = get(hash);
		if (oldData != null) {
			return oldData;
		}
		
		lock.writeLock().lock();
		try {
			oldData = get(hash);
			if (oldData != null) {
				return oldData;
			}
			
			storage.put(hash, new WeakReference<T>(data));
			return data;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Returns the algorithm used by this ByteArrayStorage.
	 * 
	 * @return the algorithm used by this ByteArrayStorage.
	 */
	public HashAlgorithm<? super T> getAlgorithm() {
		return algorithm;
	}
}
