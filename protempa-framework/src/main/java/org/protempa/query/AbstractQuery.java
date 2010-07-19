package org.protempa.query;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractQuery implements Query {
    private String[] keyIds;
    private DataSourceConstraint dataSourceConstraints;
    private String[] propIds;

    public AbstractQuery() {
        
    }

    public DataSourceConstraint getDataSourceConstraints() {
        return this.dataSourceConstraints;
    }

    public void setDataSourceConstraints(
            DataSourceConstraint dataSourceConstraints) {
        this.dataSourceConstraints = dataSourceConstraints;
    }

    public String[] getKeyIds() {
        return this.keyIds;
    }

    public void setKeyIds(String[] keyIds) {
        this.keyIds = keyIds;
    }

    public String[] getPropIds() {
        return this.propIds;
    }

    /**
     * If <code>null</code>, PROTEMPA will search the entire database.
     * @param propIds
     */
    public void setPropIds(String[] propIds) {
        this.propIds = propIds;
    }
}
