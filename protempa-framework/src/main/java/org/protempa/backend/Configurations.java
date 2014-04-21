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

import java.util.List;

/**
 *
 * @author Andrew Post
 */
public interface Configurations {
    /**
     * Reads all of the backend specifications from the specified 
     * configurations conforming to the given backend spec.
     * @param <B>
     * @param configurationsId
     * @param backendSpec
     * @return
     * @throws ConfigurationsNotFoundException
     * @throws ConfigurationsLoadException 
     */
    <B extends Backend> List<BackendInstanceSpec<B>>
            load(String configurationsId, BackendSpec<B> backendSpec)
            throws ConfigurationsNotFoundException, ConfigurationsLoadException;

    /**
     * Writes the provided backend specifications to the configurations with
     * the specified id. The passed-in objects will have their 
     * <code>configurationsId</code> field set to the provided value.
     * 
     * @param configurationsId
     * @param backendInstanceSpec
     * @throws ConfigurationsSaveException 
     */
    void save(String configurationsId, 
            List<BackendInstanceSpec> backendInstanceSpec)
            throws ConfigurationsSaveException;
    /**
     * Deletes the configurations with the specified id.
     * 
     * @param configurationsId
     * @throws ConfigurationRemoveException 
     */
    void remove(String configurationsId)
            throws ConfigurationRemoveException;
    List<String> loadConfigurationIds(String configurationsId)
            throws ConfigurationsNotFoundException, 
            ConfigurationsLoadException;
}
