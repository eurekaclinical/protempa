package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * A SQL generator that is compatible with Connector/J 5.x and MySQL 4.1 and
 * 5.x.
 * 
 * @author Andrew Post
 */
public class ConnectorJ5MySQL415Generator extends AbstractSQLGenerator {

    private static final String driverName = "com.mysql.jdbc.Driver";

    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibility(connection))
            return false;
        if (!checkDatabaseCompatibility(connection))
            return false;

        return true;
    }

    private boolean checkDriverCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!name.equals("MySQL-AB JDBC Driver"))
            return false;
        if (metaData.getDriverMajorVersion() != 5)
            return false;
        return true;
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().toUpperCase()
                .contains("MYSQL"))
            return false;
        int dbMajorVersion = metaData.getDatabaseMajorVersion();
        if (dbMajorVersion != 4 && dbMajorVersion != 5)
            return false;
        return true;
    }

    @Override
    public void generateFromTable(String schema, String table,
        StringBuilder fromPart, int i) {
        if (schema != null)
            throw new IllegalArgumentException("schema is not supported");
        fromPart.append(table);
        generateTableReference(i, fromPart);
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return driverName;
    }
}
