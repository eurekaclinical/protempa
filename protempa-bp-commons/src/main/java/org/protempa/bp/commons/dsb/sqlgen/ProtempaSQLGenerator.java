package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.SQLException;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;
import org.protempa.bp.commons.dsb.SQLGenerator;

/**
 * Generates SQL for PROTEMPA, and abstracts away database and driver-specific
 * initialization routines. This is called by
 * {@link RelationalDatabaseDataSourceBackend}.
 *
 * @author Andrew Post
 */
public interface ProtempaSQLGenerator extends SQLGenerator {
    /**
     * Checks the compatibility of this generator with the databaes that
     * is connected to. Call {@link #loadDriverIfNeeded()} before calling this
     * method to ensure that the database driver for this SQL generator has
     * been loaded.
     * 
     * @param connection a database {@link Connection}.
     * @return <code>true</code> if compatible, <code>false</code> if not.
     * @throws SQLException if an error occurs in querying the database for
     * compatibility.
     */
    boolean checkCompatibility(Connection connection)
            throws SQLException;

    /**
     * Initializes the generator with schema mapping information
     * (@link RelationalDatabaseSpec}), a {@link ConnectionSpec} for
     * connecting to the database, and the relational database data source
     * backend that is calling this generator. This method should only be
     * called after {@link #loadDriverIfNeeded()} and
     * {@link #checkCompatibility(java.sql.Connection)}.
     *
     * @param relationalDatabaseSpec a {@link RelationalDatabaseSpec}.
     * @param connectionSpec a {@link ConnectionSpec}.
     * @param backend a {@link RelationalDatabaseDataSourceBackend}.
     */
    void initialize(RelationalDatabaseSpec relationalDatabaseSpec,
            ConnectionSpec connectionSpec,
            RelationalDatabaseDataSourceBackend backend);

    /**
     * For loading the database driver if needed for the driver that this
     * generator supports. Call this method before other methods of this
     * class.
     */
    void loadDriverIfNeeded();
}
