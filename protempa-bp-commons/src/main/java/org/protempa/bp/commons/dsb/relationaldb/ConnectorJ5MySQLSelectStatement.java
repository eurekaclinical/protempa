package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class ConnectorJ5MySQLSelectStatement extends SelectStatement {

    public ConnectorJ5MySQLSelectStatement(EntitySpec entitySpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor);
    }
    
    @Override
    protected SelectClause getSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getFromClause(java.util.List, java.util.Map)
     */
    @Override
    protected FromClause getFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getWhereClause(org.protempa.bp.commons.dsb.relationaldb.ColumnSpecInfo, java.util.List, java.util.Set, java.util.Map, java.util.Set, org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy, org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor)
     */
    @Override
    protected WhereClause getWhereClause(ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Map<ColumnSpec, Integer> referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        // TODO Auto-generated method stub
        return null;
    }

}
