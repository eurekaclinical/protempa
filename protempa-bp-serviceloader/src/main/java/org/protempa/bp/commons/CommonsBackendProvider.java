/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.bp.commons;

import java.util.*;
import org.arp.javautil.serviceloader.ClassServiceLoader;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;
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

    private <B extends org.protempa.backend.Backend> BackendSpecLoader<B>
            getBackendSpecLoader(Class<B> clazz) 
            throws BackendProviderSpecLoaderException {
        ArrayList<BackendSpec<B>> backendSpecs =
                new ArrayList<>();
        List<Class<?>> classNamesL = new ArrayList<>();
        try {
            classNamesL.addAll(ClassServiceLoader.load(clazz));
        } catch (ServiceConfigurationError ex) {
            throw new BackendProviderSpecLoaderException(
                    "Error loading backend " + clazz.getName(), ex);
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
        return new BackendSpecLoader<>(backendSpecs);
    }

    @Override
    public Object newInstance(String resourceId)
            throws BackendNewInstanceException {
        try {
            return Class.forName(resourceId).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException ex) {
            throw new BackendNewInstanceException(ex);
        }
    }
}
