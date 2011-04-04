package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public interface PositionFormat {
    Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException;
    String format(Long position);
}
