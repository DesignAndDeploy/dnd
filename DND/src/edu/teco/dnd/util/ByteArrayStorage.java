package edu.teco.dnd.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Stores byte arrays based on their hash. No check for collisions is made. If an array is stored for a given hash and
 * a second array is added that has the same hash, the second one is discard. All arrays are stored as
 * {@link WeakReference}s so that they may be garbage collected. All operations are thread safe, however no guarantees
 * as to which byte array is stored are made in the case of concurrent puts. The byte arrays must not be modified while
 * they are stored.
 * 
 * @author Philipp Adolf
 */
public class ByteArrayStorage {
	private final Map<Hash, WeakReference<byte[]>> bytes = new HashMap<Hash, WeakReference<byte[]>>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final HashAlgorithm algorithm;
	
	public ByteArrayStorage(final HashAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	/**
	 * Returns a byte array with the given hash, if one was stored and it has not been garbage collected yet.
	 * Returns null otherwise.
	 * 
	 * @param hash the hash to look up
	 * @return a byte array with the given hash, if one is stored
	 */
	public byte[] get(final Hash hash) {
		lock.readLock().lock();
		try {
			final WeakReference<byte[]> reference = bytes.get(hash);
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
	 * Adds a byte array to the store. If there already is a byte array with the same hash, that array is returned.
	 * Otherwise the byte array given as a parameter is stored and returned again. {@code null} can be stored if
	 * the HashAlgorithm given to the constructor supports hashing it.
	 * 
	 * @param data the byte array to add
	 * @return a byte array with the same Hash as the one that was given
	 */
	public byte[] putIfAbsent(final byte[] data) {
		final Hash hash = algorithm.getHash(data);
		byte[] oldData = get(hash);
		if (oldData != null) {
			return oldData;
		}
		
		lock.writeLock().lock();
		try {
			oldData = get(hash);
			if (oldData != null) {
				return oldData;
			}
			
			bytes.put(hash, new WeakReference<byte[]>(data));
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
	public HashAlgorithm getAlgorithm() {
		return algorithm;
	}
}
