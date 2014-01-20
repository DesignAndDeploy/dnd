package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * Response to let the block sending a value message know, that the retrieving of the value failed.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueNak extends Response {
	/**
	 * The reason the Value could not be delivered.
	 * 
	 * @author Marvin Marx
	 * 
	 */
	public enum ErrorType {
		/**
		 * Value could not be delivered, because this ModuleInfo does not know/execute the given FunctionBlock.
		 */
		WRONG_MODULE,
		/**
		 * The value could not be delivered because, although the module does execute the given Block, said Block does
		 * not have an input of the given name.
		 */
		INVALID_INPUT,
		/**
		 * Some other error occurred, that is not covered by the above.
		 */
		OTHER;
	}

	public static final String MESSAGE_TYPE = "value nak";
	/**
	 * UUID of the Block the Value was meant for.
	 */
	public final UUID blockId;
	/**
	 * Name of the Input the Value was meant for.
	 */
	public final String input;

	/**
	 * Type of error that occurred. See ErrorType.
	 */
	public final ErrorType errorType;

	/**
	 * 
	 * @param appId
	 *            ID of application sending/receiving this value.
	 * @param errorType
	 *            type of error that occurred.
	 * @param blockId
	 *            Id of block this value was meant for.
	 * @param input
	 *            name of input this value was meant for.
	 */
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
