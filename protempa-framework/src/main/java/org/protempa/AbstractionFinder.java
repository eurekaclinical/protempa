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
package org.protempa;

import java.util.List;
import org.protempa.dest.Destination;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.QueryBuilder;

/**
 * Class that actually does the abstraction finding.
 *
 * @author Andrew Post
 */
final class AbstractionFinder {

    private final DataSource dataSource;
    private final KnowledgeSource knowledgeSource;
    private final AlgorithmSource algorithmSource;
    private boolean closed;
    private Executor executor;
    private final List<? extends ProtempaEventListener> eventListeners;

    AbstractionFinder(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource,
            List<? extends ProtempaEventListener> eventListeners)
            throws KnowledgeSourceReadException {
        assert dataSource != null : "dataSource cannot be null";
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        assert algorithmSource != null : "algorithmSource cannot be null";
        assert eventListeners != null : "eventListeners cannot be null";

        this.dataSource = dataSource;
        this.knowledgeSource = knowledgeSource;
        this.algorithmSource = algorithmSource;
        
        this.eventListeners = eventListeners;
        
        this.dataSource.setEventListeners(eventListeners);
        this.knowledgeSource.setEventListeners(eventListeners);
        this.algorithmSource.setEventListeners(eventListeners);

        this.dataSource.addSourceListener(
                new SourceListener<DataSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(DataSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

        this.knowledgeSource.addSourceListener(
                new SourceListener<KnowledgeSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(KnowledgeSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

        this.algorithmSource.addSourceListener(
                new SourceListener<AlgorithmSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(AlgorithmSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

    }

    List<? extends ProtempaEventListener> getEventListeners() {
        return eventListeners;
    }
    
    DataSource getDataSource() {
        return this.dataSource;
    }

    KnowledgeSource getKnowledgeSource() {
        return this.knowledgeSource;
    }

    AlgorithmSource getAlgorithmSource() {
        return this.algorithmSource;
    }

    void doFind(Query query, Destination destination)
            throws QueryException {
        assert destination != null : "destination cannot be null";
        try {
            this.executor = new Executor(query, destination, this);
            this.executor.init();
            this.executor.execute();
            this.executor.close();
            this.executor = null;
        } catch (CloseException ex) {
            this.executor = null; //Don't try closing it again below
            throw new QueryException(query.getName(), ex);
        } finally {
            if (this.executor != null) {
                try {
                    this.executor.close();
                } catch (CloseException ignored) {
                }
            }
        }
    }

    void cancel() {
        if (this.executor != null) {
            this.executor.cancel();
        }
    }

    Query buildQuery(QueryBuilder queryBuilder) throws QueryBuildException {
        return queryBuilder.build(this.knowledgeSource, this.algorithmSource);
    }

    void close() throws CloseException {
        CloseException exception = null;

        try {
            this.algorithmSource.close();
        } catch (SourceCloseException ex) {
            exception = new CloseException(ex);
        }
        try {
            this.knowledgeSource.close();
        } catch (SourceCloseException ex) {
            if (exception == null) {
                exception = new CloseException(ex);
            } else {
                exception.addSuppressed(ex);
            }
        }
        try {
            this.dataSource.close();
        } catch (SourceCloseException ex) {
            if (exception == null) {
                exception = new CloseException(ex);
            } else {
                exception.addSuppressed(ex);
            }
        }
        if (exception != null) {
            throw exception;
        }
        this.closed = true;
    }

    boolean isClosed() {
        return this.closed;
    }

}
