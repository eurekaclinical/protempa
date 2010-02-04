package org.protempa.backend.test;

import org.protempa.backend.*;
import java.util.Collections;
import org.protempa.AlgorithmSourceBackend;
import org.protempa.DataSourceBackend;
import org.protempa.KnowledgeSourceBackend;

public final class MockBackendProvider implements BackendProvider {

    public String getDisplayName() {
        return "Provider 1";
    }

    public BackendSpecLoader<DataSourceBackend>
            getDataSourceBackendSpecLoader() {
        return new BackendSpecLoader<DataSourceBackend>(
                Collections.singletonList(
            new BackendSpec<DataSourceBackend>(
            this, "DSBackendSpec1", "DS Backend Spec 1", null)));
    }

    public BackendSpecLoader<KnowledgeSourceBackend>
            getKnowledgeSourceBackendSpecLoader() {
        return new BackendSpecLoader<KnowledgeSourceBackend>(
            Collections.singletonList(
            new BackendSpec<KnowledgeSourceBackend>(
            this, "KSBackendSpec1",
            "KS Backend Spec 1", null)));
    }

    public BackendSpecLoader<AlgorithmSourceBackend>
            getAlgorithmSourceBackendSpecLoader() {
        return new BackendSpecLoader<AlgorithmSourceBackend>(
            Collections.singletonList(
            new BackendSpec<AlgorithmSourceBackend>(
            this, "ASBackendSpec1",
            "AS Backend Spec 1", Collections.singletonList(
            new BackendPropertySpec("url", "URL",
            "The URL to the knowledge base", String.class,
            new DefaultBackendPropertyValidator())))));
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
