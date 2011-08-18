package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface for classes that parse data in a database into a PROTEMPA
 * position and format a PROTEMPA position into a string suitable for
 * inclusion in a SELECT statement executed via JDBC.
 * 
 * @author Andrew Post
 */
public interface JDBCPositionFormat {
    /**
     * Parses a column in a JDBC result set into a PROTEMPA position.
     * 
     * @param resultSet a {@link ResultSet}. Cannot be <code>null</code>.
     * @param columnIndex the index of the column to retrieve.
     * @param columnType the type of the column. A {@link java.sql.Types}.
     * @return a PROTEMPA position.
     * 
     * @throws SQLException if an error occurs accessing the result set or the
     * column cannot be parsed.
     */
    Long toLong(ResultSet resultSet, int columnIndex, int columnType)
            throws SQLException;
    
    /**
     * Formats a PROTEMPA long as a string suitable for inclusion in a SELECT 
     * statement executed via JDBC.
     * 
     * @param position a PROTEMPA position.
     * @return a {@link String}.
     */
    String format(Long position);
}
