package edu.teco.dnd.util.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.util.ByteArrayStorage;
import edu.teco.dnd.util.Hash;
import edu.teco.dnd.util.HashAlgorithm;

@RunWith(MockitoJUnitRunner.class)
public class ByteArrayStorageTest {
	private static final byte[] ARRAY1 = new byte[] { 0x00 };
	private static final byte[] ARRAY2 = new byte[] { 0x00, 0x00 };
	private static final byte[] ARRAY3 = new byte[] { 0x00, 0x01, 0x02, 0x03 };
	
	@Mock
	private HashAlgorithm algorithm;

	private Hash hash1;
	private Hash hash2;
	private Hash hash3;
	
	private ByteArrayStorage bsa;
	
	@Before
	public void setup() {
		hash1 = new Hash(algorithm, ARRAY1);
		hash2 = new Hash(algorithm, ARRAY2);
		hash3 = new Hash(algorithm, ARRAY3);
		when(algorithm.getHash(ARRAY1)).thenReturn(hash1);
		when(algorithm.getHash(ARRAY2)).thenReturn(hash2);
		when(algorithm.getHash(ARRAY3)).thenReturn(hash3);
		
		bsa = new ByteArrayStorage(algorithm);
	}
	
	@Test
	public void testEmpty() {
		assertNull(bsa.get(hash1));
		assertNull(bsa.get(hash2));
		assertNull(bsa.get(hash3));
	}
	
	@Test
	public void testPutIfAbsent() {
		assertArrayEquals(ARRAY1, bsa.putIfAbsent(ARRAY1));
	}
	
	@Test
	public void testGet() {
		bsa.putIfAbsent(ARRAY1);
		
		assertArrayEquals(ARRAY1, bsa.get(hash1));
	}
	
	@Test
	public void testGetOther() {
		bsa.putIfAbsent(ARRAY1);
		
		assertNull(bsa.get(hash2));
	}
	
	@Test
	public void testGetMultiple() {
		bsa.putIfAbsent(ARRAY1);
		bsa.putIfAbsent(ARRAY2);

		assertArrayEquals(ARRAY1, bsa.get(hash1));
		assertArrayEquals(ARRAY2, bsa.get(hash2));
	}
	
	@Test
	public void testCollision() {
		when(algorithm.getHash(ARRAY2)).thenReturn(hash1);
		
		bsa.putIfAbsent(ARRAY1);
		bsa.putIfAbsent(ARRAY2);
		
		assertArrayEquals(ARRAY1, bsa.get(hash1));
	}
}
