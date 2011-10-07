package org.protempa.bp.commons.dsb.relationaldb;


final class Ojdbc6OracleInClause extends AbstractInClause {
    
    private final ColumnSpec columnSpec;
    private final Object[] elements;
    private final boolean not;
    private final TableAliaser referenceIndices;
    
    Ojdbc6OracleInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not, TableAliaser referenceIndices) {
        super(columnSpec, elements, not, referenceIndices);
        
        this.columnSpec = columnSpec;
        this.elements = elements;
        this.not = not;
        this.referenceIndices = referenceIndices;
    }

    /**
     * Oracle doesn't allow more than 1000 elements in an IN clause, so if we
     * want more than 1000 we create multiple IN clauses chained together by OR.
     */
    @Override
    public String generateClause() {
        StringBuilder wherePart = new StringBuilder();
        wherePart.append(referenceIndices.generateColumnReference(columnSpec));
        if (not) {
            wherePart.append(" NOT");
        }
        wherePart.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object val = elements[k];
            wherePart.append(SqlGeneratorUtil.prepareValue(val));
            if (k + 1 < elements.length) {
                if ((k + 1) % 1000 == 0) {
                    wherePart.append(") OR ");
                    wherePart.append(referenceIndices.generateColumnReference(columnSpec));
                    wherePart.append(" IN (");
                } else {
                    wherePart.append(',');
                }
            }
        }
        wherePart.append(')');
        
        return wherePart.toString();
    }
}
