package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractOrderByClause implements OrderByClause {

    private final int startReferenceIndex;
    private final int finishReferenceIndex;
    private final String startColumn;
    private final String finishColumn;
    private final SQLOrderBy order;
    private final AbstractSqlStatement stmt;

    AbstractOrderByClause(int startReferenceIndex, String startColumn,
            int finishReferenceIndex, String finishColumn, SQLOrderBy order,
            AbstractSqlStatement stmt) {
        this.startReferenceIndex = startReferenceIndex;
        this.finishReferenceIndex = finishReferenceIndex;
        this.startColumn = startColumn;
        this.finishColumn = finishColumn;
        this.order = order;
        this.stmt = stmt;
    }

    @Override
    public String generateClause() {
        StringBuilder clause = new StringBuilder(" order by ");
        clause.append(SqlGeneratorUtil.generateColumnReference(stmt, startReferenceIndex,
                startColumn));
        if (finishReferenceIndex > 0) {
            clause.append(',');
            clause.append(SqlGeneratorUtil.generateColumnReference(stmt, 
                    finishReferenceIndex, finishColumn));
        }
        clause.append(' ');
        if (order == SQLOrderBy.ASCENDING) {
            clause.append("ASC");
        } else {
            clause.append("DESC");
        }

        return clause.toString();
    }

}
