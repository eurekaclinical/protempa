package org.arp.javautil.sql;

import javax.naming.NamingException;

/**
 * For specifying which Java database API to use for getting database
 * connections ({@link java.sql.DriverManager} or
 * {@link javax.sql.DataSource}).
 */
public enum DatabaseAPI {

    DRIVERMANAGER, DATASOURCE;

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
