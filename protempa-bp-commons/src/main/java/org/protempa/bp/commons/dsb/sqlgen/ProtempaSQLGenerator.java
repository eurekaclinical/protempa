package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.SQLException;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;
import org.protempa.bp.commons.dsb.SQLGenerator;

/**
 *
 * @author Abdrew Post
 */
public interface ProtempaSQLGenerator extends SQLGenerator {
    boolean checkCompatibility(Connection connection)
            throws SQLException;

    void initialize(RelationalDatabaseSpec relationalDatabaseSpec,
            ConnectionSpec connectionSpec,
            RelationalDatabaseDataSourceBackend backend);

    void loadDriverIfNeeded();
}
