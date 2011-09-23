package org.protempa.bp.commons.dsb.relationaldb;

class DefaultOrderByClause extends AbstractOrderByClause {

    public DefaultOrderByClause(ColumnSpec startColumnSpec,
            ColumnSpec finishColumnSpec, SQLOrderBy order,
            TableAliaser referenceIndices) {
        super(startColumnSpec, finishColumnSpec, order, referenceIndices);
    }

}
