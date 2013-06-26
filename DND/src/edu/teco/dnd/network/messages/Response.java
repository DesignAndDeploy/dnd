package edu.teco.dnd.network.messages;

import java.util.UUID;

public class Response extends Message {
	private final UUID sourceUUID;
	
	public Response(final UUID sourceUUID, final UUID uuid) {
		super(uuid);
		this.sourceUUID = sourceUUID;
	}
	
	public Response(final UUID sourceUUID) {
		super();
		this.sourceUUID = sourceUUID;
	}
	
	public UUID getSourceUUID() {
		return this.sourceUUID;
	}
}
