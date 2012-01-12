package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

/**
 * 
 * @author Andrew Post
 */
public class Ojdbc6OracleSQLGenerator extends AbstractSQLGenerator {

    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibility(connection)) {
            return false;
        }
        if (!checkDatabaseCompatibility(connection)) {
            return false;
        }

        return true;
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().equals("Oracle")) {
            return false;
        }
        int majorVersion = metaData.getDatabaseMajorVersion();
        if (majorVersion != 10 && majorVersion != 11) {
            return false;
        }

        return true;
    }

    private boolean checkDriverCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!name.equals("Oracle JDBC driver")) {
            return false;
        }
        int majorVersion = metaData.getDriverMajorVersion();
        if (majorVersion != 11) {
            return false;
        }

        return true;
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return "oracle.jdbc.OracleDriver";
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables) {
        return new Ojdbc6OracleSelectStatement(entitySpec, referenceSpec,
                entitySpecs, filters, propIds, keyIds, order, resultProcessor,
                stagedTables);
    }

    @Override
    protected DataStager getDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        return new Ojdbc6OracleDataStager(stagingSpecs, referenceSpec,
                entitySpecs, filters, propIds, keyIds, order, resultProcessor);
    }

}
