package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.network.messages.Message;

public class RequestModuleInfoMessage extends Message {
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "request module info";

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestModuleInfoMessage [getUUID()=" + getUUID() + "]";
	}
	
	
}
