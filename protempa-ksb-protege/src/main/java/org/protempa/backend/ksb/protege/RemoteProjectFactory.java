/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import java.util.List;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.Configuration;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.ConfigurationsProviderManager;
import org.protempa.backend.InvalidPropertyNameException;

/**
 * Opens a remote Protege project that is specified in a PROTEMPA configuration.
 * A remote Protege project is uniquely specified by its hostname (and port).
 *
 * @param configurationId the id {@link String} of the configuration.
 * @param host the hostname {@link String}.
 * @param knowledgeBaseName the name {@link String} of the knowledge base.
 * @return a Protege {@link Project}.
 *
 * @author Andrew Post
 */
public class RemoteProjectFactory {

    public Project getInstance(String configurationId,
            String host, String knowledgeBaseName)
            throws ConfigurationsLoadException,
            BackendProviderSpecLoaderException, InvalidPropertyNameException,
            ConfigurationsNotFoundException {
        Configurations configurations
                = ConfigurationsProviderManager.getConfigurations();
        String hostname = null;
        String username = null;
        String password = null;
        Configuration load = configurations.load(configurationId);
        List<BackendInstanceSpec<KnowledgeSourceBackend>> knowledgeSourceBackendSections = load.getKnowledgeSourceBackendSections();
        KNOWLEDGE_SOURCE_BACKEND_SPEC_LOOP:
        for (BackendInstanceSpec<KnowledgeSourceBackend> bis : knowledgeSourceBackendSections) {
            if (bis.getBackendSpec().getId().equals(
                    RemoteKnowledgeSourceBackend.class.getName()) && 
                    knowledgeBaseName.equals(bis.getProperty("knowledgeBaseName"))) {
                String hname = (String) bis.getProperty("hostname");
                if (host.equals(hname)) {
                    hostname = hname;
                    username = (String) bis.getProperty("username");
                    password = (String) bis.getProperty("password");
                    break KNOWLEDGE_SOURCE_BACKEND_SPEC_LOOP;
                }
            }
        }
        Project project = RemoteProjectManager.getInstance().getProject(
                hostname, username, password, knowledgeBaseName, false);
        return project;
    }
}
