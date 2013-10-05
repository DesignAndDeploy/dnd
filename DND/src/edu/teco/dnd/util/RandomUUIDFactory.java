package edu.teco.dnd.util;

import java.util.UUID;

public class RandomUUIDFactory implements UUIDFactory {
	@Override
	public UUID createUUID() {
		return UUID.randomUUID();
	}
}
