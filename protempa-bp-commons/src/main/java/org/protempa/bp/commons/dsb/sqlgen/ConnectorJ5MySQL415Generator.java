package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.protempa.bp.commons.dsb.SQLOrderBy;

/**
 * A SQL generator that is compatible with Connector/J 5.x and MySQL 4.1 and
 * 5.x.
 * 
 * @author Andrew Post
 */
public class ConnectorJ5MySQL415Generator extends AbstractSQLGenerator {

    private static final String getAllKeyIdsSQL =
        "select {0} from {1} " +
        "where {2}) a " +
        "limit {3,number,#}, " +
        "{4,number,#}";

    private static final String readPropositionsSQL =
            "select {0} from {1} where {2}";

    public boolean checkCompatibility(Driver driver, Connection connection)
            throws SQLException {
        if (checkDriverCompatibility(driver))
            return false;
        if (checkDatabaseCompatibility(connection))
            return false;

        return true;
    }

    public boolean isLimitingSupported() {
        return true;
    }

    private boolean checkDriverCompatibility(Driver driver) {
        String name = driver.getClass().getName();
        if (!name.equals("com.mysql.jdbc.Driver"))
            return false;
        if (driver.getMajorVersion() != 5)
            return false;
        return false;
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
        return false;
    }

    @Override
    public void generateSelectColumn(boolean distinctRequested, 
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext) {
        if (distinctRequested) {
            selectPart.append(" distinct");
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
    public String assembleGetAllKeyIdsQuery(StringBuilder selectClause,
            StringBuilder fromClause,
            StringBuilder whereClause, int start, int count) {
        return MessageFormat.format(getAllKeyIdsSQL,
                selectClause, fromClause, whereClause, start,
                count);
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
            ColumnSpec columnSpec) {
        wherePart.append(" IN (");
        ColumnSpec.ConstraintValue[] constraintValue =
                columnSpec.getConstraintValues();
        for (int k = 0; k < constraintValue.length; k++) {
            Object val = constraintValue[k].getValue();
            appendValue(val, wherePart);
            if (k + 1 < constraintValue.length) {
                wherePart.append(',');
            }
        }
    }

}
