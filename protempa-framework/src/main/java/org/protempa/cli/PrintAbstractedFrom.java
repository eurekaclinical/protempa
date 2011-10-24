package org.protempa.cli;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.protempa.AbstractionDefinition;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;

/**
 *
 * @author Andrew Post
 */
public class PrintAbstractedFrom extends CLI {
    public PrintAbstractedFrom() {
        super(System.getProperty("app.name"),
                new Argument[] {new Argument("propositionId", true)});
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        String propositionId = commandLine.getArgs()[0];
        KnowledgeSource knowledgeSource = protempa.getKnowledgeSource();
        AbstractionDefinition propDef;
        try {
            propDef = knowledgeSource.readAbstractionDefinition(propositionId);
        } catch (KnowledgeSourceReadException ex) {
            throw new CLIException("Error reading proposition definition", ex);
        }
        if (propDef == null) {
            System.out.println("No abstraction definition with id " +
                    propositionId);
        } else {
            try {
                List<PropositionDefinition> result =
                        knowledgeSource.readAbstractedFrom(propDef);
                PropositionDefinitionPrinter printer =
                    new PropositionDefinitionPrinter();
                printer.visit(result);
            } catch (KnowledgeSourceReadException ex) {
                throw new CLIException("Error getting leaves", ex);
            }
        }
    }

    public static void main(String[] args) {
        PrintAbstractedFrom pl = new PrintAbstractedFrom();
        pl.processOptionsAndArgs(args);
        pl.initializeExecuteAndClose();
    }
}
