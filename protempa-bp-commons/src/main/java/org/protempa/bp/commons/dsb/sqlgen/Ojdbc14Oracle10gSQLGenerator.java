package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.protempa.bp.commons.dsb.SQLOrderBy;

/**
 *
 * @author Andrew Post
 */
public class Ojdbc14Oracle10gSQLGenerator extends AbstractSQLGenerator {

    private static final String getAllKeyIdsSQL =
        "select b.keyid from " +
        "(select keyid, rownum rnum from " +
        "(select {0} from {1} " +
        "where {2}) a " +
        "where rownum < {4,number,#}) b " +
        "where rnum >= {3,number,#}";

    private static final String readPropositionsSQL =
            "select {0} from {1} where {2}";

    public boolean checkCompatibility(Connection connection) 
            throws SQLException {
        if (checkDriverCompatibility(connection))
            return false;
        if (checkDatabaseCompatibility(connection))
            return false;

        return true;
    }

    public boolean isLimitingSupported() {
        return true;
    }

    @Override
    public void generateSelectColumn(boolean distinctRequested,
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext) {
        if (distinctRequested) {
            selectPart.append(" distinct ");
        }
        selectPart.append("a").append(index).append('.').append(column);
        selectPart.append(" as ");
        selectPart.append(name);
        if (hasNext) {
            selectPart.append(',');
        }
    }

    @Override
    public void generateOn(StringBuilder fromPart, int fromIndex,
            int toIndex, String fromKey,
            String toKey) {
        fromPart.append("on (");
        fromPart.append('a').append(fromIndex).append('.');
        fromPart.append(fromKey);
        fromPart.append(" = a");
        fromPart.append(toIndex).append('.').append(toKey);
        fromPart.append(") ");
    }

    @Override
    public void generateFromTable(String schema, String table,
        StringBuilder fromPart, int i) {
        if (schema != null) {
            fromPart.append(schema);
            fromPart.append('.');
        }

        fromPart.append(table);
        generateFromTableReference(i, fromPart);
    }

    @Override
    public void generateFromTableReference(int i, StringBuilder fromPart) {
        fromPart.append(" a").append(i);
        fromPart.append(' ');
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

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().toUpperCase()
                .contains("ORACLE"))
            return false;
        if (metaData.getDatabaseMajorVersion() != 10)
            return false;
        return false;
    }

    private boolean checkDriverCompatibility(Connection connection) 
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!(name.equals("oracle.jdbc.driver.OracleDriver") ||
                name.equals("oracle.jdbc.OracleDriver")))
            return false;
        if (metaData.getDriverMajorVersion() != 10)
            return false;
        return false;
    }

    @Override
    public String assembleGetAllKeyIdsQuery(StringBuilder selectClause,
            StringBuilder fromClause,
            StringBuilder whereClause, int start, int count) {
        return MessageFormat.format(getAllKeyIdsSQL,
                selectClause, fromClause, whereClause, start,
                start + count);
    }

    @Override
    public String assembleReadPropositionsQuery(StringBuilder selectClause,
            StringBuilder fromClause, StringBuilder whereClause) {
        return MessageFormat.format(readPropositionsSQL,
                selectClause, fromClause, whereClause);
    }

    @Override
    public void generateJoin(StringBuilder fromPart) {
        fromPart.append(" join ");
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
            ColumnSpec.ConstraintValue[] constraintValue) {
        wherePart.append("a");
        wherePart.append(referenceIndex);
        wherePart.append(".");
        wherePart.append(column);
        wherePart.append(" IN (");
        for (int k = 0; k < constraintValue.length; k++) {
            Object val = constraintValue[k].getValue();
            appendValue(val, wherePart);
            if (k + 1 < constraintValue.length) {
                if ((k + 1) % 1000 == 0) {
                    wherePart.append(") OR ");
                    wherePart.append("a");
                    wherePart.append(referenceIndex);
                    wherePart.append(".");
                    wherePart.append(column);
                    wherePart.append(" IN (");
                } else {
                    wherePart.append(',');
                }
            }
        }
        wherePart.append(") ");
    }

    @Override
    protected String getDriverNameToLoad() {
        return "oracle.jdbc.driver.OracleDriver";
    }

}
