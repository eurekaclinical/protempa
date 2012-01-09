package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;

class Ojdbc6OracleFromClause extends AbstractFromClause {

    private final TableAliaser referenceIndices;
    private final StagingSpec[] stagedTables;

    Ojdbc6OracleFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        super(columnSpecs, referenceIndices);
        this.referenceIndices = referenceIndices;
        this.stagedTables = stagedTables;
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
            if (stagedTables != null) {
                for (StagingSpec sspec : stagedTables) {
                    for (TableSpec tspec : sspec.getReplacedTables()) {
                        if (tspec.getSchema().equals(columnSpec.getSchema())
                                && tspec.getTable().equals(
                                        columnSpec.getTable())) {
                            fromPart.append(sspec.getStagingArea().getSchema());
                        }
                    }
                }
            } else {
                fromPart.append(columnSpec.getSchema());
            }
            fromPart.append('.');
        }

        if (stagedTables != null) {
            for (StagingSpec sspec : stagedTables) {
                for (TableSpec tspec : sspec.getReplacedTables()) {
                    if (tspec.getSchema().equals(columnSpec.getSchema())
                            && tspec.getTable().equals(columnSpec.getTable())) {
                        fromPart.append(sspec.getStagingArea().getTable());
                    }
                }
            }
        } else {
            fromPart.append(columnSpec.getTable());
        }
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
    protected AbstractOnClause getOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices) {
        return new DefaultOnClause(joinSpec, referenceIndices);
    }

}
