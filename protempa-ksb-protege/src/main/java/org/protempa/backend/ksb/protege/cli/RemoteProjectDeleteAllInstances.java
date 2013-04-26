/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.backend.ksb.protege.cli;

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
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.cli.CLI;
import org.protempa.backend.ksb.protege.RemoteProjectFactory;

/**
 *
 * @author Andrew Post
 */
public class RemoteProjectDeleteAllInstances extends CLI {

    public RemoteProjectDeleteAllInstances() {
        super(new Argument[] {
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
        } catch (ConfigurationsNotFoundException ex) {
            pdai.printException(ex);
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
