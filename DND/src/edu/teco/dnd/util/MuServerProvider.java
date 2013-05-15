package edu.teco.dnd.util;

import mucode.MuServer;

/**
 * Provides a singleton MuServer for each Module / the central System.
 */
public final class MuServerProvider {
	/**
	 * Utility class.
	 */
	private MuServerProvider() {
	}

	/**
	 * the singleton instance of a muServer used by this application.
	 */
	private static MuServer singleMuServerInstance = null;

	/**
	 * Single MuServer for whole Module.
	 * 
	 * @return the single MuServer.
	 */
	public static synchronized MuServer getMuServer() {
		if (singleMuServerInstance == null) {
			singleMuServerInstance = new MuServer();
			singleMuServerInstance.addUbiquitousPackage("lime.*");
			singleMuServerInstance.addUbiquitousPackage("lights.*");
			singleMuServerInstance.addUbiquitousPackage("groupmgmt.*");
			singleMuServerInstance.addUbiquitousPackage("location.*");
			singleMuServerInstance.addUbiquitousPackage("devutil.*");
			singleMuServerInstance.addUbiquitousPackage("sun.*");
			singleMuServerInstance.addUbiquitousPackage("org.apache.logging.log4j.*");
			singleMuServerInstance.addUbiquitousPackage("edu.teco.dnd.*");
			singleMuServerInstance.boot();
		}
		return singleMuServerInstance;
	}

}
