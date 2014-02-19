package edu.teco.dnd.util;

import java.util.UUID;

/**
 * A {@link UUIDFactory} that returns random {@link UUID}s.
 */
public class RandomUUIDFactory implements UUIDFactory {
	@Override
	public UUID createUUID() {
		return UUID.randomUUID();
	}
}
