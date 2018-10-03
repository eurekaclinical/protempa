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

import org.arp.javautil.log.Logging;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.ProtempaEvent;

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
    private final ColumnSpec codeSpec;
    private final String[] propIds;
    private final PropertySpec[] propertySpecs;
    private final Value[] propertyValues;
    private final Map<String, ReferenceSpec> inboundRefSpecs;
    private final UniqueIdPair[] refUniqueIds;
    private final Map<String, ReferenceSpec> bidirectionalRefSpecs;
    private Map<UniqueId, P> props;
    private DataStreamingEvent<P> dataStreamingEvent;
    private String keyId;
    private boolean end;
    private final InboundReferenceResultSetIterator referenceIterator;

    private boolean advanceInvoked = false;
    private final RelationalDbDataSourceBackend backend;

    PropositionResultSetIterator(RelationalDbDataSourceBackend backend,
            Statement statement, ResultSet resultSet,
            EntitySpec entitySpec, Map<String, ReferenceSpec> inboundRefSpecs,
            Map<String, ReferenceSpec> bidirectionalRefSpecs,
            String dataSourceBackendId, InboundReferenceResultSetIterator referenceIterator)
            throws SQLException {
        assert backend != null : "backend cannot be null";
        assert resultSet != null : "resultSet cannot be null";
        assert entitySpec != null : "entitySpec cannot be null";
        assert dataSourceBackendId != null : "dataSourceBackendId cannot be null";
        this.backend = backend;
        this.resultSet = resultSet;
        this.logger = SQLGenUtil.logger();
        this.uniqueIds
                = new String[entitySpec.getUniqueIdSpecs().length];
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
        this.referenceIterator = referenceIterator;
        this.inboundRefSpecs = inboundRefSpecs;
        this.bidirectionalRefSpecs = bidirectionalRefSpecs;
        this.refUniqueIds = new UniqueIdPair[this.inboundRefSpecs.size() + this.bidirectionalRefSpecs.size()];
        this.props = new HashMap<>();
        this.statement = statement;
    }

    final String getKeyId() {
        return this.keyId;
    }

    final InboundReferenceResultSetIterator getReferenceIterator() {
        return this.referenceIterator;
    }

    /**
     * For recording the key id of the current record.
     *
     * @param kId the key id {@link String}.
     */
    final void handleKeyId(String kId) {
        String oldKeyId = getKeyId();
        if (oldKeyId != null && !oldKeyId.equals(kId)) {
            createDataStreamingEvent(oldKeyId, this.props);
        }
        this.keyId = kId;
    }

    abstract void fireResultSetCompleted();

    void handleProposition(P prop) {
        if (!this.props.containsKey(prop.getUniqueId())) {
            this.props.put(prop.getUniqueId(), prop);
        }
    }

    /**
     * Reads the next record from the result set and creates a
     * {@link Proposition}. Implementations must call {@link #handleKeyId} with
     * the current key id, and they must call {@link #handleProposition} with
     * the proposition.
     *
     * @param resultSet
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
            Map<String, ReferenceSpec> bidirectionalRefSpecs,
            int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
            Value[] propertyValues, UniqueIdPair[] refUniqueIds) throws
            SQLException;

    private void createDataStreamingEvent(String key, Map<UniqueId, P> propositions) {
        List<P> uniqueProps = new ArrayList<>(propositions.values());
        this.dataStreamingEvent = new DataStreamingEvent<>(key, uniqueProps);
        this.props = new HashMap<>();
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
        if (!this.advanceInvoked) {
            this.advanceInvoked = true;
            logger.log(Level.INFO, "First invocation of advance() for {0} proposition iterator <{1}>", new Object[]{this.entitySpec.getName(), this.hashCode()});
        }

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
                                    this.bidirectionalRefSpecs,
                                    this.columnTypes, this.propIds,
                                    this.propertySpecs, this.propertyValues,
                                    this.refUniqueIds);
                            this.count++;
                        } else {
                            logger.log(Level.INFO, "Result set complete for {0} proposition iterator", this.entitySpec.getName());
                            this.end = true;
                            fireResultSetCompleted();
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
                    backend.fireProtempaEvent(
                        new ProtempaEvent(
                            ProtempaEvent.Level.INFO,
                            ProtempaEvent.Type.DSB_QUERY_STOP,
                            backend.getClass(),
                            new Date(),
                            this.entitySpec.getName()));
                    backend.fireProtempaEvent(
                        new ProtempaEvent(
                            ProtempaEvent.Level.INFO,
                            ProtempaEvent.Type.DSB_QUERY_RESULT,
                            backend.getClass(),
                            new Date(),
                            this.entitySpec.getName() + ": ERROR (" + ex.getMessage() + ")"));
                    throw new StreamingSQLException(
                            "Error during streaming entity "
                            + this.entitySpec.getName(),
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
            backend.fireProtempaEvent(
                    new ProtempaEvent(
                            ProtempaEvent.Level.INFO,
                            ProtempaEvent.Type.DSB_QUERY_STOP,
                            backend.getClass(),
                            new Date(),
                            this.entitySpec.getName()));
            backend.fireProtempaEvent(
                    new ProtempaEvent(
                            ProtempaEvent.Level.INFO,
                            ProtempaEvent.Type.DSB_QUERY_RESULT,
                            backend.getClass(),
                            new Date(),
                            this.entitySpec.getName() + ": " + count + " record(s) total"));
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
