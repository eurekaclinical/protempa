package org.protempa.backend;

import java.util.logging.Level;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.DiscoverSingleton;

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
}
