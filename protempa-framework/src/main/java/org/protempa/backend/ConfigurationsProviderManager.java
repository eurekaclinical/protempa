package org.protempa.backend;

import java.util.logging.Level;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.DiscoverSingleton;

/**
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
                "No ConfigurationsProvider classes were found by service discovery.",
                de);
        }
    }

    public static void setConfigurationsProvider(
            ConfigurationsProvider configurationsProvider) {
        ConfigurationsProviderManager.configurationsProvider =
                configurationsProvider;
    }

    public static ConfigurationsProvider getConfigurationsProvider() {
        return configurationsProvider;
    }
}
