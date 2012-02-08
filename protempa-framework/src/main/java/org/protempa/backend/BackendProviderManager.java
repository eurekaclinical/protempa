/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import java.util.logging.Level;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;

/**
 * A service for managing PROTEMPA's backend provider.
 *
 * As part of its initialization, the {@link BackendProviderManager} class will
 * attempt to load the {@link BackendProvider} class referenced in the
 * "org.protempa.backend.BackendProvider" system property. This allows users to
 * customize the PROTEMPA backend provider used by their applications.
 *
 * A program can also explicitly load a backend provider using the
 * {@link #setBackendProvider} method. This will override the initialization
 * above.
 * 
 * @author Andrew Post
 */
public final class BackendProviderManager {

    private static BackendProvider backendProvider;

    private BackendProviderManager() {
        
    }
    
    static {
        try {
            backendProvider = (BackendProvider)
                    DiscoverSingleton.find(BackendProvider.class);
        } catch (DiscoveryException de) {
            BackendUtil.logger().log(Level.FINE,
                "No BackendProvider classes were found by service discovery.",
                de);
        }
    }

    public static void setBackendProvider(BackendProvider backendProvider) {
        BackendProviderManager.backendProvider = backendProvider;
    }

    public static BackendProvider getBackendProvider() {
        return backendProvider;
    }

    public static BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        if (backendProvider == null)
            throw new IllegalStateException("No backendProvider found by service discovery or set with setBackendProvicer");
        return backendProvider.getDataSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<KnowledgeSourceBackend>
            getKnowledgeSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        if (backendProvider == null)
            throw new IllegalStateException("No backendProvider found by service discovery or set with setBackendProvicer");
        return backendProvider.getKnowledgeSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<AlgorithmSourceBackend>
            getAlgorithmSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        if (backendProvider == null)
            throw new IllegalStateException("No backendProvider found by service discovery or set with setBackendProvicer");
        return backendProvider.getAlgorithmSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<TermSourceBackend>
            getTermSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        if (backendProvider == null)
            throw new IllegalStateException("No backendProvider found by service discovery or set with setBackendProvicer");
        return backendProvider.getTermSourceBackendSpecLoader();
    }
}
