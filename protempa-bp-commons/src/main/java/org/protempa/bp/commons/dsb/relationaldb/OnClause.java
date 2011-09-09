package org.protempa.bp.commons.dsb.relationaldb;

abstract class OnClause implements SQLClause {

    private final int fromIndex;
    private final int toIndex;
    private final String fromKey;
    private final String toKey;
    private final SqlStatement stmt;

    OnClause(int fromIndex, int toIndex, String fromKey, String toKey,
            SqlStatement stmt) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.fromKey = fromKey;
        this.toKey = toKey;
        this.stmt = stmt;
    }

    @Override
    public String generateClause() {
        return new StringBuilder("on (")
                .append(stmt.generateColumnReference(fromIndex, fromKey))
                .append(" = ")
                .append(stmt.generateColumnReference(toIndex, toKey))
                .append(") ").toString();
    }

}
