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

import org.apache.commons.cli.CommandLine;
import org.protempa.Protempa;
import org.protempa.Term;
import org.protempa.TermSource;
import org.protempa.TermSourceReadException;
import org.protempa.Terminology;

/**
 * Prints a specified term to the console.
 * 
 * @author Andrew Post
 */
public class PrintTerm extends CLI {

    public PrintTerm() {
        super(System.getProperty("app.name"),
                new Argument[]{new Argument("termId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        String termId = commandLine.getArgs()[0];
        TermSource termSource = protempa.getTermSource();
        Term term;
        try {
            term = termSource.readTerm(termId);
        } catch (TermSourceReadException ex) {
            throw new CLIException("Error reading term", ex);
        }
        if (term == null) {
            System.out.println("No term with id " + termId);
        } else {
            System.out.println("Term: " + termId);
            Terminology terminology = term.getTerminology();
            String terminologyName = terminology.getName();
            String terminologyVersion = terminology.getVersion();
            System.out.println("Terminology: " + 
                    terminologyName + (terminologyVersion != null ? " (v. " +
                    terminologyVersion + ")" : ""));
            System.out.println("Code: " + term.getCode());
            System.out.println("Display name: " + term.getDisplayName());
            System.out.println("Abbreviated display name: "
                    + term.getAbbrevDisplayName());
            System.out.println("Description: " + term.getDescription());
            System.out.println("Semantic type: " + term.getSemanticType());
        }
    }

    public static void main(String[] args) {
        PrintTerm pt = new PrintTerm();
        pt.processOptionsAndArgs(args);
        pt.initializeExecuteAndClose();
    }
}
