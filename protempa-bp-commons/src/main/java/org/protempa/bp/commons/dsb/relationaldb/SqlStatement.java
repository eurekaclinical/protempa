package org.protempa.bp.commons.dsb.relationaldb;

abstract class SqlStatement {
    String generateTableReference(int tableNumber) {
        return " a" + tableNumber;
    }

    String generateColumnReference(int tableNumber, String columnName) {
        return generateTableReference(tableNumber) + "." + columnName;
    }
}
