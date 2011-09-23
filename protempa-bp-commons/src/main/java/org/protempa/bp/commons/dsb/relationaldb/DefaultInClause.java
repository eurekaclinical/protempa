package org.protempa.bp.commons.dsb.relationaldb;

class DefaultInClause extends AbstractInClause {

    public DefaultInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not, TableAliaser referenceIndices) {
        super(columnSpec, elements, not, referenceIndices);
    }

}
