package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 * Implements parsing of dates/times using {@link ResultSet#getDate(int)}.
 * 
 * @author Andrew Post
 */
public final class JDBCDatePositionParser implements JDBCPositionFormat {

    @Override
    public Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        Date result = resultSet.getDate(columnIndex);
        if (result != null) {
            return result.getTime();
        } else {
            return null;
        }

    }

    @Override
    public String format(Long position) {
        return "{ts '" + AbsoluteTimeGranularity.toSQLString(position) + "'}";
    }
}
