package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;

final class Ojdbc6OracleFromClause extends AbstractFromClause {

    private final TableAliaser referenceIndices;

    Ojdbc6OracleFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        super(columnSpecs, referenceIndices);
        this.referenceIndices = referenceIndices;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.FromClause#generateFromTable
     * (java.lang.String, java.lang.String, int)
     */
    @Override
    protected String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();

        if (columnSpec.getSchema() != null) {
            fromPart.append(columnSpec.getSchema());
            fromPart.append('.');
        }

        fromPart.append(columnSpec.getTable());
        fromPart.append(" ");
        fromPart.append(referenceIndices.generateTableReference(columnSpec));

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
    protected AbstractJoinClause getJoinClause(JoinType joinType) {
        return new DefaultJoinClause(joinType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.bp.commons.dsb.relationaldb.FromClause#getOnClause(int,
     * int, java.lang.String, java.lang.String)
     */
    @Override
    protected AbstractOnClause getOnClause(JoinSpec joinSpec, TableAliaser referenceIndices) {
        return new DefaultOnClause(joinSpec, referenceIndices);
    }

}
