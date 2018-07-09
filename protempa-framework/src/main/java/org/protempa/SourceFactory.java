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
package org.protempa;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.ConfigurationsProviderManager;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.backend.Configuration;

/**
 * @author Andrew Post
 */
public class SourceFactory {

    private final List<BackendInstanceSpec<AlgorithmSourceBackend>> algorithmSourceBackendInstanceSpecs;
    private final List<BackendInstanceSpec<DataSourceBackend>> dataSourceBackendInstanceSpecs;
    private final List<BackendInstanceSpec<KnowledgeSourceBackend>> knowledgeSourceBackendInstanceSpecs;
    
    public SourceFactory(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }
        this.algorithmSourceBackendInstanceSpecs = configuration.getAlgorithmSourceBackendSections();
        this.dataSourceBackendInstanceSpecs = configuration.getDataSourceBackendSections();
        this.knowledgeSourceBackendInstanceSpecs = configuration.getKnowledgeSourceBackendSections();
    }

    public SourceFactory(Configurations configurations, String configurationId)
            throws BackendProviderSpecLoaderException,
            ConfigurationsLoadException,
            InvalidConfigurationException,
            ConfigurationsNotFoundException {
        Logger logger = ProtempaUtil.logger();
        logger.fine("Loading backend provider");
        BackendProvider backendProvider =
                BackendProviderManager.getBackendProvider();
        logger.log(Level.FINE, "Got backend provider {0}",
                backendProvider.getClass().getName());
        if (configurations == null) {
            logger.fine("Loading configurations");
            configurations =
                    ConfigurationsProviderManager.getConfigurations();
            logger.fine("Got available configurations");
        }
        
        logger.log(Level.FINE, "Loading configuration {0}", configurationId);
        Configuration configuration = configurations.load(configurationId);
        this.algorithmSourceBackendInstanceSpecs = configuration.getAlgorithmSourceBackendSections();
        this.dataSourceBackendInstanceSpecs = configuration.getDataSourceBackendSections();
        this.knowledgeSourceBackendInstanceSpecs = configuration.getKnowledgeSourceBackendSections();
        logger.log(Level.FINE, "Configuration {0} loaded", configurationId);
    }

    public SourceFactory(String configurationId)
            throws ConfigurationsLoadException,
            BackendProviderSpecLoaderException, InvalidConfigurationException,
            ConfigurationsNotFoundException {
        this(null, configurationId);
    }
    
    public final DataSource newDataSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        DataSourceBackend[] backends = new DataSourceBackend[this.dataSourceBackendInstanceSpecs.size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.dataSourceBackendInstanceSpecs.get(i).getInstance();
        }
        return new DataSourceImpl(backends);
    }

    public final KnowledgeSource newKnowledgeSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        KnowledgeSourceBackend[] backends = new KnowledgeSourceBackend[this.knowledgeSourceBackendInstanceSpecs.size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.knowledgeSourceBackendInstanceSpecs.get(i).getInstance();
        }
        return new KnowledgeSourceImpl(backends);
    }

    public final AlgorithmSource newAlgorithmSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        AlgorithmSourceBackend[] backends = new AlgorithmSourceBackend[this.algorithmSourceBackendInstanceSpecs.size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.algorithmSourceBackendInstanceSpecs.get(i).getInstance();
        }
        return new AlgorithmSourceImpl(backends);
    }

}
