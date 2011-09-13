package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

abstract class AbstractSqlStatement implements SqlStatement {
    
    public String generateTableReference(int tableNumber) {
        return " a" + tableNumber;
    }
}
