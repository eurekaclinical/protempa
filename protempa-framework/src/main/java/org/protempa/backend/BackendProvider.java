package org.protempa.backend;

import org.protempa.AlgorithmSourceBackend;
import org.protempa.DataSourceBackend;
import org.protempa.KnowledgeSourceBackend;
import org.protempa.TermSourceBackend;

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
