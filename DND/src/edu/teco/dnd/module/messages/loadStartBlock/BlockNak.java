package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.network.messages.Response;

/**
 * 
 * Send, when starting of block failed.
 */
public class BlockNak extends Response {
	public static final String MESSAGE_TYPE = "block nak";
	
	private String errorMessage;
	
	public BlockNak(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public BlockNak(final Throwable cause) {
		this(cause.getMessage());
	}
	
	public BlockNak() {
		this((String) null);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Override
	public String toString() {
		return "BlockNak[" + (errorMessage == null ? "" : errorMessage) + "]";
	}
}
