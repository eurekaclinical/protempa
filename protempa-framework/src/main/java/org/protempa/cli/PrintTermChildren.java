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
