package org.protempa.backend;

import java.util.List;

/**
 *
 * @author Andrew Post
 */
public interface Configurations {
    <B extends Backend> List<BackendInstanceSpec<B>>
            load(String configurationsId, BackendSpec<B> backendSpec)
            throws ConfigurationsLoadException;
    void save(String configurationsId, 
            List<BackendInstanceSpec> backendInstanceSpec)
            throws ConfigurationsSaveException;
    void remove(String configurationsId)
            throws ConfigurationRemoveException;
    List<String> loadConfigurationIds(String configurationsId)
            throws ConfigurationsLoadException;
}
