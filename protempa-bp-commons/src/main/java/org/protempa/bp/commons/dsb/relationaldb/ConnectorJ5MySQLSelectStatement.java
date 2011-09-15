package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class ConnectorJ5MySQLSelectStatement extends AbstractSelectStatement {

    public ConnectorJ5MySQLSelectStatement(EntitySpec entitySpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor);
    }
    
    @Override
    public AbstractSelectClause getSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getFromClause(java.util.List, java.util.Map)
     */
    @Override
    public AbstractFromClause getFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getWhereClause(org.protempa.bp.commons.dsb.relationaldb.ColumnSpecInfo, java.util.List, java.util.Set, java.util.Map, java.util.Set, org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy, org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor)
     */
    @Override
    public AbstractWhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Map<ColumnSpec, Integer> referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, SelectClause selectClause) {
        // TODO Auto-generated method stub
        return null;
    }

}
