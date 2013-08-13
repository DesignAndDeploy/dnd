package edu.teco.dnd.module.messages.values;

import java.io.Serializable;
import java.util.UUID;

import edu.teco.dnd.module.UsercodeWrapper;
import edu.teco.dnd.module.UserSuppliedCodeException;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class ValueMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "value";
	public final UUID blockId;
	public final String input;
	public final Serializable value;

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
		} else
			try {
				if (!UsercodeWrapper.getEquals(value, other.value)) {
					return false;
				}
			} catch (UserSuppliedCodeException e) {
				e.printStackTrace();
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
