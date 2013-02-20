/*
 * #%L
 * Protempa Relational Database Data Source Backend
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
package org.protempa.backend.dsb.relationaldb;

import org.protempa.DataStreamingEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.log.Logging;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEventIterator;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;

/**
 * Base implementation for iterators that read a result set that is ordered by
 * key id.
 *
 * @author Andrew Post
 */
abstract class PropositionResultSetIterator<P extends Proposition>
        implements DataStreamingEventIterator<P> {

    private ResultSet resultSet;
    private final Logger logger;
    private final String[] uniqueIds;
    private final EntitySpec entitySpec;
    private final int[] columnTypes;
    private Statement statement;
    private int count;
    private ColumnSpec codeSpec;
    private String[] propIds;
    private PropertySpec[] propertySpecs;
    private Value[] propertyValues;
    private List<P> props;
    private DataStreamingEvent<P> dataStreamingEvent;
    private String keyId;
    private boolean end;

    PropositionResultSetIterator(Statement statement, ResultSet resultSet,
            EntitySpec entitySpec, String dataSourceBackendId)
            throws SQLException {
        assert resultSet != null : "resultSet cannot be null";
        assert entitySpec != null : "entitySpec cannot be null";
        assert dataSourceBackendId != null : "dataSourceBackendId cannot be null";
        this.resultSet = resultSet;
        this.logger = SQLGenUtil.logger();
        this.uniqueIds =
                new String[entitySpec.getUniqueIdSpecs().length];
        this.entitySpec = entitySpec;
        this.propIds = entitySpec.getPropositionIds();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        this.columnTypes = new int[resultSetMetaData.getColumnCount()];
        for (int i = 0; i < this.columnTypes.length; i++) {
            this.columnTypes[i] = resultSetMetaData.getColumnType(i + 1);
        }
        ColumnSpec localCodeSpec = entitySpec.getCodeSpec();
        if (localCodeSpec != null) {
            List<ColumnSpec> codeSpecL = localCodeSpec.asList();
            localCodeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        this.codeSpec = localCodeSpec;
        this.propertySpecs = entitySpec.getPropertySpecs();
        this.propertyValues = new Value[this.propertySpecs.length];
        this.props = new ArrayList<P>();
        this.statement = statement;
    }

    final String getKeyId() {
        return this.keyId;
    }

    /**
     * For recording the key id of the current record.
     *
     * @param kId the key id {@link String}.
     * @param props that key id's propositions.
     */
    final void handleKeyId(String kId) {
        String oldKeyId = getKeyId();
        if (oldKeyId != null && !oldKeyId.equals(kId)) {
            createDataStreamingEvent(oldKeyId, this.props);
        }
        this.keyId = kId;

    }

    void handleProposition(P prop) {
        this.props.add(prop);
    }

    /**
     * Reads the next record from the result set and creates a
     * {@link Proposition}. Implementations must call {@link #handleKeyId()}
     * with the current key id, and they must call {@link #handleProposition}
     * with the proposition.
     *
     * @param resultSet
     * @param props
     * @param uniqueIds
     * @param codeSpec
     * @param entitySpec
     * @param columnTypes
     * @param propIds
     * @param propertySpecs
     * @param propertyValues
     * @throws SQLException
     */
    abstract void doProcess(ResultSet resultSet,
            String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
            int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
            Value[] propertyValues) throws SQLException;

    final void createDataStreamingEvent(String key, List<P> propositions) {
        this.dataStreamingEvent = new DataStreamingEvent<P>(key, propositions);
        this.props = new ArrayList<P>();
    }
    
    @Override
    public boolean hasNext() {
        return this.dataStreamingEvent != null || advance() != null;
    }

    @Override
    public final DataStreamingEvent<P> next() {
        DataStreamingEvent<P> result;
        if (this.dataStreamingEvent != null) {
            result = this.dataStreamingEvent;
        } else {
            result = advance();
        }
        if (result == null) {
            throw new NoSuchElementException();
        }
        this.dataStreamingEvent = null;
        return result;
    }

    private DataStreamingEvent<P> advance() throws StreamingSQLException {
        if (this.end) {
            return null;
        }
        boolean normalExit = true;
        if (this.resultSet != null) {
            try {
                try {
                    /*
                     * If this.dataStreamingEvent == null then skip the current
                     * record.
                     */
                    while (this.dataStreamingEvent == null) {
                        if (this.resultSet.next()) {
                            doProcess(this.resultSet, this.uniqueIds,
                                this.codeSpec, this.entitySpec,
                                this.columnTypes, this.propIds,
                                this.propertySpecs, this.propertyValues);
                            this.count++;
                        } else {
                            this.end = true;
                            break;
                        }
                    }

                    if (this.dataStreamingEvent != null) {
                        return this.dataStreamingEvent;
                    }
                    if (this.end) {
                        this.resultSet.close();
                        this.resultSet = null;
                        this.statement.close();
                        this.statement = null;
                    }
                } catch (SQLException ex) {
                    normalExit = false;
                    throw new StreamingSQLException("Error during streaming",
                            ex);
                } finally {
                    if (!normalExit && this.resultSet != null) {
                        try {
                            this.resultSet.close();
                        } catch (SQLException ignore) {
                        } finally {
                            this.resultSet = null;
                        }
                    }
                }
            } finally {
                if (!normalExit && this.statement != null) {
                    try {
                        this.statement.close();
                    } catch (SQLException ignore) {
                    }
                }
            }
        }
        if (this.resultSet == null) {
            if (logger.isLoggable(Level.FINE)) {
                Logging.logCount(logger, Level.FINE, count,
                        "Retrieved {0} record total",
                        "Retrieved {0} records total");
            }
        }

        assert this.dataStreamingEvent == null :
                "dataStreamingEvent should be null";
        if (this.keyId != null) {
            createDataStreamingEvent(this.keyId, this.props);
        }

        return this.dataStreamingEvent;
    }

    @Override
    public final void close() throws DataSourceReadException {
        if (this.resultSet != null) {
            try {
                this.resultSet.close();
                this.resultSet = null;
                if (this.statement != null) {
                    try {
                        this.statement.close();
                        this.statement = null;
                    } catch (SQLException ex) {
                        throw new DataSourceReadException("Error closing statement", ex);
                    }
                }
            } catch (SQLException ex) {
                throw new DataSourceReadException("Error closing result set", ex);
            } finally {
                if (this.statement != null) {
                    try {
                        this.statement.close();
                    } catch (SQLException ignore) {
                    }
                }
            }
        }
    }
}
