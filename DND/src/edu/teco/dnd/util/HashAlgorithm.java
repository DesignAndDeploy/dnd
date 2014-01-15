package edu.teco.dnd.util;

/**
 * A hash algorithm is used to calculate the hash of some data.
 * 
 * @author Philipp Adolf
 */
public interface HashAlgorithm {
	/**
	 * Returns the hash for the given data. If equal byte arrays are passed to the method, equal hashes have to be
	 * returned. For non equal byte arrays, non equal hashes should be returned, however hash collisions are allowed.
	 * May throw a {@link NullPointerException} or {@link IllegalArgumentException} if {@code null} is passed.
	 * 
	 * @param data the data that should be hashed. Implementations may require this parameter to be not null.
	 * @return the hash of the data
	 */
	Hash getHash(byte[] data);
}
