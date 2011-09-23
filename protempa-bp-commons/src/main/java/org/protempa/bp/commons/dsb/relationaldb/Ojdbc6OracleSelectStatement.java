package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleSelectStatement extends AbstractSelectStatement {

    Ojdbc6OracleSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor);
    }

    @Override
    public SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec) {
        return new Ojdbc6OracleSelectClause(info, referenceIndices, entitySpec);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getFromClause
     * (java.util.List, java.util.Map)
     */
    @Override
    public FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        return new Ojdbc6OracleFromClause(columnSpecs, referenceIndices);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getWhereClause
     * (org.protempa.bp.commons.dsb.relationaldb.ColumnSpecInfo, java.util.List,
     * java.util.Set, java.util.Map, java.util.Set,
     * org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy,
     * org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor)
     */
    @Override
    public WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, SelectClause selectClause) {
        return new Ojdbc6OracleWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause);
    }

}
