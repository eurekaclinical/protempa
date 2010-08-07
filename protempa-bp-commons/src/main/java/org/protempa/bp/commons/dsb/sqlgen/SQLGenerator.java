package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.bp.commons.dsb.SQLOrderBy;

/**
 * An API for ServiceLoader-style services that generate database-specific SQL
 * for the {@link RelationalDatabaseSchemaAdaptor}.
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
     * Returns whether the SQL generator generates row limiting clauses,
     * e.g., MySQL's LIMIT, Oracle's rownum.
     * 
     * @return true if the SQL generator generates low limiting clauses,
     * false if not.
     */
    boolean isLimitingSupported();

    /**
     * Returns a SQL statement for use by
     * {@link RelationalDatabaseSchemaAdaptor.getAllKeyIds()}.
     *
     * @param keyColumn the column in which the key id exists.
     * @param keyTable the table to use when retrieving the key ids.
     * @param start return key ids starting with this row number.
     * @param count return this many key ids.
     * @param dataSourceConstraints a {@link DataSourceConstraint} with
     * position and value constraints that reduce the number of key ids
     * returned. If <code>null</code>, no constraints will be applied.
     * @return a {@link String} containing a SQL statement.
     */
    String generateGetAllKeyIdsQuery(
            int start, int count, DataSourceConstraint dataSourceConstraints,
            Map<PropertySpec, List<String>> specs);

    String generateReadPropositionsQuery(Set<String> propIds,
            DataSourceConstraint dataSourceConstraints,
            Map<PropertySpec, List<String>> specs, Set<String> keyIds,
            SQLOrderBy order);

    void loadDriverIfNeeded();
}
