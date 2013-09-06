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
import org.protempa.proposition.UniqueId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public final class ReferenceResultSetIterator
        implements DataStreamingEventIterator<UniqueIdPair> {

    private static final Logger LOGGER = Logger.getLogger
            (ReferenceResultSetIterator.class.getName());

    private ResultSet resultSet;
    private final Logger logger;
    private final String[] uniqueIds;
    private final String[] refUniqueIds;
    private final EntitySpec entitySpec;
    private int count;
    private List<UniqueIdPair> props;
    private DataStreamingEvent<UniqueIdPair> dataStreamingEvent;
    private ReferenceSpec referenceSpec;
    private UniqueId uniqueIdentifier;
    private String[] uniqueIdsCopy;
    private StreamingRefResultProcessor processor;
    private String keyId;
    private String lastDelivered;
    private Statement statement;
    private boolean end;

    ReferenceResultSetIterator(ResultSet resultSet,
            ReferenceSpec referenceSpec, EntitySpec entitySpec,
            String dataSourceBackendId,
            StreamingRefResultProcessor<?> processor) throws SQLException {
        assert resultSet != null : "resultSet cannot be null";
        assert entitySpec != null : "entitySpec cannot be null";
        assert referenceSpec != null : "referenceSpec cannot be null";
        assert dataSourceBackendId != null : "dataSourceBackendId cannot be null";
        this.resultSet = resultSet;
        this.logger = SQLGenUtil.logger();
        this.uniqueIds =
                new String[entitySpec.getUniqueIdSpecs().length];
        this.entitySpec = entitySpec;
        this.referenceSpec = referenceSpec;
        this.refUniqueIds =
                new String[referenceSpec.getUniqueIdSpecs().length];
        this.uniqueIdsCopy = new String[uniqueIds.length];
        this.processor = processor;
        this.props = new ArrayList<UniqueIdPair>();
        this.statement = processor.getStatement();
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        return this.dataStreamingEvent != null || advance() != null;
    }

    @Override
    public DataStreamingEvent<UniqueIdPair> next() 
            throws DataSourceReadException {
        DataStreamingEvent<UniqueIdPair> result;
        if (this.dataStreamingEvent != null) {
            result = this.dataStreamingEvent;
        } else {
            result = advance();
        }
        if (result == null) {
            throw new NoSuchElementException();
        }
        this.dataStreamingEvent = null;

        LOGGER.log(Level.INFO, "{0}: Current: {1}, Last Delivered: {2}",
                new Object[]{this.entitySpec.getName(), result.getKeyId(),
                        this.lastDelivered});
        this.lastDelivered = result.getKeyId();

        return result;
    }

    @Override
    public void close() throws DataSourceReadException {
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

    private DataStreamingEvent<UniqueIdPair> advance()
            throws DataSourceReadException {
        if (this.end) {
            return null;
        }
        boolean normalExit = true;
        try {
            try {
                while (this.dataStreamingEvent == null) {
                    if (!this.resultSet.next()) {
                        this.end = true;
                        break;
                    }
                    int i = 1;

                    String kId = resultSet.getString(i++);
                    if (kId == null) {
                        logger.warning("A keyId is null. Skipping record.");
                        continue;
                    }
                    if (keyId != null && !keyId.equals(kId)) {
                        createDataStreamingEvent(props);
                    }
                    keyId = kId;

                    i = this.processor.readUniqueIds(uniqueIds, resultSet, i);
                    if (org.arp.javautil.arrays.Arrays.contains(uniqueIds, null)) {
                        /**
                         * We skip records with null values in the unique id
                         * field, and fail if appropriate in
                         * Event/Constant/PrimitiveParameterResultProcessor. We
                         * need to do this because it is okay to include a
                         * potentially null column as part of the unique id when
                         * the column is the keyid.
                         */
                        continue;
                    }
                    if (uniqueIdentifier == null
                            || !Arrays.equals(uniqueIds, uniqueIdsCopy)) {
                        uniqueIdentifier = this.processor.generateUniqueId(
                                this.entitySpec.getName(), uniqueIds);
                        System.arraycopy(uniqueIds, 0, uniqueIdsCopy, 0,
                                uniqueIds.length);
                    }

                    i = this.processor.readUniqueIds(refUniqueIds, resultSet, i);
                    if (org.arp.javautil.arrays.Arrays.contains(refUniqueIds, null)) {
                        /**
                         * We skip records with null values in the unique id
                         * field, and fail if appropriate in
                         * Event/Constant/PrimitiveParameterResultProcessor. We
                         * need to do this because it is okay to include a
                         * potentially null column as part of the unique id when
                         * the column is the keyid.
                         */
                        continue;
                    }
                    UniqueId refUniqueIdentifier = this.processor.generateUniqueId(
                            this.referenceSpec.getEntityName(), refUniqueIds);
                    this.props.add(new UniqueIdPair(
                            this.referenceSpec.getReferenceName(),
                            uniqueIdentifier, refUniqueIdentifier));
                    this.count++;
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
                throw new DataSourceReadException("Error during streaming", ex);
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
                } catch (SQLException ex) {
                }
            }
        }
        if (this.dataStreamingEvent == null) {
            if (logger.isLoggable(Level.FINE)) {
                Logging.logCount(logger, Level.FINE, count,
                        "Retrieved {0} reference total",
                        "Retrieved {0} references total");
            }
        }
        assert this.dataStreamingEvent == null : "dataStreamingEvent must be null";
        if (this.keyId != null) {
            createDataStreamingEvent(this.props);
        }
        return this.dataStreamingEvent;
    }

    private void createDataStreamingEvent(List<UniqueIdPair> uniqueIds) {
        this.dataStreamingEvent = new DataStreamingEvent<UniqueIdPair>(this.keyId, uniqueIds);
        this.props = new ArrayList<UniqueIdPair>();
    }
}
