package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractResultProcessor implements SQLGenResultProcessor {
    private String dataSourceBackendId;
    private EntitySpec entitySpec;
    private boolean casePresent;

    final String getDataSourceBackendId() {
        return dataSourceBackendId;
    }

    final void setDataSourceBackendId(String dataSourceBackendId) {
        assert dataSourceBackendId != null :
            "dataSourceBackendId cannot be null";
        this.dataSourceBackendId = dataSourceBackendId;
    }

    final void setEntitySpec(EntitySpec entitySpec) {
        assert entitySpec != null : "entitySpec cannot be null";
        this.entitySpec = entitySpec;
    }

    final EntitySpec getEntitySpec() {
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
    
    protected final UniqueIdentifier generateUniqueIdentifier(
            String entitySpecName, String[] uniqueIds)
            throws SQLException {
        
        return new UniqueIdentifier(
                DataSourceBackendId.getInstance(this.dataSourceBackendId),
                new SQLGenUniqueIdentifier(entitySpecName, uniqueIds));
    }
}
