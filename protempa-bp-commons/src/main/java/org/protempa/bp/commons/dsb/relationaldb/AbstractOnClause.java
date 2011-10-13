package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractOnClause implements OnClause {

    private final JoinSpec joinSpec;
    private final TableAliaser referenceIndices;

    AbstractOnClause(JoinSpec joinSpec, TableAliaser referenceIndices) {
        this.joinSpec = joinSpec;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        return new StringBuilder("ON (")
                .append(referenceIndices.generateTableReference(joinSpec
                        .getPrevColumnSpec()))
                .append("." + joinSpec.getFromKey())
                .append(" = ")
                .append(referenceIndices.generateTableReference(joinSpec
                        .getNextColumnSpec()))
                .append("." + joinSpec.getToKey()).append(") ").toString();
    }

}
