package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractInClause implements InClause {

    private final ColumnSpec columnSpec;
    private final Object[] elements;
    private final boolean not;
    private final TableAliaser referenceIndices;
    
    AbstractInClause(ColumnSpec columnSpec, Object[] elements, boolean not, TableAliaser referenceIndices) {
        this.columnSpec = columnSpec;
        this.elements = elements;
        this.not = not;
        this.referenceIndices = referenceIndices;
    }
    
    @Override
    public String generateClause() {
        StringBuilder result = new StringBuilder();
        
//        result.append(SqlGeneratorUtil.generateColumnReference(stmt, tableNumber, columnName));
        result.append(referenceIndices.generateColumnReference(columnSpec));
        if (not) {
            result.append(" NOT");
        }
        result.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object sqlCode = elements[k];
            result.append(SqlGeneratorUtil.prepareValue(sqlCode));
            if (k + 1 < elements.length) {
                result.append(',');
            }
        }
        result.append(')');
        
        return result.toString();
    }

}
