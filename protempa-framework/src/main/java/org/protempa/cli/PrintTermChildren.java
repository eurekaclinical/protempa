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

import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.protempa.Protempa;
import org.protempa.Term;
import org.protempa.TermSource;
import org.protempa.TermSourceReadException;

/**
 * Prints a term's children.
 * 
 * @author Andrew Post
 */
public class PrintTermChildren extends CLI {

    public PrintTermChildren() {
        super(System.getProperty("app.name"),
                new Argument[]{new Argument("termId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine) throws CLIException {
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
            String[] childIds = term.getDirectChildren();
            Arrays.sort(childIds);
            if (childIds.length == 0) {
                System.out.println("No term with id " + termId);
            } else {
                if (childIds.length == 1) {
                    System.out.println(termId + " has 1 child:");
                } else {
                    System.out.println(termId + " has " + childIds.length
                            + " children:");
                }
                for (String childId : childIds) {
                    System.out.println(childId);
                }
            }
        }
    }

    public static void main(String[] args) {
        PrintTermChildren ptc = new PrintTermChildren();
        ptc.processOptionsAndArgs(args);
        ptc.initializeExecuteAndClose();
    }
}