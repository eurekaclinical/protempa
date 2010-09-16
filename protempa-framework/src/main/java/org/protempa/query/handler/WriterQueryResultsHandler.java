package org.protempa.query.handler;

import org.protempa.Derivations;
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
import org.protempa.proposition.Proposition;

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
     * Closes the output stream.
     * 
     * @throws FinderException if an error occurred closing the output stream.
     */
    @Override
    public void finish() throws FinderException {
        try {
            super.close();
        } catch (IOException ioe) {
            throw new FinderException(ioe);
        }
    }

    /**
     * No-op.
     *
     * @throws FinderException should never throw.
     */
    @Override
    public void init() throws FinderException {
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
            List<Derivations> derivationsList)
            throws FinderException;
}
