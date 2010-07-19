package org.protempa.query;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public interface Query {
    void setKeyIds(String[] keyIds);
    String[] getKeyIds();

    void setDataSourceConstraints(DataSourceConstraint dataSourceConstraints);

    DataSourceConstraint getDataSourceConstraints();

    /**
     *
     * @param propIds an array of proposition id {@link String}s.
     */
    void setPropIds(String[] propIds);

    String[] getPropIds();
}
