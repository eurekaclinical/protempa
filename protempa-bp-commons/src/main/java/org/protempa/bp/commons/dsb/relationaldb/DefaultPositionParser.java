package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public final class DefaultPositionParser implements JDBCPositionFormat {

    @Override
    public Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    @Override
    public String format(Long position) {
        return position.toString();
    }
}
