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
