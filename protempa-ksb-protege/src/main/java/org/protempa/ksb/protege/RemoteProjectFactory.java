package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import java.util.List;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsProviderManager;
import org.protempa.backend.InvalidPropertyNameException;

/**
 * Opens a remote Protege project that is specified in a PROTEMPA
 * configuration. A remote Protege project is uniquely specified by its
 * hostname (and port).
 *
 * @param configurationId the id {@link String} of the configuration.
 * @param host the hostname {@link String}.
 * @param knowledgeBaseName the name {@link String} of the knowledge base.
 * @return a Protege {@link Project}.
 * 
 * @author Andrew Post
 */
public class RemoteProjectFactory {
    public Project getInstance(String configurationId,
            String host, String knowledgeBaseName)
            throws ConfigurationsLoadException,
            BackendProviderSpecLoaderException, InvalidPropertyNameException {
        Configurations configurations =
                ConfigurationsProviderManager.getConfigurations();
        BackendSpecLoader<KnowledgeSourceBackend> ksl =
                BackendProviderManager.getKnowledgeSourceBackendSpecLoader();
        String hostname = null;
        String username = null;
        String password = null;
        String projectName = null;
        KNOWLEDGE_SOURCE_BACKEND_SPEC_LOOP:
        for (BackendSpec backendSpec : ksl) {
            if (backendSpec.getId().equals(
                    RemoteKnowledgeSourceBackend.class.getName())) {
                List<BackendInstanceSpec<KnowledgeSourceBackend>> specs =
                        configurations.load(configurationId, backendSpec);
                for (BackendInstanceSpec<KnowledgeSourceBackend> spec :
                        specs) {
                    String hname = (String) spec.getProperty("hostname");
                    if (host.equals(hname)) {
                        hostname = hname;
                        username = (String) spec.getProperty("username");
                        password = (String) spec.getProperty("password");
                        projectName =
                                (String) spec.getProperty("knowledgeBaseName");
                        break KNOWLEDGE_SOURCE_BACKEND_SPEC_LOOP;
                    }
                }
            }
        }
        Project project = RemoteProjectManager.getInstance().getProject(
                hostname, username, password, projectName, false);
        return project;
    }
}
