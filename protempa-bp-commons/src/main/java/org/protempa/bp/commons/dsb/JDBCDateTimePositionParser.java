package org.protempa.bp.commons.dsb;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Implements parsing of JDBC dates, times and timestamps.
 * 
 * @author Andrew Post
 */
public final class JDBCDateTimePositionParser implements PositionParser {

    @Override
    public long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        switch (colType) {
            case Types.DATE:
                Date date = resultSet.getDate(columnIndex);
                return date.getTime();
            case Types.TIME:
                Time time = resultSet.getTime(columnIndex);
                return time.getTime();
            case Types.TIMESTAMP:
                Timestamp timestamp = resultSet.getTimestamp(columnIndex);
                return timestamp.getTime();
            default:
                throw new AssertionError("Not a date, time or timestamp!");
        }

    }

}
