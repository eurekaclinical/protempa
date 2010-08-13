package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;

/**
 * An API for ServiceLoader-style services that generate database- and driver-
 * specific SQL for the {@link RelationalDatabaseSchemaAdaptor}.
 *
 * The following database-specific SQL generators are provided:
 * <ul>
 * <li>{@link ConnectorJ5MySQL415Generator}
 * <li>{@link Ojdbc14Oracle10gSQLGenerator}
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
    /**
     * Checks whether this SQL generator is compatible with the specified
     * driver and a database to which the program is connected.
     * 
     * @param connection a {@link Connection} to a database.
     * @return true if this SQL generator is compatible, false otherwise.
     * @throws SQLException if a connection to the database to determine
     * compatibility fails.
     */
    boolean checkCompatibility(Connection connection) throws SQLException;


    /**
     * Returns whether the SQL generator supports limiting the returned rows
     * by row number.
     * 
     * @return <code>true</code> if the SQL generator supports limiting the
     * returned rows by number, <code>false</code> otherwise.
     */
    boolean isLimitingSupported();

    /**
     * Generates a SQL select statement meeting the specified criteria.
     * @param propIds
     * @param dataSourceConstraints
     * @param propertySpecs
     * @param keyIds
     * @param order
     * @return a SQL select statement {@link String}.
     */
    String generateSelect(Set<String> propIds,
            DataSourceConstraint dataSourceConstraints,
            Set<PropertySpec> propertySpecs, Set<String> keyIds,
            SQLOrderBy order);

    /**
     * If the supported driver is JDBC 3 or earlier, load the driver here
     * using {@link Class#forName}.
     */
    void loadDriverIfNeeded();
}
