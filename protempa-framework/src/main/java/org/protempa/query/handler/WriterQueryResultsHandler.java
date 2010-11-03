package org.protempa.query.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 * An implementation of QueryResultsHandler that provides base functionality
 * for writing each result to some output stream.
 * 
 * @author Michel Mansour
 *
 */
public abstract class WriterQueryResultsHandler extends BufferedWriter
        implements QueryResultsHandler {

    /**
     * Instantiates this handler to write to a file.
     *
     * @param file a {@link File}.
     * @throws IOException if an error occurred creating an output stream to
     * the file.
     */
    public WriterQueryResultsHandler(File file)
            throws IOException {
        super(new FileWriter(file));

    }

    /**
     * Instantiates this handler to write to a file.
     * @param fileName the system dependent file name.
     *
     * @throws IOException if an error occurred creating an output stream to
     * the file.
     */
    public WriterQueryResultsHandler(String fileName)
            throws IOException {
        super(new FileWriter(fileName));
    }

    /**
     * Instantiates this handler to write to an output stream.
     * 
     * @param out an {@link OutputStream}.
     */
    public WriterQueryResultsHandler(OutputStream out) {
        super(new OutputStreamWriter(out));
    }

    /**
     * Intantiates this handler to write to a character stream.
     * 
     * @param out a {@link Writer}.
     */
    public WriterQueryResultsHandler(Writer out) {
        super(out);
    }

    /**
     * Cleans up any resources other than closing the output stream. Call
     * {@link #close()} to close the output stream if desired.
     *
     * The default implementation is a no-op.
     * 
     * @throws FinderException if an error occurred.
     */
    @Override
    public void finish() throws FinderException {
        
    }

    /**
     * No-op.
     *
     * @throws FinderException should never throw.
     */
    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
    }

    /**
     * Writes the key ids and propositions to an output stream.
     *
     * @param key a key id {@link String}.
     * @param proposition a {@link List<Proposition>} of propositions.
     * @param derivations a mapping from propositions to derived abstractions.
     * @throws FinderException if an error occurred writing to the output
     * stream.
     */
    @Override
    public abstract void handleQueryResult(String key,
            List<Proposition> propositions,
            Map<Proposition,List<Proposition>> derivations,
            Map<UniqueIdentifier,Proposition> references)
            throws FinderException;
}
