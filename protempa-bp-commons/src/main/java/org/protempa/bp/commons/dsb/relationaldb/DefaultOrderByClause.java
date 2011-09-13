package org.protempa.bp.commons.dsb.relationaldb;

class DefaultOrderByClause extends AbstractOrderByClause {

    public DefaultOrderByClause(int startReferenceIndex, String startColumn,
            int finishReferenceIndex, String finishColumn, SQLOrderBy order,
            AbstractSqlStatement stmt) {
        super(startReferenceIndex, startColumn, finishReferenceIndex,
                finishColumn, order, stmt);
    }

}
