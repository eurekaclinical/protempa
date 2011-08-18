package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public interface JDBCValueFormat {
    Value toValue(ResultSet resultSet, int columnIndex, int columnType) 
            throws SQLException;
}
