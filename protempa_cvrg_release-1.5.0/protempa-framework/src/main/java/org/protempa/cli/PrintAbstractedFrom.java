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
import org.protempa.*;

/**
 *
 * @author Andrew Post
 */
public class PrintAbstractedFrom extends CLI {

    public PrintAbstractedFrom() {
        super(new Argument[]{new Argument("propositionId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        String propositionId = commandLine.getArgs()[0];
        KnowledgeSource knowledgeSource = protempa.getKnowledgeSource();

        try {
            List<? extends PropositionDefinition> result =
                    knowledgeSource.readAbstractedFrom(propositionId);
            PropositionDefinitionPrinter printer =
                    new PropositionDefinitionPrinter();
            printer.visit(result);
        } catch (KnowledgeSourceReadException ex) {
            throw new CLIException("Error reading proposition '" + 
                    propositionId + "'", ex);
        }

    }

    public static void main(String[] args) {
        PrintAbstractedFrom pl = new PrintAbstractedFrom();
        pl.processOptionsAndArgs(args);
        pl.initializeExecuteAndClose();
    }
}
