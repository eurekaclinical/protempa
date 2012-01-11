package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

final class Ojdbc6OracleStagingFromClause extends Ojdbc6OracleFromClause {

    public Ojdbc6OracleStagingFromClause(EntitySpec currentSpec,
            List<ColumnSpec> columnSpecs, TableAliaser referenceIndices,
            StagingSpec[] stagedTables) {
        super(currentSpec, columnSpecs, referenceIndices, stagedTables);
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
        fromPart.append(getReferenceIndices().generateTableReference(columnSpec));

        return fromPart.toString();
    }
}
