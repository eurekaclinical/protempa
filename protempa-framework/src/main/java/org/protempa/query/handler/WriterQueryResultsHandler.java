package org.protempa.query.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.protempa.FinderException;
import org.protempa.QuerySession;
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
    private QuerySession querySession;

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
     * Stores the query session.
     *
     * @param a {@link QuerySession}.
     * @throws FinderException should never throw.
     */
    @Override
    public void init(QuerySession querySession) throws FinderException {
        this.querySession = querySession;
    }

    /**
     * Gets the {@link QuerySession}.
     *
     * @return a {@link QuerySession}.
     */
    protected QuerySession getQuerySession() {
        return this.querySession;
    }

    /**
     * Writes the key ids and propositions to an output stream.
     *
     * @param key a key id {@link String}.
     * @param proposition a {@link List<Proposition>} of propositions.
     * @throws FinderException if an error occurred writing to the output
     * stream.
     */
    @Override
    public abstract void handleQueryResult(String key,
            List<Proposition> propositions) throws FinderException;
}
