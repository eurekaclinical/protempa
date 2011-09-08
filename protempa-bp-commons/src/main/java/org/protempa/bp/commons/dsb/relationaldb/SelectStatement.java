package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

abstract class SelectStatement {
    private SelectClause select;
    private FromClause from;
    private WhereClause where;

    private final EntitySpec entitySpec;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;

    protected SelectStatement(EntitySpec entitySpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        this.entitySpec = entitySpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.propIds = Collections.unmodifiableSet(propIds);
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
    }

    protected abstract SelectClause getSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec);

    public String generate() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(propIds,
                entitySpec, entitySpecs, filters, referenceSpec);
        Map<ColumnSpec, Integer> referenceIndices = computeReferenceIndices(info
                .getColumnSpecs());

        SelectClause select = new SelectClause(info, referenceIndices,
                this.entitySpec);
        FromClause from = new FromClause(info.getColumnSpecs(),
                referenceIndices);
        WhereClause where = new WhereClause(info, this.entitySpecs,
                this.filters, referenceIndices, this.keyIds, this.order,
                this.resultProcessor);

        StringBuilder result = new StringBuilder(select.generateClause())
                .append(" ").append(from.generateClause()).append(" ")
                .append(where.generateClause());

        return result.toString();
    }

    private static int findPreviousInstance(int i, int j,
            List<ColumnSpec> columnSpecs, ColumnSpec columnSpec) {
        for (; i < j; i++) {
            ColumnSpec columnSpec2 = columnSpecs.get(i);
            if (columnSpec.isSameSchemaAndTable(columnSpec2)) {
                return i;
            }
        }
        return -1;
    }

    private static Map<ColumnSpec, Integer> computeReferenceIndices(
            List<ColumnSpec> columnSpecs) {
        Map<ColumnSpec, Integer> result = new HashMap<ColumnSpec, Integer>();

        int index = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);
            /*
             * Only generate a table if we're the first table or there is an
             * inbound join.
             */
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                int previousInstanceIndex = -1;
                // if there's no inbound join, then don't try to reuse an
                // earlier instance.
                if (currentJoin == null/* || columnSpec.getJoin() != null */) {
                    previousInstanceIndex = findPreviousInstance(0, j,
                            columnSpecs, columnSpec);
                    // System.out.println("previousInstanceIndex 1: " +
                    // previousInstanceIndex);
                } else {
                    // If there's an inbound join and an earlier instance, then
                    // use an earlier version only if the inbound join of the
                    // earlier
                    // instance is the same
                    int startIndex = 0;
                    int cs2i = -1;
                    do {
                        cs2i = findPreviousInstance(startIndex, j, columnSpecs,
                                columnSpec);
                        startIndex = cs2i + 1;
                        if (cs2i >= 0) {
                            // System.out.println("found previous instance at "
                            // + cs2i);
                            for (int k = 0; k < cs2i; k++) {
                                ColumnSpec csPrev = columnSpecs.get(k);
                                JoinSpec prevJoin = csPrev.getJoin();
                                if (currentJoin.isSameJoin(prevJoin)) {
                                    previousInstanceIndex = cs2i;
                                    // System.out.println("setting previousIstanceIndex="
                                    // + previousInstanceIndex);
                                }
                            }
                        }
                    } while (cs2i >= 0);
                    // System.out.println("previousInstanceIndex 2: " +
                    // previousInstanceIndex);
                }
                // If we found an earlier instance, then use its index otherwise
                // assign it a new index.
                if (previousInstanceIndex >= 0) {
                    ColumnSpec previousInstance = columnSpecs
                            .get(previousInstanceIndex);
                    assert result.containsKey(previousInstance) : "doesn't contain columnSpec "
                            + previousInstance;
                    int prevIndex = result.get(previousInstance);
                    result.put(columnSpec, prevIndex);
                    // System.err.println("assigning " + columnSpec.getTable() +
                    // " to  previous index " + prevIndex);
                } else {
                    result.put(columnSpec, index++);
                    // System.err.println("assigning " + columnSpec.getTable() +
                    // " to " + (index - 1));
                }
                begin = false;
            }

            if (columnSpec.getJoin() != null) {
                currentJoin = columnSpec.getJoin();
            } else {
                currentJoin = null;
                begin = true;
            }
        }
        return result;
    }
}
