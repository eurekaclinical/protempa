package org.protempa.bp.commons.dsb.relationaldb;


abstract class AbstractSqlStatement implements SqlStatement {
    
    public String generateTableReference(int tableNumber) {
        return " a" + tableNumber;
    }
}
