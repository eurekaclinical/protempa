package org.protempa.backend;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class ConfigurationsSupport {

    private final BackendProvider backendProvider;
    private final List<BackendInstanceSpec<AlgorithmSourceBackend>> asbInstanceSpecs;
    private final List<BackendInstanceSpec<DataSourceBackend>> dsbInstanceSpecs;
    private final List<BackendInstanceSpec<KnowledgeSourceBackend>> ksbInstanceSpecs;
    private BackendSpecLoader<AlgorithmSourceBackend> asl;
    private BackendSpecLoader<DataSourceBackend> dsl;
    private BackendSpecLoader<KnowledgeSourceBackend> ksl;
    private String configurationId;

    public ConfigurationsSupport(BackendProvider backendProvider) {
        Logger logger = BackendUtil.logger();
        if (backendProvider != null) {
            this.backendProvider = backendProvider;
        } else {
            this.backendProvider
                    = BackendProviderManager.getBackendProvider();
        }
        logger.log(Level.FINE, "Got backend provider {0}",
                this.backendProvider.getClass().getName());
        this.asbInstanceSpecs = new ArrayList<>();
        this.dsbInstanceSpecs = new ArrayList<>();
        this.ksbInstanceSpecs = new ArrayList<>();
    }

    public void init(String configurationId) throws ConfigurationsLoadException {
        this.configurationId = configurationId;
        try {
            asl = backendProvider.getAlgorithmSourceBackendSpecLoader();
            dsl = backendProvider.getDataSourceBackendSpecLoader();
            ksl = backendProvider.getKnowledgeSourceBackendSpecLoader();
        } catch (BackendProviderSpecLoaderException ex) {
            throw new ConfigurationsLoadException(ex);
        }
    }

    public BackendInstanceSpec<? extends Backend> load(String sectionId) throws ConfigurationsLoadException, InvalidConfigurationException {
        BackendInstanceSpec<? extends Backend> result;
        try {
            if (asl.hasSpec(sectionId)) {
                BackendSpec<AlgorithmSourceBackend> loadSpec = asl.loadSpec(sectionId);
                BackendInstanceSpec<AlgorithmSourceBackend> newBackendInstanceSpec = loadSpec.newBackendInstanceSpec();
                result = newBackendInstanceSpec;
                asbInstanceSpecs.add(newBackendInstanceSpec);
            } else if (dsl.hasSpec(sectionId)) {
                BackendSpec<DataSourceBackend> loadSpec = dsl.loadSpec(sectionId);
                BackendInstanceSpec<DataSourceBackend> newBackendInstanceSpec = loadSpec.newBackendInstanceSpec();
                result = newBackendInstanceSpec;
                dsbInstanceSpecs.add(newBackendInstanceSpec);
            } else if (ksl.hasSpec(sectionId)) {
                BackendSpec<KnowledgeSourceBackend> loadSpec = ksl.loadSpec(sectionId);
                BackendInstanceSpec<KnowledgeSourceBackend> newBackendInstanceSpec = loadSpec.newBackendInstanceSpec();
                result = newBackendInstanceSpec;
                ksbInstanceSpecs.add(newBackendInstanceSpec);
            } else {
                throw new InvalidConfigurationException(
                        "The backend " + sectionId + " was not found");
            }
        } catch (BackendSpecNotFoundException e) {
            throw new InvalidConfigurationException(
                    "The backend " + sectionId + " was not found");
        }
        return result;
    }

    public List<BackendInstanceSpec<AlgorithmSourceBackend>> getAlgorithmSourceBackendInstanceSpecs() {
        return asbInstanceSpecs;
    }

    public List<BackendInstanceSpec<DataSourceBackend>> getDataSourceBackendInstanceSpecs() {
        return dsbInstanceSpecs;
    }

    public List<BackendInstanceSpec<KnowledgeSourceBackend>> getKnowledgeSourceBackendInstanceSpecs() {
        return ksbInstanceSpecs;
    }

    public Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setConfigurationId(this.configurationId);
        configuration.setAlgorithmSourceBackendSections(asbInstanceSpecs);
        configuration.setDataSourceBackendSections(dsbInstanceSpecs);
        configuration.setKnowledgeSourceBackendSections(ksbInstanceSpecs);
        return configuration;
    }

}
