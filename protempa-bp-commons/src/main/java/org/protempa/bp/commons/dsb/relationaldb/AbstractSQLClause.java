package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractSQLClause implements SQLClause {

    @Override
    public abstract String generateClause();

    StringBuilder generateTableReference(int tableNumber) {
        return new StringBuilder(" a").append(tableNumber);
    }
    
    StringBuilder generateColumnReference(int tableNumber, String columnName) {
        return generateTableReference(tableNumber).append('.').append(columnName);
    }
}
