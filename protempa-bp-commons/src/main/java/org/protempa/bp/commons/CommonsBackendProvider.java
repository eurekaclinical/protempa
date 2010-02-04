package org.protempa.bp.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.arp.javautil.serviceloader.ServiceLoader;
import org.protempa.AlgorithmSourceBackend;
import org.protempa.DataSourceBackend;
import org.protempa.KnowledgeSourceBackend;
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

    public String getDisplayName() {
        return CommonsUtil.resourceBundle().getString("displayName");
    }

    public BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(DataSourceBackend.class);
    }

    public BackendSpecLoader<KnowledgeSourceBackend>
            getKnowledgeSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(KnowledgeSourceBackend.class);
    }

    public BackendSpecLoader<AlgorithmSourceBackend>
            getAlgorithmSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        return getBackendSpecLoader(AlgorithmSourceBackend.class);
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
            throw new BackendProviderSpecLoaderException(de);
        }
        try {
            classNamesL.addAll(ServiceLoader.load(clazz));
        } catch (IOException ex) {
            throw new BackendProviderSpecLoaderException(ex);
        } catch (ClassNotFoundException ex) {
            throw new BackendProviderSpecLoaderException(ex);
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
