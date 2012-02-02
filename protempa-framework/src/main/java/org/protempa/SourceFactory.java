package org.protempa;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.tsb.TermSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.InvalidConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsProviderManager;


/**
 * 
 * @author Andrew Post
 */
public final class SourceFactory {

    private final List<BackendInstanceSpec<AlgorithmSourceBackend>>
            algorithmSourceBackendInstanceSpecs;

    private final List<BackendInstanceSpec<DataSourceBackend>>
            dataSourceBackendInstanceSpecs;

    private final List<BackendInstanceSpec<KnowledgeSourceBackend>>
            knowledgeSourceBackendInstanceSpecs;

    private final List<BackendInstanceSpec<TermSourceBackend>>
            termSourceBackendInstanceSpecs;

    public SourceFactory(String configurationId) 
            throws ConfigurationsLoadException, BackendProviderSpecLoaderException,
            InvalidConfigurationException {
        Logger logger = ProtempaUtil.logger();
        logger.fine("Loading backend provider");
        BackendProvider backendProvider =
                BackendProviderManager.getBackendProvider();
        logger.log(Level.FINE, "Got backend provider {0}", 
                backendProvider.getClass().getName());
        logger.fine("Loading configurations");
        Configurations configurations =
                ConfigurationsProviderManager.getConfigurations();
        logger.fine("Got available configurations");
        logger.fine("Loading configuration " + configurationId);
        BackendSpecLoader<AlgorithmSourceBackend> asl =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        BackendSpecLoader<DataSourceBackend> dsl =
                backendProvider.getDataSourceBackendSpecLoader();
        BackendSpecLoader<KnowledgeSourceBackend> ksl =
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        BackendSpecLoader<TermSourceBackend> tsl =
                backendProvider.getTermSourceBackendSpecLoader();

        for (String specId :
            configurations.loadConfigurationIds(configurationId)) {
            if (!asl.hasSpec(specId) && !dsl.hasSpec(specId)
                    && !ksl.hasSpec(specId) && !tsl.hasSpec(specId))
                throw new InvalidConfigurationException(
                        "The backend " + specId + " was not found");
        }

        this.algorithmSourceBackendInstanceSpecs = 
                new ArrayList<BackendInstanceSpec<AlgorithmSourceBackend>>();

        for (BackendSpec backendSpec : asl) {
            this.algorithmSourceBackendInstanceSpecs
                    .addAll(configurations.load(configurationId, backendSpec));
        }

        this.dataSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<DataSourceBackend>>();

        for (BackendSpec backendSpec : dsl) {
            this.dataSourceBackendInstanceSpecs
                    .addAll(configurations.load(configurationId, backendSpec));
        }

        this.knowledgeSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<KnowledgeSourceBackend>>();

        for (BackendSpec backendSpec : ksl) {
            this.knowledgeSourceBackendInstanceSpecs
                    .addAll(configurations.load(configurationId, backendSpec));
        }

        this.termSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<TermSourceBackend>>();
        for (BackendSpec backendSpec : tsl) {
            this.termSourceBackendInstanceSpecs
                    .addAll(configurations.load(configurationId, backendSpec));
        }
        logger.fine("Configuration " + configurationId + " loaded");
    }

    public DataSource newDataSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        DataSourceBackend[] backends = new DataSourceBackend[
                                           this.dataSourceBackendInstanceSpecs
                                           .size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.dataSourceBackendInstanceSpecs.get(i)
                    .getInstance();
        }
        return new DataSource(backends);
    }

    public KnowledgeSource newKnowledgeSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        KnowledgeSourceBackend[] backends = new KnowledgeSourceBackend[
                                            this.knowledgeSourceBackendInstanceSpecs
                                            .size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.knowledgeSourceBackendInstanceSpecs.get(i)
                    .getInstance();
        }
        return new KnowledgeSource(backends);
    }

    public AlgorithmSource newAlgorithmSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        AlgorithmSourceBackend[] backends = new AlgorithmSourceBackend[
                                            this.algorithmSourceBackendInstanceSpecs
                                            .size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.algorithmSourceBackendInstanceSpecs.get(i)
                    .getInstance();
        }
        return new AlgorithmSource(backends);
    }

    public TermSource newTermSourceInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        TermSourceBackend[] backends = new TermSourceBackend[
                                       this.termSourceBackendInstanceSpecs
                                       .size()];
        for (int i = 0; i < backends.length; i++) {
            backends[i] = this.termSourceBackendInstanceSpecs.get(i)
                    .getInstance();
        }
        return new TermSource(backends);

    }
}
