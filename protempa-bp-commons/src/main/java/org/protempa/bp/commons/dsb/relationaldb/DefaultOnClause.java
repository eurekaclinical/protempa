package org.protempa.bp.commons.dsb.relationaldb;

final class DefaultOnClause extends OnClause {

    public DefaultOnClause(int fromIndex, int toIndex, String fromKey,
            String toKey, SqlStatement stmt) {
        super(fromIndex, toIndex, fromKey, toKey, stmt);
    }

}
