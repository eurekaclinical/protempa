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
package org.protempa.backend.test;

import org.protempa.backend.*;

import java.util.Collections;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;

public final class MockBackendProvider implements BackendProvider {

    @Override
    public String getDisplayName() {
        return "Provider 1";
    }

    @Override
    public BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader() {
        return new BackendSpecLoader<>(
                Collections.singletonList(
                        new BackendSpec<DataSourceBackend>(
                                this, "DSBackendSpec1", "DS Backend Spec 1", null)));
    }

    @Override
    public BackendSpecLoader<KnowledgeSourceBackend>
            getKnowledgeSourceBackendSpecLoader() {
        return new BackendSpecLoader<>(
                Collections.singletonList(
                        new BackendSpec<KnowledgeSourceBackend>(
                                this, "KSBackendSpec1",
                                "KS Backend Spec 1", null)));
    }

    @Override
    public BackendSpecLoader<AlgorithmSourceBackend>
            getAlgorithmSourceBackendSpecLoader() {
        return new BackendSpecLoader<>(
                Collections.singletonList(
                        new BackendSpec<AlgorithmSourceBackend>(
                                this, "ASBackendSpec1",
                                "AS Backend Spec 1", new BackendPropertySpec[]{
                                    new BackendPropertySpec("url", "URL",
                                            "The URL to the knowledge base", BackendPropertyType.STRING,
                                            false, new DefaultBackendPropertyValidator())})));
    }

    @Override
    public BackendSpecLoader<TermSourceBackend> getTermSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        return new BackendSpecLoader<>(
                Collections.singletonList(
                        new BackendSpec<TermSourceBackend>(
                                this, "TSBackendSpec1", "TS Backend Spec 1", null)));
    }

    @Override
    public Object newInstance(String resourceId)
            throws BackendNewInstanceException {
        try {
            return Class.forName(resourceId).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException ex) {
            throw new BackendNewInstanceException(ex);
        }
    }
}
