package edu.teco.dnd.util.tests;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.util.HashStorage;
import edu.teco.dnd.util.Hash;
import edu.teco.dnd.util.HashAlgorithm;

@RunWith(MockitoJUnitRunner.class)
public class HashStorageTest {
	private static final Object OBJECT1 = new Object();
	private static final Object OBJECT2 = new Object();
	private static final Object OBJECT3 = new Object();
	
	@Mock
	private HashAlgorithm<Object> algorithm;

	private Hash hash1;
	private Hash hash2;
	private Hash hash3;
	
	private HashStorage<Object> bsa;
	
	@Before
	public void setup() {
		hash1 = new Hash(algorithm, new byte[] { 0 });
		hash2 = new Hash(algorithm, new byte[] { 1 });
		hash3 = new Hash(algorithm, new byte[] { 2 });
		when(algorithm.getHash(OBJECT1)).thenReturn(hash1);
		when(algorithm.getHash(OBJECT2)).thenReturn(hash2);
		when(algorithm.getHash(OBJECT3)).thenReturn(hash3);
		
		bsa = new HashStorage<Object>(algorithm);
	}
	
	@Test
	public void testEmpty() {
		assertNull(bsa.get(hash1));
		assertNull(bsa.get(hash2));
		assertNull(bsa.get(hash3));
	}
	
	@Test
	public void testPutIfAbsent() {
		assertSame(OBJECT1, bsa.putIfAbsent(OBJECT1));
	}
	
	@Test
	public void testGet() {
		bsa.putIfAbsent(OBJECT1);
		
		assertSame(OBJECT1, bsa.get(hash1));
	}
	
	@Test
	public void testGetOther() {
		bsa.putIfAbsent(OBJECT1);
		
		assertNull(bsa.get(hash2));
	}
	
	@Test
	public void testGetMultiple() {
		bsa.putIfAbsent(OBJECT1);
		bsa.putIfAbsent(OBJECT2);

		assertSame(OBJECT1, bsa.get(hash1));
		assertSame(OBJECT2, bsa.get(hash2));
	}
	
	@Test
	public void testCollision() {
		when(algorithm.getHash(OBJECT2)).thenReturn(hash1);
		
		bsa.putIfAbsent(OBJECT1);
		bsa.putIfAbsent(OBJECT2);
		
		assertSame(OBJECT1, bsa.get(hash1));
	}
}
