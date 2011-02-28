package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public final class DefaultPositionParser implements PositionParser {

    public long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        return resultSet.getLong(columnIndex);
    }

}
