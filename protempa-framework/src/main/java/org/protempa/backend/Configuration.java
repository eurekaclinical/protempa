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
import java.util.Collections;
import java.util.List;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class Configuration {

    private String configurationId;

    private List<BackendInstanceSpec<DataSourceBackend>> dataSourceBackendSections;

    private List<BackendInstanceSpec<KnowledgeSourceBackend>> knowledgeSourceBackendSections;

    private List<BackendInstanceSpec<AlgorithmSourceBackend>> algorithmSourceBackendSections;

    public Configuration() {
        this.dataSourceBackendSections = Collections.emptyList();
        this.knowledgeSourceBackendSections = Collections.emptyList();
        this.algorithmSourceBackendSections = Collections.emptyList();
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }
    
    public void setDataSourceBackendSections(List<BackendInstanceSpec<DataSourceBackend>> dataSourceBackendSections) {
        if (dataSourceBackendSections != null) {
            this.dataSourceBackendSections = dataSourceBackendSections;
        } else {
            this.dataSourceBackendSections = Collections.emptyList();
        }
    }

    public void setKnowledgeSourceBackendSections(List<BackendInstanceSpec<KnowledgeSourceBackend>> knowledgeSourceBackendSections) {
        if (knowledgeSourceBackendSections != null) {
            this.knowledgeSourceBackendSections = knowledgeSourceBackendSections;
        } else {
            this.knowledgeSourceBackendSections = Collections.emptyList();
        }
    }

    public void setAlgorithmSourceBackendSections(List<BackendInstanceSpec<AlgorithmSourceBackend>> algorithmSourceBackendSections) {
        if (algorithmSourceBackendSections != null) {
            this.algorithmSourceBackendSections = algorithmSourceBackendSections;
        } else {
            this.algorithmSourceBackendSections = Collections.emptyList();
        }
    }

    public List<BackendInstanceSpec<DataSourceBackend>> getDataSourceBackendSections() {
        return new ArrayList<>(dataSourceBackendSections);
    }

    public List<BackendInstanceSpec<KnowledgeSourceBackend>> getKnowledgeSourceBackendSections() {
        return new ArrayList<>(knowledgeSourceBackendSections);
    }

    public List<BackendInstanceSpec<AlgorithmSourceBackend>> getAlgorithmSourceBackendSections() {
        return new ArrayList<>(algorithmSourceBackendSections);
    }

    public List<BackendInstanceSpec<? extends Backend>> getAllSections() {
        List<BackendInstanceSpec<? extends Backend>> result = new ArrayList<>(
                this.dataSourceBackendSections.size() + 
                        this.knowledgeSourceBackendSections.size() + 
                        this.algorithmSourceBackendSections.size());
        result.addAll(this.dataSourceBackendSections);
        result.addAll(this.knowledgeSourceBackendSections);
        result.addAll(this.algorithmSourceBackendSections);
        return result;
    }
    
    public void merge(Configuration otherConfiguration) throws InvalidPropertyNameException, InvalidPropertyValueException {
        if (otherConfiguration != null) {
            for (int i = 0, n = otherConfiguration.algorithmSourceBackendSections.size(); i < n; i++) {
                BackendInstanceSpec<AlgorithmSourceBackend> otherBis = otherConfiguration.algorithmSourceBackendSections.get(i);
                if (this.algorithmSourceBackendSections.size() >= i + 1) {
                    BackendInstanceSpec<AlgorithmSourceBackend> bis = this.algorithmSourceBackendSections.get(i);
                    if (bis.getBackendSpec().getId().equals(otherBis.getBackendSpec().getId())) {
                        for (String name : otherBis.getPropertyNames()) {
                            bis.setProperty(name, otherBis.getProperty(name));
                        }
                    }
                }
            }
            
            for (int i = 0, n = otherConfiguration.dataSourceBackendSections.size(); i < n; i++) {
                BackendInstanceSpec<DataSourceBackend> otherBis = otherConfiguration.dataSourceBackendSections.get(i);
                if (this.dataSourceBackendSections.size() >= i + 1) {
                    BackendInstanceSpec<DataSourceBackend> bis = this.dataSourceBackendSections.get(i);
                    if (bis.getBackendSpec().getId().equals(otherBis.getBackendSpec().getId())) {
                        for (String name : otherBis.getPropertyNames()) {
                            bis.setProperty(name, otherBis.getProperty(name));
                        }
                    }
                }
            }
            
            for (int i = 0, n = otherConfiguration.knowledgeSourceBackendSections.size(); i < n; i++) {
                BackendInstanceSpec<KnowledgeSourceBackend> otherBis = otherConfiguration.knowledgeSourceBackendSections.get(i);
                if (this.knowledgeSourceBackendSections.size() >= i + 1) {
                    BackendInstanceSpec<KnowledgeSourceBackend> bis = this.knowledgeSourceBackendSections.get(i);
                    if (bis.getBackendSpec().getId().equals(otherBis.getBackendSpec().getId())) {
                        for (String name : otherBis.getPropertyNames()) {
                            bis.setProperty(name, otherBis.getProperty(name));
                        }
                    }
                }
            }
            
        }
    }

}
