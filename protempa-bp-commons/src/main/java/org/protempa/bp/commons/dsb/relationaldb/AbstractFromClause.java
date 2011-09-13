package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractFromClause implements FromClause {
    private final List<ColumnSpec> columnSpecs;
    private final Map<ColumnSpec, Integer> referenceIndices;
    private final AbstractSqlStatement stmt;

    protected AbstractFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices, AbstractSqlStatement stmt) {
        this.columnSpecs = Collections.unmodifiableList(columnSpecs);
        this.referenceIndices = Collections.unmodifiableMap(referenceIndices);
        this.stmt = stmt;
    }

    protected abstract AbstractJoinClause getJoinClause(JoinSpec.JoinType joinType);

    protected abstract AbstractOnClause getOnClause(int fromIndex, int toIndex,
            String fromKey, String toKey);

    public String generateClause() {
        Map<Integer, ColumnSpec> columnSpecCache = new HashMap<Integer, ColumnSpec>();
        StringBuilder fromPart = new StringBuilder();
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);

            JoinSpec currentJoin = null;

            /*
             * To find something to join to, first we see if there is a join to
             * it.
             */
            for (int k = j - 1; k >= 0; k--) {
                ColumnSpec prevColumnSpec = columnSpecs.get(k);
                JoinSpec js = prevColumnSpec.getJoin();
                if (js != null && js.getNextColumnSpec() == columnSpec) {
                    currentJoin = js;
                    break;
                }
            }

            /*
             * Next, if there is not a join, we see if there is a join specified
             * to another column spec with the same schema and table as this
             * one.
             */
            if (currentJoin == null) {
                for (int k = 0; k < j; k++) {
                    ColumnSpec prevColumnSpec = columnSpecs.get(k);
                    JoinSpec js = prevColumnSpec.getJoin();
                    if (js != null
                            && js.getNextColumnSpec().isSameSchemaAndTable(
                                    columnSpec)) {
                        currentJoin = js;
                        break;
                    }
                }
            }

            Integer i = referenceIndices.get(columnSpec);
            if (i != null && !columnSpecCache.containsKey(i)) {
                assert begin || currentJoin != null : "No 'on' clause can be generated for "
                        + columnSpec + " because there is no incoming join.";
                String schema = columnSpec.getSchema();
                String table = columnSpec.getTable();
                if (!begin) {
                    fromPart.append(getJoinClause(currentJoin.getJoinType())
                            .generateClause());
                }
                fromPart.append(generateFromTable(schema, table, i));
                fromPart.append(' ');
                columnSpecCache.put(i, columnSpec);

                if (currentJoin != null) {
                    int fromIndex = referenceIndices.get(currentJoin
                            .getPrevColumnSpec());
                    int toIndex = referenceIndices.get(currentJoin
                            .getNextColumnSpec());
                    fromPart.append(getOnClause(fromIndex, toIndex,
                            currentJoin.getFromKey(), currentJoin.getToKey()).generateClause());
                }
                begin = false;
            }
        }
        return fromPart.toString();
    }

    protected abstract String generateFromTable(String schema, String table,
            int i);

}
