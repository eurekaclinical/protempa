package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

final class Ojdbc6OracleSelectClause extends AbstractSelectClause {
    Ojdbc6OracleSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec, AbstractSqlStatement stmt) {
        super(info, referenceIndices, entitySpec, stmt);
    }
}
