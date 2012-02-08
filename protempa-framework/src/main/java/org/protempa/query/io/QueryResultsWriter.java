/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
