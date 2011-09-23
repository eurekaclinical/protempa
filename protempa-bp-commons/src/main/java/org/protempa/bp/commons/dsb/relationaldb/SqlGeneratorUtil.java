package org.protempa.bp.commons.dsb.relationaldb;


/**
 * Utility class for SQL generation
 * 
 * @author Michel Mansour
 *
 */
public final class SqlGeneratorUtil {
    private SqlGeneratorUtil() {
    }

    /**
     * Generates an SQL-ready string for the given value based on its type.
     * 
     * @param val the value to prepare
     * @return a <tt>String</tt> ready to be appended to an SQL statement
     */
    public static String prepareValue(Object val) {
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
