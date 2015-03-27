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
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractConfigurations implements Configurations {

    private final BackendProvider backendProvider;

    protected AbstractConfigurations(BackendProvider backendProvider) {
        if (backendProvider != null) {
            this.backendProvider = backendProvider;
        } else {
            this.backendProvider = BackendProviderManager.getBackendProvider();
        }
        if (this.backendProvider == null) {
            throw new AssertionError("No backend provider available!");
        }
    }

    public BackendProvider getBackendProvider() {
        return backendProvider;
    }

    @Override
    public BackendInstanceSpec<AlgorithmSourceBackend> newAlgorithmSourceBackendSection(String id) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException {
        BackendSpecLoader<AlgorithmSourceBackend> loader
                = this.backendProvider.getAlgorithmSourceBackendSpecLoader();
        BackendSpec<AlgorithmSourceBackend> backendSpec
                = loader.loadSpec(id);
        return backendSpec.newBackendInstanceSpec();
    }

    @Override
    public BackendInstanceSpec<DataSourceBackend> newDataSourceBackendSection(String id) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException {
        BackendSpecLoader<DataSourceBackend> loader
                = this.backendProvider.getDataSourceBackendSpecLoader();
        BackendSpec<DataSourceBackend> backendSpec
                = loader.loadSpec(id);
        return backendSpec.newBackendInstanceSpec();
    }

    @Override
    public BackendInstanceSpec<KnowledgeSourceBackend> newKnowledgeSourceBackendSection(String id) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException {
        BackendSpecLoader<KnowledgeSourceBackend> loader
                = this.backendProvider.getKnowledgeSourceBackendSpecLoader();
        BackendSpec<KnowledgeSourceBackend> backendSpec
                = loader.loadSpec(id);
        return backendSpec.newBackendInstanceSpec();
    }

    @Override
    public BackendInstanceSpec<TermSourceBackend> newTermSourceBackendSection(String id) throws BackendSpecNotFoundException, BackendProviderSpecLoaderException {
        BackendSpecLoader<TermSourceBackend> loader
                = this.backendProvider.getTermSourceBackendSpecLoader();
        BackendSpec<TermSourceBackend> backendSpec
                = loader.loadSpec(id);
        return backendSpec.newBackendInstanceSpec();
    }

}
