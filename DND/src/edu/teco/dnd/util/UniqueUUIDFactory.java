package edu.teco.dnd.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 * An implementation of {@link UUIDFactory} that guarantees that no UUID is created twice. This relies on a "good"
 * implementation for the internal factory, that is the factory has to create the enough different UUIDs or
 * {@link UniqueUUIDFactory#createUUID()} will loop endlessly.
 * </p>
 * 
 * <p>
 * This class is a potential memory hog as it will keep all generated UUIDs
 * </p>
 */
public class UniqueUUIDFactory implements UUIDFactory {
	private final UUIDFactory uuidFactory;
	private final Set<UUID> uuids = new HashSet<UUID>();

	/**
	 * Initializes a new UniqueUUIDFactory that will use the given {@link UUIDFactory}. Note that the factory should
	 * return a "sufficient" amount of different {@link UUID}s or {@link #createUUID()} may loop endlessly.
	 * 
	 * @param uuidFactory
	 *            the UUIDFactory to use internally
	 */
	public UniqueUUIDFactory(final UUIDFactory uuidFactory) {
		this.uuidFactory = uuidFactory;
	}

	/**
	 * Initializes a new UniqueUUIDFactory that will use a default {@link UUIDFactory} internally.
	 */
	public UniqueUUIDFactory() {
		this(new RandomUUIDFactory());
	}

	@Override
	public UUID createUUID() {
		UUID newUUID;
		do {
			newUUID = uuidFactory.createUUID();
		} while (hasGeneratedUUID(newUUID));
		addUUID(newUUID);
		return newUUID;
	}

	public boolean hasGeneratedUUID(final UUID uuid) {
		return uuids.contains(uuid);
	}

	private void addUUID(final UUID uuid) {
		uuids.add(uuid);
	}
}
