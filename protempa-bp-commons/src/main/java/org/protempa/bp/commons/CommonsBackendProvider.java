package org.protempa.bp.commons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ServiceConfigurationError;
import java.util.Set;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.arp.javautil.serviceloader.ServiceLoader;
import org.protempa.AlgorithmSourceBackend;
import org.protempa.DataSourceBackend;
import org.protempa.KnowledgeSourceBackend;
import org.protempa.TermSourceBackend;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;

/**
 *
 * @author Andrew Post
 */
public final class CommonsBackendProvider
        implements BackendProvider {

    @Override
    public String getDisplayName() {
        return CommonsUtil.resourceBundle().getString("displayName");
    }

    @Override
    public BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(DataSourceBackend.class);
    }

    @Override
    public BackendSpecLoader<KnowledgeSourceBackend>
            getKnowledgeSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(KnowledgeSourceBackend.class);
    }

    @Override
    public BackendSpecLoader<AlgorithmSourceBackend>
            getAlgorithmSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(AlgorithmSourceBackend.class);
    }

    @Override
    public BackendSpecLoader<TermSourceBackend> 
            getTermSourceBackendSpecLoader()
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(TermSourceBackend.class);
    }

    private <B extends org.protempa.Backend> BackendSpecLoader<B>
            getBackendSpecLoader(Class<B> clazz) 
            throws BackendProviderSpecLoaderException {
        ArrayList<BackendSpec<B>> backendSpecs =
                new ArrayList<BackendSpec<B>>();
        DiscoverClass discoverClass = new DiscoverClass();
        Set<Class> classNamesL = new HashSet<Class>();
        try {
            classNamesL.add(discoverClass.find(clazz));
        } catch (DiscoveryException de) {
            throw new BackendProviderSpecLoaderException(
                    "Error loading backend", de);
        }
        try {
            classNamesL.addAll(ServiceLoader.load(clazz));
        } catch (ServiceConfigurationError ex) {
            throw new BackendProviderSpecLoaderException(
                    "Error loading backend", ex);
        }
        for (Class className : classNamesL) {
            try {
                backendSpecs.add(BackendSpecFactory.newInstance(this,
                        className));
            } catch (InvalidBackendException ex) {
                throw new BackendProviderSpecLoaderException("Backend "
                        + className + " is invalid", ex);
            }
        }
        return new BackendSpecLoader<B>(backendSpecs);
    }

    @Override
    public Object newInstance(String resourceId)
            throws BackendNewInstanceException {
        try {
            return Class.forName(resourceId).newInstance();
        } catch (InstantiationException ex) {
            throw new BackendNewInstanceException(ex);
        } catch (IllegalAccessException ex) {
            throw new BackendNewInstanceException(ex);
        } catch (ClassNotFoundException ex) {
            throw new BackendNewInstanceException(ex);
        }
    }
}
