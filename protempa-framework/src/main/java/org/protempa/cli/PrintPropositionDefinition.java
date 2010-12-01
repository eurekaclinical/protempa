package org.protempa.cli;

import org.apache.commons.cli.CommandLine;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;

/**
 * Prints a specified proposition definition to the console.
 * 
 * @author Andrew Post
 */
public class PrintPropositionDefinition extends CLI {

    public PrintPropositionDefinition() {
        super(System.getProperty("app.name"),
                new Argument[] {new Argument("propositionId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        String propositionId = commandLine.getArgs()[0];
        KnowledgeSource knowledgeSource = protempa.getKnowledgeSource();
        PropositionDefinition propDef;
        try {
            propDef = knowledgeSource.readPropositionDefinition(propositionId);
        } catch (KnowledgeSourceReadException ex) {
            throw new CLIException("Error reading proposition definition", ex);
        }
        if (propDef == null) {
            System.out.println("No proposition definition with id " + 
                    propositionId);
        } else {
            PropositionDefinitionPrinter printer =
                    new PropositionDefinitionPrinter();
            propDef.accept(printer);
        }
    }

    public static void main(String[] args) {
        PrintPropositionDefinition ppd =
                new PrintPropositionDefinition();
        ppd.processOptionsAndArgs(args);
        ppd.initializeExecuteAndClose();
    }

}
