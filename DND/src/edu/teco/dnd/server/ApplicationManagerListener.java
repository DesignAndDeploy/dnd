package edu.teco.dnd.server;

import java.util.Collection;

import edu.teco.dnd.discover.ApplicationInformation;

/**
 * Provides Information on running applications.
 * @author jung
 *
 */
public interface ApplicationManagerListener {

	/**
	 * Informs that an application is running.
	 */
	public void applicationsResolved(final Collection<ApplicationInformation> apps);
	
	public void serverOnline();
	
	public void serverOffline();
	
}
