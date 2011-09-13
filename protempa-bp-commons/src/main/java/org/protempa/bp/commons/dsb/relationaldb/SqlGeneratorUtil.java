package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

/**
 * Utility class for SQL generation
 * 
 * @author Michel Mansour
 *
 */
public final class SqlGeneratorUtil {
    private SqlGeneratorUtil() {
    }

    static String generateColumnReference(SqlStatement statement,
            int tableNumber, String columnName) {
        return statement.generateTableReference(tableNumber) + "." + columnName;
    }
    
    static String generateColumnReference(SqlStatement statement, ColumnSpec.ColumnOp columnOp,
            int tableNumber, ColumnSpec columnSpec) {
        StringBuilder result = new StringBuilder();
        if (columnOp != null) {
            switch (columnOp) {
                case UPPER:
                    result.append("upper");
                    break;
                default:
                    throw new AssertionError("invalid column op: " + columnOp);
            }
            result.append('(');
        }
        generateColumnReference(statement, tableNumber, columnSpec.getColumn());
        if (columnOp != null) {
            result.append(')');
        }
        
        return result.toString();
    }

    static String appendColumnReference(SqlStatement statement,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec) {
        StringBuilder result = new StringBuilder();
        Integer tableNumber = referenceIndices.get(columnSpec);
        assert tableNumber != null : "tableNumber is null";
        result.append(generateColumnReference(statement, tableNumber, columnSpec.getColumn()));
        
        return result.toString();
    }
    
    static String appendColumnRef(SqlStatement statement, Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec) {
        StringBuilder result = new StringBuilder();
        if (columnSpec.getColumnOp() != null) {
            Integer tableNumber = referenceIndices.get(columnSpec);
            assert tableNumber != null : "tableNumber is null";
            result.append(generateColumnReference(statement, columnSpec.getColumnOp(), tableNumber,
                    columnSpec));
        } else {
            result.append(appendColumnReference(statement, referenceIndices, columnSpec));
        }
        
        return result.toString();
    }
    
    public static String appendValue(Object val) {
        StringBuilder result = new StringBuilder();
        
        boolean numberOrBoolean;
        if (!(val instanceof Number) && !(val instanceof Boolean)) {
            numberOrBoolean = false;
            result.append("'");
        } else {
            numberOrBoolean = true;
        }
        if (val instanceof Boolean) {
            Boolean boolVal = (Boolean) val;
            if (boolVal.equals(Boolean.TRUE)) {
                result.append(1);
            } else {
                result.append(0);
            }
        } else {
            result.append(val);
        }
        if (!numberOrBoolean) {
            result.append("'");
        }
        return result.toString();
    }
}
