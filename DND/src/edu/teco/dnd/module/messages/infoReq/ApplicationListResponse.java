package edu.teco.dnd.module.messages.infoReq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * Contains the UUIDs and names of all running Applications.
 * 
 * @author Philipp Adolf
 */
public class ApplicationListResponse extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "application list";

	/**
	 * The UUIDs of all running applications mapped to their names.
	 */
	private final Map<UUID, String> applications;

	/**
	 * The UUID of the module this Response was generated on.
	 */
	private final UUID moduleUUID;

	/**
	 * Creates a new ApplicationListResponse.
	 * 
	 * @param applications
	 *            the UUIDs of the running Applications mapped to their names
	 */
	public ApplicationListResponse(final UUID moduleUUID, final Map<UUID, String> applications) {
		this.moduleUUID = moduleUUID;
		this.applications = Collections.unmodifiableMap(new HashMap<UUID, String>(applications));
	}

	/**
	 * Constructor used by gson.
	 */
	@SuppressWarnings("unused")
	private ApplicationListResponse() {
		this.moduleUUID = null;
		this.applications = null;
	}

	/**
	 * Returns the UUID of the module this Response was generated on.
	 * 
	 * @return the UUID of the module this Response was generated on
	 */
	public UUID getModuleUUID() {
		return this.moduleUUID;
	}

	/**
	 * Returns the UUIDs of all running Applications mapped to their name
	 * 
	 * @return the UUIDs of all running Applications mapped to their name
	 */
	public Map<UUID, String> getApplications() {
		return this.applications;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((applications == null) ? 0 : applications.hashCode());
		result = prime * result + ((moduleUUID == null) ? 0 : moduleUUID.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		ApplicationListResponse other = (ApplicationListResponse) obj;
		if (applications == null) {
			if (other.applications != null) {
				return false;
			}
		} else if (!applications.equals(other.applications)) {
			return false;
		}
		if (moduleUUID == null) {
			if (other.moduleUUID != null) {
				return false;
			}
		} else if (!moduleUUID.equals(other.moduleUUID)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApplicationListResponse [applications=" + applications + ", moduleUUID=" + moduleUUID
				+ ", getSourceUUID()=" + getSourceUUID() + "]";
	}
	
	
}
