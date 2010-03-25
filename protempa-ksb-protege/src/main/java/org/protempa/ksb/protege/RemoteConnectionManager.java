package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteProjectManager;

/**
 * Connection manager for knowledge bases on Protege servers.
 * 
 * @author Andrew Post
 * 
 */
final class RemoteConnectionManager extends ConnectionManager {
	private String host;

	private String username;

	private String password;

	/**
	 * Creates a connection manager for a project on a Protege server on
	 * the specified host.
	 * 
	 * @param host
	 *            the hostname {@link String} of the Protege server.
	 * @param username
	 *            a valid username {@link String} on the host.
	 * @param password
	 *            a valid password {@link String}.
	 * @param projectName
	 *            a valid project name {@link String} on the
         *            given server, which can be retrieved using
         *            {@link #getProjectIdentifier()}.
	 */
	RemoteConnectionManager(String host, String username, String password,
			String projectName) {
		super(projectName);
		this.host = host;
		this.username = username;
		this.password = password;
	}

	/**
	 * Connects to the project specified in the constructor. Throws an
         * undocumented runtime exception
	 * from within Protege if something bad happens.
	 * 
	 * @return a Protege {@link Project}.
	 */
	protected Project initProject() {
		return RemoteProjectManager.getInstance().getProject(host, username,
				password, getProjectIdentifier(), false);
	}

}
