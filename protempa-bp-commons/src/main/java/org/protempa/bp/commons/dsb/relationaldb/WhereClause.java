package org.protempa.bp.commons.dsb.relationaldb;


interface WhereClause extends SqlClause {
    InClause getInClause(ColumnSpec columnSpec, Object[] elements, boolean not);

    OrderByClause getOrderByClause(ColumnSpec startColumnSpec,
            ColumnSpec finishColumnSpec);
}
