/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides an interface for getting a connection to a relational database.
 *
 * @author Andrew Post
 */
public interface ConnectionSpec {
    /**
     * Returns a connection to a SQL database that may be new or part of a
     * connection pool.
     *
     * @return a {@link Connection}.
     * @throws SQLException if an error occurs getting the connection.
     */
    Connection getOrCreate() throws SQLException;

    /**
     * Performs some processing on this connection spec.
     *
     * @param visitor
     *            a {@link ConnectionSpecVisitor}.
     */
    void accept(ConnectionSpecVisitor connectionSpecVisitor);
}
