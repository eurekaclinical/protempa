package org.protempa.query.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.protempa.QueryResults;

/**
 *
 * @author Andrew Post
 */
public abstract class QueryResultsWriter extends BufferedWriter {
    private final QueryResults queryResults;

    public QueryResultsWriter(QueryResults queryResults, File file)
            throws IOException {
        super(new FileWriter(file));
        if (queryResults == null)
            throw new IllegalArgumentException("queryResults may not be null");
        this.queryResults = queryResults;
    }

    public QueryResultsWriter(QueryResults queryResults, String fileName)
            throws IOException {
        super(new FileWriter(fileName));
        if (queryResults == null)
            throw new IllegalArgumentException("queryResults may not be null");
        this.queryResults = queryResults;
    }

    public QueryResultsWriter(QueryResults queryResults, OutputStream out) {
        super(new OutputStreamWriter(out));
        if (queryResults == null)
            throw new IllegalArgumentException("queryResults may not be null");
        this.queryResults = queryResults;
    }

    public QueryResultsWriter(QueryResults queryResults, Writer out) {
        super(out);
        if (queryResults == null)
            throw new IllegalArgumentException("queryResults may not be null");
        this.queryResults = queryResults;
    }

    protected QueryResults getQueryResults() {
        return this.queryResults;
    }

    public abstract void writeAll() throws IOException;

}
