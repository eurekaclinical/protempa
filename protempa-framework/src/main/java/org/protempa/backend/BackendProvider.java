package org.protempa.backend;

import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;

/**
 * Interface for PROTEMPA backend provider modules.
 *
 * @author Andrew Post
 */
public interface BackendProvider {
    String getDisplayName();
    BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;
    BackendSpecLoader<KnowledgeSourceBackend> 
            getKnowledgeSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;
    BackendSpecLoader<AlgorithmSourceBackend> 
            getAlgorithmSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;
    BackendSpecLoader<TermSourceBackend>
            getTermSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException;

    Object newInstance(String resourceId) throws BackendNewInstanceException ;
}
