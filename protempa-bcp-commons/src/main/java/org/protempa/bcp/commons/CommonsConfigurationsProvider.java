package org.protempa.bcp.commons;

import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsProvider;

/**
 *
 * @author Andrew Post
 */
public class CommonsConfigurationsProvider implements ConfigurationsProvider {

    public Configurations getConfigurations() {
        Configurations result = (Configurations)
                DiscoverSingleton.find(Configurations.class);
        if (result == null)
            throw new AssertionError("No Configurations classes found!");
        return result;
    }

    

}
