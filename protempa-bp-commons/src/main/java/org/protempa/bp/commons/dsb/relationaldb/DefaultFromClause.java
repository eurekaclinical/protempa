package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;

import org.protempa.bp.commons.dsb.relationaldb.JoinSpec.JoinType;

final class DefaultFromClause extends AbstractFromClause {

    DefaultFromClause(EntitySpec currentSpec, List<ColumnSpec> columnSpecs,
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
    protected String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();
        
        fromPart.append(columnSpec.getSchema());
        fromPart.append(".");
        fromPart.append(columnSpec.getTable());
        fromPart.append(" ");
        fromPart.append(getReferenceIndices().generateTableReference(columnSpec));
        
        return fromPart.toString();
    }

}
