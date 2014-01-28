package edu.teco.dnd.server;

import java.util.Collection;

import edu.teco.dnd.discover.ApplicationInformation;

/**
 * This listener is informed every time the list of running Applications has been refreshed.
 * 
 * @see ApplicationManager
 */
public interface ApplicationManagerListener {
	/**
	 * This method is called when the list of running Applications has been updated.
	 * 
	 * @param applications
	 *            the running Applications. Do not try to modify this collection.
	 */
	public void applicationsResolved(final Collection<ApplicationInformation> applications);
}
