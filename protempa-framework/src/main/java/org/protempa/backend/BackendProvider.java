/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 * Interface for PROTEMPA backend provider modules.
 *
 * @author Andrew Post
 */
public interface BackendProvider {
    String getDisplayName();
    BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;
    BackendSpecLoader<KnowledgeSourceBackend> 
            getKnowledgeSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;
    BackendSpecLoader<AlgorithmSourceBackend> 
            getAlgorithmSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;

    Object newInstance(String resourceId) throws BackendNewInstanceException ;
}
