package org.protempa.ksb.protege;

import edu.stanford.smi.protege.event.ProjectEvent;
import org.protempa.UnrecoverableBackendErrorEvent;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;

/**
 * Converts a Protege knowledge base on a Protege server to a PROTEMPA
 * knowledge base.
 * 
 * Properties for this backend are:
 * <ul>
 * <li>hostname: the hostname of the Protege knowledge base.</li>
 * <li>username: a username for the Protege knowledge base.</li>
 * <li>password: a password for the Protege knowledge base.</li>
 * <li>knowledgeBaseName: the name of the knowledge base.</li>
 * <li>units: ABSOLUTE or RELATIVE time units.</li>
 * </ul>
 * 
 * @author Andrew Post
 */
@BackendInfo(displayName = "Remote Protege knowledge base backend")
public final class RemoteKnowledgeSourceBackend
        extends ProtegeKnowledgeSourceBackend {

    private String hostname;
    private String username;
    private String password;
    private String knowledgeBaseName;
    private String units;

    public String getHostname() {
        return hostname;
    }

    @BackendProperty(displayName = "Hostname")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getKnowledgeBaseName() {
        return knowledgeBaseName;
    }

    @BackendProperty(displayName = "Knowledge base name")
    public void setKnowledgeBaseName(String knowledgeBaseName) {
        this.knowledgeBaseName = knowledgeBaseName;
    }

    public String getPassword() {
        return password;
    }

    @BackendProperty(displayName = "Password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUnits() {
        return units;
    }

    @BackendProperty(displayName = "Units")
    public void setUnits(String units) {
        this.units = units;
    }

    public String getUsername() {
        return username;
    }

    @BackendProperty(displayName = "Username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Instantiates the backend with no initial configuration.
     */
    public RemoteKnowledgeSourceBackend() {
    }

    @Override
    ConnectionManager initConnectionManager(BackendInstanceSpec configuration)
            throws KnowledgeSourceBackendInitializationException {
        initUnits(units);
        return new RemoteConnectionManager(hostname, username,
                password, knowledgeBaseName);
    }

    @Override
    public void serverSessionLost(ProjectEvent pe) {
        fireUnrecoverableError(new UnrecoverableBackendErrorEvent(pe));
    }
}
