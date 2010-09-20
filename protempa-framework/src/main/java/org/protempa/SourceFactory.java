package org.protempa;

import org.protempa.backend.InvalidConfigurationsException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import sun.swing.BakedArrayList;

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

    public SourceFactory(String configurationsId)
            throws ConfigurationsLoadException,
            BackendProviderSpecLoaderException, InvalidConfigurationsException {
        BackendProvider backendProvider = BackendProviderManager
                .getBackendProvider();

        Configurations configurations = ConfigurationsProviderManager
                .getConfigurationsProvider().getConfigurations();

        BackendSpecLoader<AlgorithmSourceBackend> asl = backendProvider
                .getAlgorithmSourceBackendSpecLoader();
        BackendSpecLoader<DataSourceBackend> dsl = backendProvider
                .getDataSourceBackendSpecLoader();
        BackendSpecLoader<KnowledgeSourceBackend> ksl = backendProvider
                .getKnowledgeSourceBackendSpecLoader();
        BackendSpecLoader<TermSourceBackend> tsl = backendProvider
                .getTermSourceBackendSpecLoader();

        for (String configurationId : configurations
                .loadConfigurationIds(configurationsId)) {
            if (!asl.hasSpec(configurationId) && !dsl.hasSpec(configurationId)
                    && !ksl.hasSpec(configurationId)
                    && !tsl.hasSpec(configurationId))
                throw new InvalidConfigurationsException("The backend "
                        + configurationId + " was not found");
        }

        this.algorithmSourceBackendInstanceSpecs = 
                new ArrayList<BackendInstanceSpec<AlgorithmSourceBackend>>();

        for (BackendSpec backendSpec : asl) {
            this.algorithmSourceBackendInstanceSpecs.addAll(configurations
                    .load(configurationsId, backendSpec));
        }

        this.dataSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<DataSourceBackend>>();

        for (BackendSpec backendSpec : dsl) {
            this.dataSourceBackendInstanceSpecs.addAll(configurations.load(
                    configurationsId, backendSpec));
        }

        this.knowledgeSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<KnowledgeSourceBackend>>();

        for (BackendSpec backendSpec : ksl) {
            this.knowledgeSourceBackendInstanceSpecs.addAll(configurations
                    .load(configurationsId, backendSpec));
        }

        this.termSourceBackendInstanceSpecs = 
            new ArrayList<BackendInstanceSpec<TermSourceBackend>>();
        for (BackendSpec backendSpec : tsl) {
            this.termSourceBackendInstanceSpecs.addAll(configurations.load(
                    configurationsId, backendSpec));
        }
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
