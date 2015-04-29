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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.*;
import org.protempa.proposition.Proposition;

/**
 * Groups (multiplexes) multiple {@link DataStreamingEventIterator}s by key id.
 * This implementation provides resource management for
 * {@link ReferenceIterator}s and the database connection.
 *
 * @author Andrew Post
 */
public class RelationalDbDataReadIterator
        extends DataSourceBackendMultiplexingDataStreamingEventIterator {

    private final List<Connection> connections;
    private final DataStager stager;

    RelationalDbDataReadIterator(
            List<? extends DataStreamingEventIterator<UniqueIdPair>> refs,
            List<? extends DataStreamingEventIterator<Proposition>> itrs,
            List<Connection> connections, DataStager stager) {
        super(itrs, refs);
        this.connections = connections;
        this.stager = stager;
    }

    @Override
    public void close() throws DataSourceReadException {
        try {
            super.close();
        } finally {
            for (Connection connection : this.connections) {
                try {

                    if (this.stager != null) {
                        cleanupStagingArea(this.stager);
                    }

                    connection.close();
                    connection = null;
                } catch (SQLException sqle) {
                    throw new DataSourceReadException("Error retrieving data", sqle);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException ignore) {
                        }
                    }
                }
            }
        }
    }

    private void cleanupStagingArea(DataStager stager)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        logger.log(Level.INFO, "Cleaning up staged data");
        try {
            stager.cleanup();
        } catch (SQLException ex) {
            throw new DataSourceReadException(
                    "Failed to clean up the staging area", ex);
        }
    }
}
