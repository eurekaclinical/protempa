package org.protempa.cli;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;

/**
 *
 * @author Andrew Post
 */
public class PrintInverseIsA extends CLI {
    public PrintInverseIsA() {
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
                List<PropositionDefinition> result =
                        knowledgeSource.readInverseIsA(propDef);
                PropositionDefinitionPrinter printer =
                    new PropositionDefinitionPrinter();
                printer.visit(result);
            } catch (KnowledgeSourceReadException ex) {
                throw new CLIException("Error getting leaves", ex);
            }
        }
    }

    public static void main(String[] args) {
        PrintInverseIsA pl = new PrintInverseIsA();
        pl.processOptionsAndArgs(args);
        pl.initializeExecuteAndClose();
    }
}
