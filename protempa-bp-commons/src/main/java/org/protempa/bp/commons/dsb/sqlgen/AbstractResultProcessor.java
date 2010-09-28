package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.protempa.DataSourceBackendId;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractResultProcessor implements ResultProcessor {
    private String dataSourceBackendId;
    private EntitySpec entitySpec;

    final String getDataSourceBackendId() {
        return dataSourceBackendId;
    }

    final void setDataSourceBackendId(String dataSourceBackendId) {
        this.dataSourceBackendId = dataSourceBackendId;
    }

    final void setEntitySpec(EntitySpec entitySpec) {
        this.entitySpec = entitySpec;
    }

    final EntitySpec getEntitySpec() {
        return this.entitySpec;
    }

    protected final String[] generateUniqueIdsArray(EntitySpec entitySpec) {
        return new String[entitySpec.getUniqueIdSpecs().length];
    }

    protected final int readUniqueIds(String[] uniqueIds, ResultSet resultSet,
            int i) throws SQLException {
        for (int m = 0; m < uniqueIds.length; m++) {
            uniqueIds[m] = resultSet.getString(i++);
        }
        return i;
    }
    
    protected final UniqueIdentifier generateUniqueIdentifier(
            EntitySpec entitySpec, String[] uniqueIds)
            throws SQLException {
        return new UniqueIdentifier(
                new DataSourceBackendId(getDataSourceBackendId()),
                new SQLGenUniqueIdentifier(entitySpec.getName(), uniqueIds));
    }
}
