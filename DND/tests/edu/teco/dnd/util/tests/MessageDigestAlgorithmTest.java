package edu.teco.dnd.util.tests;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import edu.teco.dnd.util.Hash;
import edu.teco.dnd.util.MessageDigestHashAlgorithm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class MessageDigestAlgorithmTest {
	private static final byte[] EMPTY_ARRAY = new byte[0];
	
	private static final String[] algorithms = new String[] { "MD5", "SHA-1", "SHA-256", "SHA-512" };
	
	@Parameters(name = "{index}: algorithm={0}")
	public static Collection<Object[]> parameters() {
		final Collection<Object[]> parameters = new ArrayList<Object[]>(algorithms.length);
		for (final String algorithm : algorithms) {
			parameters.add(new Object[] { algorithm });
		}
		return parameters;
	}
	
	@Parameter
	public String algorithm;
	
	private MessageDigest digest;
	private MessageDigestHashAlgorithm mdha;
	
	@Before
	public void setup() throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance(algorithm);
		mdha = new MessageDigestHashAlgorithm(algorithm);
	}
	
	@Test
	public void testEquals() throws NoSuchAlgorithmException {
		assertFalse(mdha.equals(null));
		assertFalse(mdha.equals(new Object()));
		assertTrue(mdha.equals(mdha));
		assertTrue(mdha.equals(new MessageDigestHashAlgorithm(algorithm)));
		for (final String otherAlgorithm : algorithms) {
			if (algorithm.equals(otherAlgorithm)) {
				continue;
			}
			assertFalse(mdha.equals(new MessageDigestHashAlgorithm(otherAlgorithm)));
		}
	}
	
	@Test
	public void testHashCode() throws NoSuchAlgorithmException {
		assertEquals(mdha.hashCode(), mdha.hashCode());
		assertEquals(mdha.hashCode(), new MessageDigestHashAlgorithm(algorithm).hashCode());
	}
	
	@Test
	public void testGetHashAlgorithm() {
		final Hash hash = mdha.getHash(EMPTY_ARRAY);
		
		assertEquals(mdha, hash.getAlgorithm());
	}
	
	@Test
	public void testHashEmpty() {
		final byte[] expected = digest.digest(EMPTY_ARRAY);
		
		final Hash hash = mdha.getHash(EMPTY_ARRAY);
		
		assertArrayEquals(expected, hash.getHash());
	}
	
	@Test
	public void testHash() {
		final byte[] expected = digest.digest(new byte[] { 0 });
		
		final Hash hash = mdha.getHash(new byte[] { 0 });
		
		assertArrayEquals(expected, hash.getHash());
	}
}
