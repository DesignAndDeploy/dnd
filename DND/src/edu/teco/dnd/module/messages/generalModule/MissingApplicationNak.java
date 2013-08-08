package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class MissingApplicationNak extends Response {

	public static String MESSAGE_TYPE = "missing app nak";

	public UUID appId;

	public MissingApplicationNak(UUID sourceUUID) {
		super(sourceUUID);
		this.appId = sourceUUID;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private MissingApplicationNak() {
		appId = null;
	}
}
