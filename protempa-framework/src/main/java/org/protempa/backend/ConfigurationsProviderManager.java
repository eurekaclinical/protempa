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

/**
 * Class for getting PROTEMPA configurations.
 *
 * @author Andrew Post
 */
public final class ConfigurationsProviderManager {
    private static ConfigurationsProvider configurationsProvider;

    static {
        try {
            configurationsProvider = (ConfigurationsProvider)
                    DiscoverSingleton.find(ConfigurationsProvider.class);
        } catch (DiscoveryException de) {
            BackendUtil.logger().log(Level.FINE,
                "No ConfigurationsProvider was found by service discovery.",
                de);
        }
    }

    /**
     * Sets the configurations provider, overriding any configurations
     * provider that was found by service discovery.
     *
     * @param configurationsProvider a {@link ConfigurationsProvider}.
     */
    public static void setConfigurationsProvider(
            ConfigurationsProvider configurationsProvider) {
        ConfigurationsProviderManager.configurationsProvider =
                configurationsProvider;
    }

    /**
     * Gets the configurations provider found by service discovery or set
     * with {@link #setConfigurationsProvider(org.protempa.backend.ConfigurationsProvider).
     *
     * @return a {@link ConfigurationsProvider}.
     */
    public static ConfigurationsProvider getConfigurationsProvider() {
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
        if (configurationsProvider == null)
            throw new IllegalStateException(
                    "No ConfigurationsProvider was found by service discovery or set with setConfigurationsProvider");
        return configurationsProvider.getConfigurations();
    }
}
