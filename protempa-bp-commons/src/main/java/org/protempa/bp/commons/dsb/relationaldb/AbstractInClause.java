package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractInClause implements InClause {

    private final int tableNumber;
    private final String columnName;
    private final Object[] elements;
    private final boolean not;
    private final AbstractSqlStatement stmt;
    
    AbstractInClause(int tableNumber, String columnName, Object[] elements, boolean not, AbstractSqlStatement stmt) {
        this.tableNumber = tableNumber;
        this.columnName = columnName;
        this.elements = elements;
        this.not = not;
        this.stmt = stmt;
    }
    
    @Override
    public String generateClause() {
        StringBuilder result = new StringBuilder();
        
        result.append(SqlGeneratorUtil.generateColumnReference(stmt, tableNumber, columnName));
        if (not) {
            result.append(" NOT");
        }
        result.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object sqlCode = elements[k];
            result.append(SqlGeneratorUtil.appendValue(sqlCode));
            if (k + 1 < elements.length) {
                result.append(',');
            }
        }
        result.append(')');
        
        return result.toString();
    }

}
