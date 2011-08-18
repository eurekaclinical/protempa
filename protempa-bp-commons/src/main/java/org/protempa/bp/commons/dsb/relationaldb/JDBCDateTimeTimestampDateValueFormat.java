package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class JDBCDateTimeTimestampDateValueFormat implements JDBCValueFormat {

    @Override
    public Value toValue(ResultSet resultSet, int columnIndex, int columnType) 
            throws SQLException {
        Date date;
        
        switch (columnType) {
            case Types.DATE:
                date = resultSet.getDate(columnIndex);
                break;
            case Types.TIME:
                date = resultSet.getTime(columnIndex);
                break;
            default:
                /*
                 * We'll end up here for Types.TIMESTAMP and non-date SQL data
                 * types. For the latter, we'll let the JDBC driver try to 
                 * parse a date.
                 */
                date = resultSet.getTimestamp(columnIndex);
        }
        if (date != null) {
            return DateValue.getInstance(date);
        } else {
            return null;
        }
    }
    
}
