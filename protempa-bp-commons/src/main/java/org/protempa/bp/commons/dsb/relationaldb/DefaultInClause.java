package org.protempa.bp.commons.dsb.relationaldb;

class DefaultInClause extends AbstractInClause {

    public DefaultInClause(int tableNumber, String columnName,
            Object[] elements, boolean not, AbstractSqlStatement stmt) {
        super(tableNumber, columnName, elements, not, stmt);
    }

}
