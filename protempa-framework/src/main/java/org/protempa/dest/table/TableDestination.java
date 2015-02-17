package org.protempa.dest.table;

import java.io.BufferedWriter;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.dest.AbstractDestination;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.query.QueryMode;
import org.protempa.query.Query;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

/**
 *
 * @author Andrew Post
 */
public final class TableDestination extends AbstractDestination {
    private final boolean inferPropositionIdsNeeded;
    private final boolean headerWritten;
    private final TableColumnSpec[] columnSpecs;
    private final String[] rowPropositionIds;
    private final char columnDelimiter;
    private final BufferedWriter out;

    public TableDestination(BufferedWriter out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        this(out, columnDelimiter, rowPropositionIds, columnSpecs,
                headerWritten, true);
    }
    
    public TableDestination(BufferedWriter out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, boolean inferPropositionIdsNeeded) {
        this.out = out;
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.inferPropositionIdsNeeded = inferPropositionIdsNeeded;
    }
    
    public String[] getRowPropositionIds() {
        return this.rowPropositionIds.clone();
    }

    public char getColumnDelimiter() {
        return this.columnDelimiter;
    }

    public TableColumnSpec[] getColumnSpecs() {
        return this.columnSpecs.clone();
    }

    public boolean isHeaderWritten() {
        return this.headerWritten;
    }
    
    @Override
    public TableQueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        if (query.getQueryMode() == QueryMode.UPDATE) {
            throw new QueryResultsHandlerInitException("Update mode not supported");
        }
        return new TableQueryResultsHandler(this.out, this.columnDelimiter, 
                this.rowPropositionIds, this.columnSpecs, this.headerWritten, 
                this.inferPropositionIdsNeeded, knowledgeSource);
    }

}
