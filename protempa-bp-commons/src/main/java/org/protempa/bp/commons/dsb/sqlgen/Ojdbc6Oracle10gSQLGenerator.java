package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public class Ojdbc6Oracle10gSQLGenerator extends AbstractSQLGenerator {
    
    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibility(connection)) {
            return false;
        }
        if (!checkDatabaseCompatibility(connection)) {
            return false;
        }

        return true;
    }

    @Override
    public void generateFromTable(String schema, String table,
            StringBuilder fromPart, int i) {
        if (schema != null) {
            fromPart.append(schema);
            fromPart.append('.');
        }

        fromPart.append(table);
        generateTableReference(i, fromPart);
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().equals("Oracle")) {
            return false;
        }
        if (metaData.getDatabaseMajorVersion() != 10) {
            return false;
        }

        return true;
    }

    private boolean checkDriverCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!name.equals("Oracle JDBC driver")) {
            return false;
        }
        int majorVersion = metaData.getDriverMajorVersion();
        if (majorVersion != 11) {
            return false;
        }

        return true;
    }

    /**
     * Oracle doesn't allow more than 1000 elements in an IN clause, so if
     * we want more than 1000 we create multiple IN clauses chained together
     * by OR.
     * 
     * @param wherePart the SQL statement {@link StringBuilder}.
     * @param tableNumber the table number.
     * @param columnName the column name {@link String}.
     * @param elements the elements of the IN clause.
     * @param not set to <code>true</code> to generate <code>NOT IN</code>.
     */
    @Override
    public void generateInClause(StringBuilder wherePart,
            int tableNumber, String columnName, Object[] elements,
            boolean not) {
        generateColumnReference(tableNumber, columnName, wherePart);
        if (not) {
            wherePart.append(" NOT");
        }
        wherePart.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object val = elements[k];
            appendValue(val, wherePart);
            if (k + 1 < elements.length) {
                if ((k + 1) % 1000 == 0) {
                    wherePart.append(") OR ");
                    generateColumnReference(tableNumber, columnName, wherePart);
                    wherePart.append(" IN (");
                } else {
                    wherePart.append(',');
                }
            }
        }
        wherePart.append(')');
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return "oracle.jdbc.OracleDriver";
    }
}
