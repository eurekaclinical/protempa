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
        super(System.getProperty("app.name"),
                new Argument[]{new Argument("termId", true)});
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
