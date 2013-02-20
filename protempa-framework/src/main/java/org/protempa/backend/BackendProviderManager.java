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

import org.arp.javautil.serviceloader.SingletonServiceLoader;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;

/**
 * Manages PROTEMPA's backend provider.
 *
 * Uses Java's {@link ServiceLoader} to load a {@link BackendProvider}. 
 * By default, it configures {@link ServiceLoader} to use the current thread's 
 * context class loader. The {@link BackendProvider} is loaded lazily
 * upon calls to {@link #getBackendProvider()} or the backend spec loader 
 * getter methods. Use {@link #setBackendProviderClassLoader} to specify a 
 * different class loader.
 *
 * A program can also explicitly set a backend provider using the
 * {@link #setBackendProvider} method. This will override the 
 * use of {@link ServiceLoader}.
 *
 * @author Andrew Post
 */
public final class BackendProviderManager {

    private static BackendProvider backendProvider;
    private static ClassLoader backendProviderClassLoader;
    private static boolean backendProviderClassLoaderSpecified;

    private BackendProviderManager() {
    }

    /**
     * Sets the class loader to use for loading a {@link BackendProvider}. If
     * never called, the current thread's context class loader will be used.
     * If set to <code>null</code>, the system class loader (or, failing that, 
     * the bootstrap class loader) will be used.
     * 
     * @param loader 
     */
    public static void setBackendProviderClassLoader(ClassLoader loader) {
        backendProviderClassLoader = loader;
        backendProviderClassLoaderSpecified = true;
    }
    
    public static ClassLoader getBackendProviderClassLoader() {
        return backendProviderClassLoader;
    }
    
    /**
     * Indicates whether {@link #setBackendProviderClassLoader} has been 
     * called.
     * 
     * @return whether {@link #setBackendProviderClassLoader} has been called.
     */
    public static boolean isBackendProviderClassLoaderSpecified() {
        return backendProviderClassLoaderSpecified;
    }

    public static void setBackendProvider(BackendProvider backendProvider) {
        BackendProviderManager.backendProvider = backendProvider;
    }

    public static BackendProvider getBackendProvider() {
        loadBackendProviderIfNeeded();
        return backendProvider;
    }

    public static BackendSpecLoader<DataSourceBackend> getDataSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        loadBackendProviderIfNeeded();
        return backendProvider.getDataSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<KnowledgeSourceBackend> getKnowledgeSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        loadBackendProviderIfNeeded();
        return backendProvider.getKnowledgeSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<AlgorithmSourceBackend> getAlgorithmSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        loadBackendProviderIfNeeded();
        return backendProvider.getAlgorithmSourceBackendSpecLoader();
    }

    public static BackendSpecLoader<TermSourceBackend> getTermSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        loadBackendProviderIfNeeded();
        return backendProvider.getTermSourceBackendSpecLoader();
    }

    private static void loadBackendProviderIfNeeded() {
        if (backendProvider == null) {
            if (backendProviderClassLoaderSpecified) {
                backendProvider = SingletonServiceLoader.load(BackendProvider.class, backendProviderClassLoader);
            } else {
                backendProvider = SingletonServiceLoader.load(BackendProvider.class);
        
            }
        }
        if (backendProvider == null) {
            throw new IllegalStateException("No backendProvider found by service discovery or set with setBackendProvicer");
        }
    }
}
