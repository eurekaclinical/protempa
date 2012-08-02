/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.ksb.protege;

import edu.stanford.smi.protege.event.ProjectEvent;
import org.protempa.backend.UnrecoverableBackendErrorEvent;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

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
