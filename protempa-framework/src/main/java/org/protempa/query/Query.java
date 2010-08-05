package org.protempa.query;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;

/**
 * 
 * @author Andrew Post
 */
public interface Query extends Cloneable {
    void setKeyIds(String[] keyIds);

    String[] getKeyIds();

    void setDataSourceConstraints(DataSourceConstraint dataSourceConstraints);

    DataSourceConstraint getDataSourceConstraints();

    /**
     * 
     * @param propIds
     *            an array of proposition id {@link String}s.
     */
    void setPropIds(String[] propIds);

    String[] getPropIds();

    public Query clone();
}
