package edu.teco.dnd.network.messages;

import java.util.UUID;

/**
 * A Message that is specific to an application running on the module.
 *
 * @author Philipp Adolf
 */
public interface ApplicationSpecificMessage extends Message {
	/**
	 * Returns the ID of the application this message should be delivered to
	 * 
	 * @return the ID of the application this message should be delivered to
	 */
	UUID getApplicationID();
}
