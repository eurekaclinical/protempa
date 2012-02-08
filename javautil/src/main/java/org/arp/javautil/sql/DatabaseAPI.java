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

import javax.naming.NamingException;

/**
 * An enum of Java's APIs (({@link java.sql.DriverManager} and
 * {@link javax.sql.DataSource}) for connecting to relational databases. It
 * provides a straightforward means for programs to make use of either
 * without having to hard-code the API choice. This is useful in developing a
 * library that may be used in a standalone program or in a container.
 *
 * Usage:
 *
 * <code>
 * public class DatabaseAPIExample {
 *     private DatabaseAPI databaseAPI;
 *     private String databaseId;
 *     private String username;
 *     private String password;
 *
 *     public DatabaseAPIExample(DatabaseAPI databaseAPI, String databaseId,
 *         String username, String password) {
 *         this.databaseAPI = databasePID;
 *         this.databaseId = databaseId;
 *         this.username = username;
 *         this.password = password;
 *     }
 *
 *     public void doProcessData() {
 *         ConnectionSpec connectionSpec =
 *                 this.databaseAPI.newConnectionSpecInstance(
 *                     this.databaseId, this.username, this.password);
 *         ...
 *     }
 * }
 * </code>
 */
public enum DatabaseAPI {

    DRIVERMANAGER, DATASOURCE;

    /**
     * Gets a {@link ConnectionSpec} implementation that is specific to the
     * selected database API for getting/creating a database {@link Connection}.
     * 
     * @param databaseId the identifier for the database (JDBC URL for
     * the {@link java.sql.DriverManager} API, JNDI name for the
     * {@link javax.sql.DataSource} API.
     * @param username the username (<code>null</code> if not applicable)
     * @param password the password (<code>null</code> if not applicable)
     * @return a {@link ConnectionSpec}.
     * @throws InvalidConnectionSpecArguments if the databaseId is invalid.
     */
    public ConnectionSpec newConnectionSpecInstance(String databaseId, 
            String username, String password)
            throws InvalidConnectionSpecArguments {
        ConnectionSpec connectionSpec;
        switch (this) {
            case DRIVERMANAGER:
                connectionSpec = 
                        new DriverManagerConnectionSpec(databaseId, username,
                        password);
                break;
            case DATASOURCE:
                try {
                    connectionSpec = new DataSourceConnectionSpec(databaseId,
                            username, password);
                } catch (NamingException ex) {
                    throw new InvalidConnectionSpecArguments(ex);
                }
                break;
            default:
                throw new AssertionError("cannot be reached");
        }
        return connectionSpec;
    }
}
