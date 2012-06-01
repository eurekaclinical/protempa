/*
 * #%L
 * Protempa Framework
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
package org.protempa.cli;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.Protempa;
import org.protempa.TermSource;
import org.protempa.TermSourceReadException;
import org.protempa.TermSubsumption;
import org.protempa.query.And;

/**
 * Prints the ids of the proposition definitions that are associated with
 * a given term.
 * 
 * @author Andrew Post
 */
public class PrintPropositionDefinitionsForTerm extends CLI {

    public PrintPropositionDefinitionsForTerm() {
        super(new Argument[]{new Argument("termId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        String termId = commandLine.getArgs()[0];
        KnowledgeSource knowledgeSource = protempa.getKnowledgeSource();
        TermSource termSource = protempa.getTermSource();
        try {
            List<String> termList = termSource.getTermSubsumption(termId);
            TermSubsumption ts = TermSubsumption.fromTerms(termList);
            List<String> propIds =
                    knowledgeSource.getPropositionDefinitionsByTerm(
                    new And<TermSubsumption>(ts));
            if (propIds.isEmpty()) {
                System.out.println(
                        "No proposition definitions are associated with term "
                        + termId);
            } else {
                if (propIds.size() == 1) {
                    System.out.println(
                            "1 proposition definition is associated with term "
                            + termId + ":");
                } else {
                    System.out.println(propIds.size()
                        + " proposition definitions are associated with term "
                        + termId + ":");
                }
                for (String propId : propIds) {
                    System.out.println(propId);
                }
            }
        } catch (KnowledgeSourceReadException ex) {
            throw new CLIException("Error reading term and its children", ex);
        } catch (TermSourceReadException ex) {
            throw new CLIException("Error reading term and its children", ex);
        }
    }

    public static void main(String[] args) {
        PrintPropositionDefinitionsForTerm pt =
                new PrintPropositionDefinitionsForTerm();
        pt.processOptionsAndArgs(args);
        pt.initializeExecuteAndClose();
    }
}
