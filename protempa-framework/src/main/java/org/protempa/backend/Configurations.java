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
import org.protempa.backend.tsb.TermSourceBackend;

/**
 *
 * @author Andrew Post
 */
public interface Configurations {
    /**
     * Reads all of the backend specifications from the specified 
     * configurations conforming to the given backend spec.
     * @param <B>
     * @param configurationId
     * @param backendSpec
     * @return
     * @throws ConfigurationsNotFoundException
     * @throws ConfigurationsLoadException 
     */
    Configuration load(String configurationId)
        throws ConfigurationsNotFoundException, ConfigurationsLoadException;
    
    BackendInstanceSpec<AlgorithmSourceBackend> newAlgorithmSourceBackendSection(String backendSpecId) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException;
    
    BackendInstanceSpec<DataSourceBackend> newDataSourceBackendSection(String backendSpecId) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException;
    
    BackendInstanceSpec<KnowledgeSourceBackend> newKnowledgeSourceBackendSection(String backendSpecId) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException;
    
    BackendInstanceSpec<TermSourceBackend> newTermSourceBackendSection(String backendSpecId) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException;
        
    /**
     * Writes the provided backend specifications to the configurations with
     * the specified id. The passed-in objects will have their 
     * <code>configurationsId</code> field set to the provided value.
     * 
     * @param configurationId
     * @param backendInstanceSpec
     * @throws ConfigurationsSaveException 
     */
    void save(Configuration configuration)
            throws ConfigurationsSaveException;
    /**
     * Deletes the configurations with the specified id.
     * 
     * @param configurationId
     * @throws ConfigurationRemoveException 
     */
    void remove(String configurationId)
            throws ConfigurationRemoveException;
    
}
