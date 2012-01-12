package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

/**
 * A SQL generator that is compatible with Connector/J 5.x and MySQL 4.1 and
 * 5.x.
 * 
 * @author Andrew Post
 */
public class ConnectorJ5MySQL415Generator extends AbstractSQLGenerator {

    private static final String driverName = "com.mysql.jdbc.Driver";

    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibility(connection))
            return false;
        if (!checkDatabaseCompatibility(connection))
            return false;

        return true;
    }

    private boolean checkDriverCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!name.equals("MySQL-AB JDBC Driver"))
            return false;
        if (metaData.getDriverMajorVersion() != 5)
            return false;
        return true;
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().toUpperCase().contains("MYSQL"))
            return false;
        int dbMajorVersion = metaData.getDatabaseMajorVersion();
        if (dbMajorVersion != 4 && dbMajorVersion != 5)
            return false;
        return true;
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return driverName;
    }
    
    @Override
    /*
     * MySQL SQL generator does not currently support staging data
     * (non-Javadoc)
     * @see org.protempa.bp.commons.dsb.relationaldb.AbstractSQLGenerator#getSelectStatement(org.protempa.bp.commons.dsb.relationaldb.EntitySpec, org.protempa.bp.commons.dsb.relationaldb.ReferenceSpec, java.util.List, java.util.Set, java.util.Set, java.util.Set, org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy, org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor, java.util.Map)
     */
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, StagingSpec[] stagedTables) {
        return new ConnectorJ5MySQL415SelectStatement(entitySpec, referenceSpec, entitySpecs,
                filters, propIds, keyIds, order, resultProcessor);
    }

    @Override
    protected DataStager getDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        return null;
    }

}
