package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public interface PositionParser {
    long toLong(ResultSet resultSet, int columnIndex, int colType) throws SQLException;
}
