package edu.teco.dnd.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UniqueUUIDUtil {
	private final UUIDFactory uuidFactory;
	private final Set<UUID> uuids = new HashSet<UUID>();

	public UniqueUUIDUtil(final UUIDFactory uuidFactory) {
		this.uuidFactory = uuidFactory;
	}

	public UniqueUUIDUtil() {
		this(new RandomUUIDFactory());
	}

	public UUID getNewUUID() {
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
