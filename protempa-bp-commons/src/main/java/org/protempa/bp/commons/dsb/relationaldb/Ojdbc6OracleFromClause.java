package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;

final class Ojdbc6OracleFromClause extends FromClause {

    private final SqlStatement stmt;

    Ojdbc6OracleFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices, SqlStatement stmt) {
        super(columnSpecs, referenceIndices, stmt);
        this.stmt = stmt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.FromClause#generateFromTable
     * (java.lang.String, java.lang.String, int)
     */
    @Override
    protected String generateFromTable(String schema, String table, int i) {
        StringBuilder fromPart = new StringBuilder();

        if (schema != null) {
            fromPart.append(schema);
            fromPart.append('.');
        }

        fromPart.append(table).append(" a").append(i);

        return fromPart.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.FromClause#getJoinClause(org
     * .protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType)
     */
    @Override
    protected JoinClause getJoinClause(JoinType joinType) {
        return new DefaultJoinClause(joinType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.bp.commons.dsb.relationaldb.FromClause#getOnClause(int,
     * int, java.lang.String, java.lang.String)
     */
    @Override
    protected OnClause getOnClause(int fromIndex, int toIndex, String fromKey,
            String toKey) {
        return new DefaultOnClause(fromIndex, toIndex, fromKey, toKey,
                this.stmt);
    }

}
