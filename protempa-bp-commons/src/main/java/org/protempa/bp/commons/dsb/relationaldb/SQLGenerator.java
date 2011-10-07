package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import org.arp.javautil.sql.ConnectionSpec;

import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend;
import org.protempa.bp.commons.dsb.relationaldb.ConnectorJ5MySQL415Generator;
import org.protempa.bp.commons.dsb.relationaldb.RelationalDatabaseSpec;
import org.protempa.bp.commons.dsb.relationaldb.ResultCache;
import org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * An API for ServiceLoader-style services that generate database- and driver-
 * specific SQL for the {@link RelationalDatabaseSchemaAdaptor}.
 * 
 * The following database-specific SQL generators are provided:
 * <ul>
 * <li>{@link ConnectorJ5MySQL415Generator}
 * <li>{@link Ojdbc6OracleSQLGenerator}
 * </ul>
 * 
 * The user may specify additional SQL generators by implementing this interface
 * and registering the implementation using the Java ServiceLoader method. Note
 * that if generators have overlapping compatibility, the generator that
 * {@link RelationalDatabaseSchemaAdaptor} will use is not deterministic.
 * 
 * @author Andrew Post
 */
public interface SQLGenerator {

    ResultCache<Proposition> readPropositions(Set<String> keyIds,
            Set<String> propIds, Filter dataSourceConstraints,
            SQLOrderBy order) throws DataSourceReadException;

    GranularityFactory getGranularities();

    UnitFactory getUnits();

    /**
     * Checks the compatibility of this generator with the database that
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
            RelationalDbDataSourceBackend backend);

    /**
     * For loading the database driver if needed for the driver that this
     * generator supports. Call this method before other methods of this
     * class.
     * 
     * @return <code>true</code> if the driver was successfully loaded, 
     * <code>false</code> if not.
     */
    boolean loadDriverIfNeeded();
}
