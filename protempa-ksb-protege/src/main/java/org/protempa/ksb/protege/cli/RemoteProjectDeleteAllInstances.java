package org.protempa.ksb.protege.cli;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import java.util.Collection;
import org.apache.commons.cli.CommandLine;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.cli.CLI;
import org.protempa.ksb.protege.RemoteProjectFactory;

/**
 *
 * @author Andrew Post
 */
public class RemoteProjectDeleteAllInstances extends CLI {

    public RemoteProjectDeleteAllInstances() {
        super(System.getProperty("app.name"),
                new Argument[] {
            new Argument("configurationId", true),
            new Argument("host", true),
            new Argument("knowledgeBaseName", true)
        }, false);
    }
    public static void main(String[] args) {
        RemoteProjectDeleteAllInstances pdai =
                new RemoteProjectDeleteAllInstances();
        CommandLine commandLine = pdai.processOptionsAndArgs(args);
        String[] leftOverArgs = commandLine.getArgs();

        String configurationId = leftOverArgs[0];
        String host = leftOverArgs[1];
        String knowledgeBaseName = leftOverArgs[2];
        Project project = null;
        try {
            project = new RemoteProjectFactory().getInstance(configurationId,
                    host, knowledgeBaseName);
        } catch (ConfigurationsLoadException ex) {
            pdai.printException(ex);
        } catch (BackendProviderSpecLoaderException ex) {
            pdai.printException(ex);
        } catch (InvalidPropertyNameException ex) {
            pdai.printException(ex);
        }
        try {
            KnowledgeBase kb = project.getKnowledgeBase();
            Collection<Instance> instances = kb.getInstances();
            for (Instance instance : instances) {
                    if (!instance.isSystem() && !(instance instanceof Cls)
                            && !(instance instanceof Slot)
                            && !(instance instanceof Facet)) {
                        instance.delete();
                    }
                }
        } finally {
            project.dispose();
        }
    }
}
