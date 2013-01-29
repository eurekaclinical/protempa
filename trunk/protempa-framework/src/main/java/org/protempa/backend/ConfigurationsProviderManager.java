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

import org.arp.javautil.serviceloader.SingletonServiceLoader;

/**
 * Class for getting PROTEMPA configurations.
 *
 * Uses Java's {@link ServiceLoader} to load a {@link ConfigurationsProvider}.
 * By default, it configures {@link ServiceLoader} to use the current thread's
 * context class loader. The {@link ConfigurationsProvider} is loaded lazily
 * upon calls to {@link #getConfigurations()} or
 * {@link #getConfigurationsProvider()}. Use
 * {@link #setConfigurationsProviderClassLoader} to specify a different class
 * loader. The {@link ConfigurationsProvider} in turn is used to get
 * {@link Configurations}.
 *
 * A program can also explicitly set a configurations provider using the
 * {@link #setConfigurationsProvider} method. This will override the use of {@link ServiceLoader}.
 *
 * @author Andrew Post
 */
public final class ConfigurationsProviderManager {

    private static ConfigurationsProvider configurationsProvider;
    private static ClassLoader configurationsProviderClassLoader;
    private static boolean configurationsProviderClassLoaderSpecified;

    public static void setConfigurationsProviderClassLoader(
            ClassLoader loader) {
        configurationsProviderClassLoader = loader;
        configurationsProviderClassLoaderSpecified = true;
    }

    public static ClassLoader getConfigurationsProviderClassLoader() {
        return configurationsProviderClassLoader;
    }

    /**
     * Indicates whether {@link #setConfigurationsProviderClassLoader} has been
     * called.
     *
     * @return whether {@link #setConfigurationsProviderClassLoader} has been
     * called.
     */
    public static boolean isConfigurationsProviderClassLoaderSpecified() {
        return configurationsProviderClassLoaderSpecified;
    }

    /**
     * Sets the configurations provider, overriding any configurations provider
     * that was found by service discovery.
     *
     * @param configurationsProvider a {@link ConfigurationsProvider}.
     */
    public static void setConfigurationsProvider(
            ConfigurationsProvider configurationsProvider) {
        ConfigurationsProviderManager.configurationsProvider =
                configurationsProvider;
    }

    /**
     * Gets the configurations provider found by service discovery or set with 
     * {@link #setConfigurationsProvider(org.protempa.backend.ConfigurationsProvider).
     *
     * @return a {@link ConfigurationsProvider}.
     */
    public static ConfigurationsProvider getConfigurationsProvider() {
        loadConfigurationsProviderIfNeeded();
        return configurationsProvider;
    }

    /**
     * Convenience static method for getting configurations, like calling
     * {@link #getConfigurationsProvider()} followed by
     * {@link ConfigurationsProvider#getConfigurations()}.
     *
     * @return a {@link Configurations} instance.
     */
    public static Configurations getConfigurations() {
        loadConfigurationsProviderIfNeeded();
        return configurationsProvider.getConfigurations();
    }

    private static void loadConfigurationsProviderIfNeeded() {
        if (configurationsProvider == null) {
            if (configurationsProviderClassLoaderSpecified) {
                configurationsProvider =
                        SingletonServiceLoader.load(ConfigurationsProvider.class,
                        configurationsProviderClassLoader);
            } else {
                configurationsProvider =
                        SingletonServiceLoader.load(ConfigurationsProvider.class);
            }
        }
        
        if (configurationsProvider == null) {
            throw new IllegalStateException("No configurationsProvider found by service discovery or set with setConfigurationsProvider");
        }
    }
}
