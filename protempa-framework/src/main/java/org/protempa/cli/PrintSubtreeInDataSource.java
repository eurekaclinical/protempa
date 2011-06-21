package org.protempa.cli;

import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;

/**
 *
 * @author Andrew Post
 */
public class PrintSubtreeInDataSource extends CLI {
    public PrintSubtreeInDataSource() {
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
            try {
                Set<PropositionDefinition> result = 
                        knowledgeSource.inDataSourcePropositionDefinitions(
                        propDef.getId());
                PropositionDefinitionPrinter printer =
                    new PropositionDefinitionPrinter();
                printer.visit(result);
            } catch (KnowledgeSourceReadException ex) {
                throw new CLIException("Error getting the subtree's proposition definitions that are marked as being in a data source", ex);
            }
        }
    }

    public static void main(String[] args) {
        PrintSubtreeInDataSource pl = new PrintSubtreeInDataSource();
        pl.processOptionsAndArgs(args);
        pl.initializeExecuteAndClose();
    }
}
