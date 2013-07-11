package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class BlockMessage extends ApplicationSpecificMessage {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block";

	public String className;
	public FunctionBlock block;

	public BlockMessage(String className, UUID appId, FunctionBlock funBlock) {
		super(appId);
		this.className = className;
		this.block = funBlock;
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
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
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
		BlockMessage other = (BlockMessage) obj;
		if (block == null) {
			if (other.block != null) {
				return false;
			}
		} else if (!block.equals(other.block)) {
			return false;
		}
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
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
		return "BlockMessage [className=" + className + ", block=" + block + ", getApplicationID()="
				+ getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
