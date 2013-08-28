package edu.teco.dnd.module.messages.values;

import java.io.Serializable;
import java.util.UUID;

import edu.teco.dnd.module.UserSuppliedCodeException;
import edu.teco.dnd.module.UsercodeWrapper;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * Message containing a value send by the output of one functionBlock to the input of another.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "value";
	/**
	 * ID of the block this is to be send to.
	 */
	public final UUID blockId;
	/**
	 * Name of the input this is to be send to.
	 */
	public final String input;
	/**
	 * The actual value.
	 */
	public final Serializable value;

	/**
	 * 
	 * @param appId
	 *            ID of the application this is part of
	 * @param functionBlock
	 *            ID of the block this is to be send to.
	 * @param input
	 *            Name of the input this is to be send to.
	 * @param value
	 *            The actual value.
	 */
	public ValueMessage(UUID appId, UUID functionBlock, String input, Serializable value) {
		super(appId);
		this.blockId = functionBlock;
		this.input = input;
		this.value = value;
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
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		try {
			result = prime * result + ((value == null) ? 0 : UsercodeWrapper.getHashCode(value));
		} catch (UserSuppliedCodeException e) {
			e.printStackTrace();
			result = prime * result + 0;
		}
		return result;
	}

	/**
	 * ATTENTION: may falsely return false, because the Serializable value does not guarantee comparability!
	 * 
	 * @param obj
	 *            the usual.
	 * @return the usual.
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
		ValueMessage other = (ValueMessage) obj;
		if (blockId == null) {
			if (other.blockId != null) {
				return false;
			}
		} else if (!blockId.equals(other.blockId)) {
			return false;
		}
		if (input == null) {
			if (other.input != null) {
				return false;
			}
		} else if (!input.equals(other.input)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else {
			try {
				if (!UsercodeWrapper.getEquals(value, other.value)) {
					return false;
				}
			} catch (UserSuppliedCodeException e) {
				e.printStackTrace();
				return false;
			}
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
		try {
			return "ValueMessage [blockId=" + blockId + ", input=" + input + ", value="
					+ UsercodeWrapper.getToString(value) + ", getApplicationID()=" + getApplicationID()
					+ ", getUUID()=" + getUUID() + "]";
		} catch (UserSuppliedCodeException e) {
			e.printStackTrace();
			return "ValueMessage [blockId=" + blockId + ", input=" + input + ", value=" + "ERROR"
					+ ", getApplicationID()=" + getApplicationID() + ", getUUID()=" + getUUID() + "]";
		}
	}

}
