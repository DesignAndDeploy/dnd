package edu.teco.dnd.util;

/**
 * A hash algorithm is used to calculate the hash of some data.
 */
public interface HashAlgorithm<T> {
	/**
	 * Returns the hash for the given data. If equal data is passed to the method, equal hashes have to be returned. For
	 * non equal data, non equal hashes should be returned, however hash collisions are allowed. May throw a
	 * {@link NullPointerException} or {@link IllegalArgumentException} if <code>null</code> is passed.
	 * 
	 * @param data
	 *            the data that should be hashed. Implementations may require this parameter to be not <code>null</code>
	 *            .
	 * @return the hash of the data
	 */
	Hash getHash(T data);
}
