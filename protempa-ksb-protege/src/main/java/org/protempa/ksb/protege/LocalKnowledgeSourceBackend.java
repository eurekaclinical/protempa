package org.protempa.ksb.protege;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;

/**
 * Converts a local Protege knowledge base to a PROTEMPA knowledge base.
 * 
 * Properties for this backend are:
 * <ul>
 * <li>projectString: the knowledge base's Protege string (e.g., its
 * filename).
 * <li>projectResource: an alternative to projectString, it allows the
 * knowledge base to be specified as a Java resource. If both it and
 * projectString are specified, it is unspecified which one will be used.
 * <li>units: ABSOLUTE or RELATIVE time units.
 * </ul>
 * 
 * @author Andrew Post
 */
@BackendInfo(
    displayName="Local Protege knowledge base backend"
)
public final class LocalKnowledgeSourceBackend extends ProtegeKnowledgeSourceBackend {
    
    private String projectString;
    
    private String projectResource;

    private String units;

    public String getProjectResource() {
        return projectResource;
    }

    @BackendProperty(
        displayName="Project resource"
    )
    public void setProjectResource(String projectResource) {
        this.projectResource = projectResource;
    }

    public String getProjectString() {
        return projectString;
    }

    @BackendProperty(
        displayName="Project string"
    )
    public void setProjectString(String projectString) {
        this.projectString = projectString;
    }

    public String getUnits() {
        return units;
    }

    @BackendProperty(
        displayName="Units"
    )
    public void setUnits(String units) {
        this.units = units;
    }

	/**
	 * Instantiates the backend with no initial configuration.
	 */
	public LocalKnowledgeSourceBackend() {
		super();
	}

	@Override
	ConnectionManager initConnectionManager(
            BackendInstanceSpec configuration) 
            throws KnowledgeSourceBackendInitializationException{
        if (projectString == null) {
            if (projectResource == null)
                throw new IllegalStateException(
                    "No Protege project filename or resource name specified");
            URL projectURL = getClass().getResource(projectResource);
            try {
                URI projectURI = projectURL.toURI();
                projectString = projectURI.toASCIIString();
            } catch (URISyntaxException e) {
                throw new AssertionError(e);
            }

        }
        initUnits(units);
		return new LocalConnectionManager(projectString);
	}

}
