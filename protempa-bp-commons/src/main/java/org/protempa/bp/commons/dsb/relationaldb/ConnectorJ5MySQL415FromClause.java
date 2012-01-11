package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;


final class ConnectorJ5MySQL415FromClause extends AbstractFromClause {

    ConnectorJ5MySQL415FromClause(EntitySpec currentSpec, List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        super(currentSpec, columnSpecs, referenceIndices);
    }

    @Override
    protected JoinClause getJoinClause(JoinType joinType) {
        return new DefaultJoinClause(joinType);
    }

    @Override
    protected OnClause getOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices) {
        return new DefaultOnClause(joinSpec, referenceIndices);
    }

    @Override
    public String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();

        if (columnSpec.getTable() != null) {
            throw new IllegalArgumentException("schema is not supported");
        }

        fromPart.append(columnSpec.getTable());
        fromPart.append(" ");
        fromPart.append(getReferenceIndices().generateTableReference(columnSpec));
        
        return fromPart.toString();
    }
}
