package org.protempa.ksb.protege.cli;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.cli.CommandLine;
import org.protempa.cli.CLI;

/**
 *
 * @author Andrew Post
 */
public class LocalProjectDeleteAllInstances extends CLI {

    public LocalProjectDeleteAllInstances() {
        super(System.getProperty("app.name"),
                new Argument[]{
                    new Argument("projectString", true)
                }, false);
    }

    public static void main(String[] args) {
        LocalProjectDeleteAllInstances pdai =
                new LocalProjectDeleteAllInstances();
        CommandLine commandLine = pdai.processOptionsAndArgs(args);
        String[] leftOverArgs = commandLine.getArgs();

        String projectString = leftOverArgs[0];
        Collection errors = new ArrayList();
        Project project = new Project(projectString, errors);
        if (errors.isEmpty()) {
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
        } else {
            System.err.println("Errors opening Protege project: ");
            for (Object error : errors) {
                System.err.println(error);
            }
            System.exit(1);
        }
    }
}
