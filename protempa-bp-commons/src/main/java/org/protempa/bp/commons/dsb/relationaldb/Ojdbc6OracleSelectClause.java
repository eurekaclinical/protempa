package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

final class Ojdbc6OracleSelectClause extends SelectClause {
    Ojdbc6OracleSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        super(info, referenceIndices, entitySpec);
    }
}
