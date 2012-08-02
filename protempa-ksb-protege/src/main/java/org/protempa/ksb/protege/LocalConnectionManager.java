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
package org.protempa.ksb.protege;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;

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
     * @param filePathOrURI
     *            a file path or URI to the Protege project. This is used as
     *            the knowledge base name. Cannot be <code>null</code>.
     * @see #getProjectIdentifier()
     */
    LocalConnectionManager(String filePathOrURI) {
        super(filePathOrURI);
    }

    /**
     * Opens the project specified by the file path or URI given in
     * the constructor.
     *
     * @return a Protege {@link Project}.
     * @see ConnectionManager#initProject()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Project initProject() {
        Collection errors = new ArrayList();
        String projectFilePathOrURI = getProjectIdentifier();
        Util.logger().fine("Trying to load Protege project "
                + projectFilePathOrURI);
        Project project = new Project(projectFilePathOrURI, errors);
        if (errors.size() == 0) {
            Util.logger().fine("Protege project "
                    + projectFilePathOrURI + " is opened.");
            return project;
        } else {
            throw new IllegalStateException(
                    "Error(s) loading knowledge base "
                    + projectFilePathOrURI + ": " + errors);
        }
    }
}
