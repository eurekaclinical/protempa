package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 * Implements parsing of JDBC dates, times and timestamps.
 * 
 * @author Andrew Post
 */
public final class JDBCDateTimePositionParser implements PositionFormat {

    @Override
    public Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        Date result = resultSet.getTimestamp(columnIndex);
        if (result != null) {
            return result.getTime();
        } else {
            return null;
        }

    }

    @Override
    public String format(Long position) {
        return "{ts '" + AbsoluteTimeGranularity.toSQLString(
                                            position) + "'}";
    }
}
