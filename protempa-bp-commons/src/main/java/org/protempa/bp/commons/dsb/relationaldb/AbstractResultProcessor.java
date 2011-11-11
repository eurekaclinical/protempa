package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractResultProcessor implements SQLGenResultProcessor {

    private String dataSourceBackendId;
    private EntitySpec entitySpec;
    private boolean casePresent;
    
    protected AbstractResultProcessor(EntitySpec entitySpec, 
            String dataSourceBackendId) {
        assert entitySpec != null : "entitySpec cannot be null";
        assert dataSourceBackendId != null : 
                "dataSourceBackendId cannot be null";
        this.entitySpec = entitySpec;
        this.dataSourceBackendId = dataSourceBackendId;
    }

    final String getDataSourceBackendId() {
        return dataSourceBackendId;
    }

    @Override
    public final EntitySpec getEntitySpec() {
        return this.entitySpec;
    }

    @Override
    public final boolean isCasePresent() {
        return this.casePresent;
    }

    @Override
    public final void setCasePresent(boolean casePresent) {
        this.casePresent = casePresent;
    }

    protected static int readUniqueIds(String[] uniqueIds, ResultSet resultSet,
            int i) throws SQLException {
        for (int m = 0; m < uniqueIds.length; m++) {
            uniqueIds[m] = resultSet.getString(i++);
        }
        return i;
    }

    protected final UniqueId generateUniqueId(String name,
            String[] uniqueIds) {
        return new UniqueId(
                DataSourceBackendId.getInstance(this.dataSourceBackendId),
                new SQLGenUniqueId(name, uniqueIds));
    }
}
