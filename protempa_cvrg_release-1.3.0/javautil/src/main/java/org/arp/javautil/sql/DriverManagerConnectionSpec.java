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
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements getting a connection to a relational database using Java's
 * {@link DriverManager} API.
 * 
 * @author Andrew Post
 */
public class DriverManagerConnectionSpec implements ConnectionSpec {
    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates an instance with a specified JDBC URL, and a username and
     * password.
     * 
     * @param url a JDBC URL {@link String}.
     * @param user a username {@link String}.
     * @param password a password {@link String}.
     */
    public DriverManagerConnectionSpec(String url, String user, 
            String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Creates a database connection or gets an existing connection with
     * the JDBC URL, username and password specified in the constructor.
     *
     * @return a {@link Connection}.
     *
     * @throws SQLException if an error occurred creating/getting a
     * {@link Connection}, possibly because the JDBC URL, username and/or
     * password are invalid.
     */
    @Override
    public Connection getOrCreate() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    /**
     * Performs some processing on this connection spec.
     *
     * @param connectionSpecVisitor a {@link ConnectionSpecVisitor}.
     */
    @Override
    public void accept(ConnectionSpecVisitor connectionSpecVisitor) {
        connectionSpecVisitor.visit(this);
    }

}
