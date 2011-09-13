package org.protempa.bp.commons.dsb.relationaldb;

final class DefaultOnClause extends AbstractOnClause {

    public DefaultOnClause(int fromIndex, int toIndex, String fromKey,
            String toKey, AbstractSqlStatement stmt) {
        super(fromIndex, toIndex, fromKey, toKey, stmt);
    }

}
