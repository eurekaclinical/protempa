package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.backend.dsb.filter.Filter;

/**
 * Generates SQL compatible with the H2 database engine 1.x
 * (http://www.h2database.com).
 * 
 * @author Michel Mansour
 */
public final class H2SQLGenerator extends AbstractSQLGenerator {

    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibilitiy(connection)) {
            return false;
        }
        if (!checkDatabaseCompatibility(connection)) {
            return false;
        }

        return true;
    }

    private boolean checkDriverCompatibilitiy(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        if (!name.equals("H2 JDBC Driver")) {
            return false;
        }
        if (metaData.getDatabaseMajorVersion() != 1) {
            return false;
        }

        return true;
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (!metaData.getDatabaseProductName().equalsIgnoreCase("H2")) {
            return false;
        }
        if (metaData.getDatabaseMajorVersion() != 1) {
            return false;
        }

        return true;
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return "org.h2.Driver";
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables) {
        return new H2SelectStatement(entitySpec, referenceSpec, entitySpecs,
                filters, propIds, keyIds, order, resultProcessor);
    }
}
