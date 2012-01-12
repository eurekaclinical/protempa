package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;

class Ojdbc6OracleFromClause extends AbstractFromClause {

    private final StagingSpec[] stagedTables;

    Ojdbc6OracleFromClause(EntitySpec currentSpec,
            List<ColumnSpec> columnSpecs, TableAliaser referenceIndices,
            StagingSpec[] stagedTables) {
        super(currentSpec, columnSpecs, referenceIndices);
        this.stagedTables = stagedTables;
    }

    protected StagingSpec[] getStagedTables() {
        return stagedTables;
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
        boolean foundStagedTable = false;
        String schemaToAppend = "";
        String tableToAppend = "";

        if (stagedTables != null) {
            for (StagingSpec sspec : stagedTables) {
                if (!foundStagedTable
//                        && noPropIdsMatch(getCurrentSpec(), sspec.getEntitySpec())
                        && columnSpec.isSameSchemaAndTable(sspec
                                .getReplacedTable())) {
                    foundStagedTable = true;
                    if (sspec.getStagingArea().getSchema() != null) {
                        schemaToAppend = sspec.getStagingArea().getSchema();
                    }
                    tableToAppend = sspec.getStagingArea().getTable();
                }
            }
        }
        if (!foundStagedTable) {
            if (columnSpec.getSchema() != null) {
                schemaToAppend = columnSpec.getSchema();
            }
            tableToAppend = columnSpec.getTable();
        }

        fromPart.append(schemaToAppend);
        fromPart.append('.');
        fromPart.append(tableToAppend);
        fromPart.append(" ");
        fromPart.append(getReferenceIndices()
                .generateTableReference(columnSpec));

        return fromPart.toString();
    }

    private static boolean noPropIdsMatch(EntitySpec es1, EntitySpec es2) {
        return !SQLGenUtil.somePropIdsMatch(es1, es2);
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
