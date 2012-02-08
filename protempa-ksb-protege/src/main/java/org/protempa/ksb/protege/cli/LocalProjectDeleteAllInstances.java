/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 Emory University
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
