package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

final class Ojdbc6OracleStagingFromClause extends Ojdbc6OracleFromClause {

    private final TableAliaser referenceIndices;
    
    public Ojdbc6OracleStagingFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        super(columnSpecs, referenceIndices, stagedTables);
        this.referenceIndices = referenceIndices;
    }

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
}
