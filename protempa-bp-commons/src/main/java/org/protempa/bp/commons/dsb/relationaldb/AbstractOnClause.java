package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractOnClause implements OnClause {

    private final JoinSpec joinSpec;
    private final TableAliaser referenceIndices;

    AbstractOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices) {
        this.joinSpec = joinSpec;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        // return new StringBuilder("on (")
        // .append(SqlGeneratorUtil.generateColumnReference(stmt, fromIndex,
        // fromKey))
        // .append(" = ")
        // .append(SqlGeneratorUtil.generateColumnReference(stmt, toIndex,
        // toKey))
        // .append(") ").toString();

        return new StringBuilder("on (")
                .append(referenceIndices.generateTableReference(joinSpec.getPrevColumnSpec()))
                .append("." + joinSpec.getFromKey())
                .append(" = ")
                .append(referenceIndices.generateTableReference(joinSpec.getNextColumnSpec()))
                .append("." + joinSpec.getToKey())
                .append(") ").toString();
    }

}
