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
    private Logger logger = SQLGenUtil.logger();

    RelationalDbDataReadIterator(
            List<? extends DataStreamingEventIterator<UniqueIdPair>> refs,
            List<? extends DataStreamingEventIterator<Proposition>> itrs,
            List<Connection> connections) {
        super(itrs, refs);
        this.connections = connections;
        logger.log(Level.FINEST,
                "Starting with connections List of size: {0}", (this.connections == null? 0:this.connections.size()));
    }

    @Override
    public void close() throws DataSourceReadException {
    	logger.log(Level.FINEST,
                "Trying to close connection");
        try {
            super.close();
            logger.log(Level.FINEST,
                    "Closed SUPER");
        } finally {
        	logger.log(Level.FINEST,
                    "Checking connections in List of size: {0}", (this.connections == null? 0:this.connections.size()));
            for (Connection connection : this.connections) {
                if (connection != null) {
                    try {
                        connection.close();
                        connection = null;
                        logger.log(Level.FINEST,
                                "Closed connection");
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
                logger.log(Level.FINEST,
                        "No Open connections");
            }
        }
    }

}
