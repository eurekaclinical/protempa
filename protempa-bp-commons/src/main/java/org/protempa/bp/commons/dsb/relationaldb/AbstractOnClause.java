package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractOnClause implements OnClause {

    private final int fromIndex;
    private final int toIndex;
    private final String fromKey;
    private final String toKey;
    private final SqlStatement stmt;

    AbstractOnClause(int fromIndex, int toIndex, String fromKey, String toKey,
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
                .append(SqlGeneratorUtil.generateColumnReference(stmt, fromIndex, fromKey))
                .append(" = ")
                .append(SqlGeneratorUtil.generateColumnReference(stmt, toIndex, toKey))
                .append(") ").toString();
    }

}
