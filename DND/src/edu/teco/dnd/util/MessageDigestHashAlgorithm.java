package edu.teco.dnd.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A wrapper for {@link MessageDigest} that provides the functionality of {@link HashAlgorithm}.
 */
public class MessageDigestHashAlgorithm implements HashAlgorithm<byte[]> {
	private final String algorithmName;

	/**
	 * Initializes a new MessageDigestHashAlgorithm.
	 * 
	 * @param algorithmName
	 *            the name of the {@link MessageDigest} algorithm to use
	 * @throws NoSuchAlgorithmException
	 *             if the algorithm does not exist
	 */
	public MessageDigestHashAlgorithm(final String algorithmName) throws NoSuchAlgorithmException {
		MessageDigest.getInstance(algorithmName);
		this.algorithmName = algorithmName;
	}

	@Override
	public Hash getHash(byte[] data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(algorithmName);
		} catch (final NoSuchAlgorithmException e) {
			// This should not happen as we test the existence of the algorithm in the constructor.
			return null;
		}
		final byte[] hash = digest.digest(data);
		return new Hash(this, hash);
	}

	public String getAlgorithm() {
		return algorithmName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithmName == null) ? 0 : algorithmName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageDigestHashAlgorithm other = (MessageDigestHashAlgorithm) obj;
		if (algorithmName == null) {
			if (other.algorithmName != null)
				return false;
		} else if (!algorithmName.equals(other.algorithmName))
			return false;
		return true;
	}
}
