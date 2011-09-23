package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractJoinClause implements JoinClause {

    private final JoinSpec.JoinType joinType;
    
    AbstractJoinClause(JoinSpec.JoinType joinType) {
        this.joinType = joinType;
    }
    
    @Override
    public String generateClause() {
        switch (joinType) {
            case INNER:
                return " JOIN ";
            case LEFT_OUTER:
                return" LEFT OUTER JOIN ";
            default:
                throw new AssertionError("invalid join type: " + joinType);
        }
    }

}
