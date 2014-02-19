package edu.teco.dnd.util.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.util.UUIDFactory;
import edu.teco.dnd.util.UniqueUUIDFactory;

@RunWith(MockitoJUnitRunner.class)
public class UniqueUUIDFactoryTest {
	private final UUID uuid0 = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private final UUID uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private final UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
	private final UUID uuid3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
	private final UUID extraUUID = UUID.fromString("00000000-0000-0000-0000-000000000042");

	@Rule
	public TestRule globalTimeout = new Timeout(1000);

	@Mock
	private UUIDFactory internalFactory;
	private UniqueUUIDFactory uniqueUUIDFactory;

	@Before
	public void setup() {
		uniqueUUIDFactory = new UniqueUUIDFactory(internalFactory);
	}

	@Test(expected = NullPointerException.class)
	public void testFactoryNull() {
		// The constructor may already throw NPE, this is intended
		final UUIDFactory nullFactory = new UniqueUUIDFactory(null);
		nullFactory.createUUID();
	}

	@Test
	public void testUUIDsNotNull() {
		setFactoryToDefaultSequence();

		assertNotNull(uniqueUUIDFactory.createUUID());
		assertNotNull(uniqueUUIDFactory.createUUID());
		assertNotNull(uniqueUUIDFactory.createUUID());
		assertNotNull(uniqueUUIDFactory.createUUID());
	}

	@Test
	public void testUUIDsDifferent() {
		setFactoryToDefaultSequence();
		final Set<UUID> uuids = new HashSet<UUID>();

		for (int i = 0; i < 4; i++) {
			final UUID newUUID = uniqueUUIDFactory.createUUID();
			if (uuids.contains(newUUID)) {
				fail(newUUID + " was already generated");
			}
		}
	}

	@Test
	public void testHasGeneratedUUIDFresh() {
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(uuid0));
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(uuid1));
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(uuid2));
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(uuid3));
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(extraUUID));
	}

	@Test
	public void testHasGeneratedUUID() {
		setFactoryToDefaultSequence();

		final UUID firstGeneratedUUID = uniqueUUIDFactory.createUUID();
		final UUID secondGeneratedUUID = uniqueUUIDFactory.createUUID();

		assertTrue(uniqueUUIDFactory.hasGeneratedUUID(firstGeneratedUUID));
		assertTrue(uniqueUUIDFactory.hasGeneratedUUID(secondGeneratedUUID));
		assertFalse(uniqueUUIDFactory.hasGeneratedUUID(extraUUID));
	}

	private void setFactoryToDefaultSequence() {
		when(internalFactory.createUUID()).thenReturn(uuid0, uuid1, uuid2, uuid3);
	}
}
