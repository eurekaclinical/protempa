package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * A SQL generator that is compatible with Connector/J 5.x and MySQL 4.1 and
 * 5.x.
 * 
 * @author Andrew Post
 */
public class ConnectorJ5MySQL415Generator extends AbstractSQLGenerator {

    private static final String readPropositionsSQL =
            "select {0} from {1} {2}";

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

    @Override
    public boolean isLimitingSupported() {
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
    public void generateSelectColumn(boolean distinctRequested, 
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext) {
        if (distinctRequested) {
            selectPart.append("distinct ");
        }
        selectPart.append("a");
        selectPart.append(index);
        selectPart.append('.');
        selectPart.append(column);
        selectPart.append(" as ");
        selectPart.append(name);
        if (hasNext) {
            selectPart.append(',');
        }
    }

    @Override
    public String assembleReadPropositionsQuery(StringBuilder selectClause,
            StringBuilder fromClause, StringBuilder whereClause) {
        return MessageFormat.format(readPropositionsSQL,
                selectClause, fromClause, whereClause);
    }

    @Override
    public void generateFromTable(String schema, String table,
        StringBuilder fromPart, int i) {
        if (schema != null)
            throw new IllegalArgumentException("schema is not supported");
        fromPart.append(table);
        generateFromTableReference(i, fromPart);
    }

    @Override
    public void generateFromTableReference(int i, StringBuilder fromPart) {
        fromPart.append(" a").append(i);
        fromPart.append(' ');
    }

    @Override
    public void generateOn(StringBuilder fromPart, int fromIndex,
            int toIndex, String fromKey,
            String toKey) {
        fromPart.append("on (");
        fromPart.append('a');
        fromPart.append(fromIndex);
        fromPart.append('.');
        fromPart.append(fromKey);
        fromPart.append(" = a");
        fromPart.append(toIndex);
        fromPart.append('.');
        fromPart.append(toKey);
        fromPart.append(") ");
    }

    @Override
    public void generateJoin(StringBuilder fromPart) {
        fromPart.append(" join ");
    }

    @Override
    public void appendValue(Object val, StringBuilder wherePart) {
        boolean isNumber;
        if (!(val instanceof Number)) {
            isNumber = false;
            wherePart.append("'");
        } else {
            isNumber = true;
        }
        wherePart.append(val);
        if (!isNumber) {
            wherePart.append("'");
        }
    }

    @Override
    public void generateFromSeparator(StringBuilder fromPart) {
        fromPart.append(',');
    }

    @Override
    public void processOrderBy(int startReferenceIndex, String startColumn,
            int finishReferenceIndex, String finishColumn,
            StringBuilder wherePart,SQLOrderBy order) {
        wherePart.append(" order by ");
        wherePart.append('a').append(startReferenceIndex);
        wherePart.append('.').append(startColumn);
        if (finishReferenceIndex > 0) {
            wherePart.append(",a").append(finishReferenceIndex);
            wherePart.append('.').append(finishColumn);
        }
        wherePart.append(' ');
        if (order == SQLOrderBy.ASCENDING) {
            wherePart.append("ASC");
        } else {
            wherePart.append("DESC");
        }
    }

    @Override
    public void generateInClause(StringBuilder wherePart,
            int referenceIndex, String column,
            Object[] sqlCodes, boolean not) {
        wherePart.append("a");
        wherePart.append(referenceIndex);
        wherePart.append(".");
        wherePart.append(column);
        if (not)
            wherePart.append(" NOT");
        wherePart.append(" IN (");
        for (int k = 0; k < sqlCodes.length; k++) {
            Object sqlCode = sqlCodes[k];
            appendValue(sqlCode, wherePart);
            if (k + 1 < sqlCodes.length) {
                wherePart.append(',');
            }
        }
        wherePart.append(") ");
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return driverName;
    }

    

}
