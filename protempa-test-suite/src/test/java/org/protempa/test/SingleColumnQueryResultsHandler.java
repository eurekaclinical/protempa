package org.protempa.test;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;

final class SingleColumnQueryResultsHandler implements QueryResultsHandler {

    private final Writer writer;

    /**
     * Creates a new instance that will write to the given writer. It is the
     * responsiblity of the caller to open and close the writer.
     * 
     * @param writer
     *            the {@link Writer} to output to
     */
    public SingleColumnQueryResultsHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {

    }

    @Override
    public void finish() throws FinderException {

    }

    private void writeLine(String str) throws IOException {
        this.writer.write(str);
        this.writer.write("\n");
    }

    private void printDerivations(
            Map<Proposition, List<Proposition>> derivations) throws IOException {
        for (Entry<Proposition, List<Proposition>> pp : derivations.entrySet()) {
            writeLine(pp.getKey().getId());
            for (Proposition p : pp.getValue()) {
                writeLine(p.getId());
            }
        }
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references) throws FinderException {
        try {
            writeLine(keyId);
            for (Proposition p : propositions) {
                writeLine(p.getId());
            }
            printDerivations(forwardDerivations);
            printDerivations(backwardDerivations);
        } catch (IOException ex) {
            throw new FinderException(ex);
        }
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException {

    }
}
