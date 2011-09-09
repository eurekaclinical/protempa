package org.protempa.bp.commons.dsb.relationaldb;

abstract class JoinClause implements SQLClause {

    private final JoinSpec.JoinType joinType;
    
    JoinClause(JoinSpec.JoinType joinType) {
        this.joinType = joinType;
    }
    
    @Override
    public String generateClause() {
        switch (joinType) {
            case INNER:
                return " join ";
            case LEFT_OUTER:
                return" left outer join ";
            default:
                throw new AssertionError("invalid join type: " + joinType);
        }
    }

}
