package org.protempa.ksb.protege;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import java.io.File;

/**
 * Access to Protege knowledge bases in local files or at URIs.
 * 
 * @author Andrew Post
 * 
 */
final class LocalConnectionManager extends ConnectionManager {

	/**
	 * Creates a connection manager for specified knowledge base. For accessing
	 * knowledge bases on Protege servers, see {@link RemoteConnectionManager}.
	 * 
	 * @param projectString
	 *            a Protege project {@link String}, which can be a filename
	 *            or URI.
	 */
	LocalConnectionManager(String projectString) {
		super(projectString);
	}

	/**
	 * Opens the knowledge base.
	 * 
	 * @return a Protege {@link Project}.
	 * @throws IllegalArgumentException
	 *             if an error occurs.
	 * @see edu.virginia.pbhs.protempa.protege.ConnectionManager#initProject()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Project initProject() {
		Collection errors = new ArrayList();
		String knowledgeBaseName = getKnowledgeBaseName();
        if (knowledgeBaseName == null)
            throw new IllegalStateException("No knowledge base specified");
        Util.logger().fine("Trying to load Protege project "
                + new File(knowledgeBaseName).getPath());
		Project project = new Project(knowledgeBaseName, errors);
		if (errors.size() == 0) {
            Util.logger().fine("Protege project "
                + new File(knowledgeBaseName).getPath() + " is opened.");
			return project;
		} else {
			throw new IllegalStateException("Error(s) loading knowledge base "
					+ new File(knowledgeBaseName).getPath() + ": " + errors);
		}
	}

}
