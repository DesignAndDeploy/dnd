package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class ValueNak extends Response {
	public enum ErrorType {
		WRONG_MODULE, // TODO need a way to handle if not even the app is running on the module.
		INVALID_INPUT, OTHER;
	}

	// TODO when sending appValueMsg listen for this and similar response (check [...].value for similar things
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "value nak";
	public final UUID blockId;
	public final String input;

	public final ErrorType errorType;

	public ValueNak(UUID appId, ErrorType errorType, UUID blockId, String input) {
		if (errorType == null) {
			errorType = ErrorType.OTHER;
		}
		this.errorType = errorType;
		this.blockId = blockId;
		this.input = input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blockId == null) ? 0 : blockId.hashCode());
		result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ValueNak other = (ValueNak) obj;
		if (blockId == null) {
			if (other.blockId != null) {
				return false;
			}
		} else if (!blockId.equals(other.blockId)) {
			return false;
		}
		if (errorType != other.errorType) {
			return false;
		}
		if (input == null) {
			if (other.input != null) {
				return false;
			}
		} else if (!input.equals(other.input)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValueNak [blockId=" + blockId + ", input=" + input + ", errorType=" + errorType + ", getSourceUUID()="
				+ getSourceUUID() + ", getUUID()=" + getUUID() + "]";
	}

}
