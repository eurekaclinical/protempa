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
