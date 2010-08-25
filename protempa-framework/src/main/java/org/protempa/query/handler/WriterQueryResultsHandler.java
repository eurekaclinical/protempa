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

    public WriterQueryResultsHandler(File file)
            throws IOException {
        super(new FileWriter(file));

    }

    public WriterQueryResultsHandler(String fileName)
            throws IOException {
        super(new FileWriter(fileName));
    }

    public WriterQueryResultsHandler(OutputStream out) {
        super(new OutputStreamWriter(out));
    }

    public WriterQueryResultsHandler(Writer out) {
        super(out);
    }

    /* (non-Javadoc)
     * @see org.protempa.query.handler.QueryResultsHandler#finish()
     */
    @Override
    public void finish() throws FinderException {
        try {
            super.close();
        } catch (IOException ioe) {
            throw new FinderException(ioe);
        }
    }

    /* (non-Javadoc)
     * @see org.protempa.query.handler.QueryResultsHandler#init()
     */
    @Override
    public void init() throws FinderException {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.protempa.query.handler.QueryResultHandler#handleQueryResult(java.lang.String, java.util.List)
     */
    @Override
    public abstract void handleQueryResult(String key,
            List<Proposition> propositions) throws FinderException;
}
