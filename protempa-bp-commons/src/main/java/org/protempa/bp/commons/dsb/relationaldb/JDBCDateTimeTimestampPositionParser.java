package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 * Implements parsing of dates/times into a PROTEMPA position using an 
 * appropriate call to {@link ResultSet}. 
 * 
 * @author Andrew Post
 */
public final class JDBCDateTimeTimestampPositionParser
        implements JDBCPositionFormat {

    /**
     * Parses a date/time into a PROTEMPA position. For types date, time and 
     * timestamp it calls the corresponding method in {@link ResultSet}. For 
     * other types, it calls {@link ResultSet#getTimestamp(int)} and lets the 
     * JDBC driver attempt to parse a date.
     * 
     * @param resultSet a {@link ResultSet}. Cannot be <code>null</code>.
     * @param columnIndex the index of the column to retrieve from the result
     * set.
     * @param colType the SQL type of the column as a {@link Types}.
     * @return a PROTEMPA position or <code>null</code> if the column in the
     * database is <code>NULL</code>.
     * 
     * @throws SQLException if there is an error accessing the result set or 
     * the column cannot be parsed into a date.
     */
    @Override
    public Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        Date result;
        switch (colType) {
            case Types.DATE:
                result = resultSet.getDate(columnIndex);
                break;
            case Types.TIME:
                result = resultSet.getTime(columnIndex);
                break;
            default:
                /*
                 * We'll end up here for Types.TIMESTAMP and non-date SQL data
                 * types. For the latter, we'll let the JDBC driver try to 
                 * parse a date.
                 */
                result = resultSet.getTimestamp(columnIndex);
        }
        if (result != null) {
            return result.getTime();
        } else {
            return null;
        }

    }

    /**
     * Formats a PROTEMPA long as a JDBC timestamp string suitable for
     * inclusion in a SELECT statement executed via JDBC.
     * 
     * @param position a PROTEMPA position.
     * @return a JDBC timestamp {@link String}.
     */
    @Override
    public String format(Long position) {
        return "{ts '" + AbsoluteTimeGranularity.toSQLString(
                position) + "'}";
    }
}
