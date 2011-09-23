package org.protempa.bp.commons.dsb.relationaldb;

import static org.arp.javautil.arrays.Arrays.asSet;
import static org.arp.javautil.collections.Collections.containsAny;

import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;

final class TimeSpecProcessor {

    static String processStartTimeSpec(
            EntitySpec entitySpec, Set<Filter> filters, boolean first,
            TableAliaser referenceIndices) {
        return new StartTimeSpecProcessor(entitySpec, filters, first,
                referenceIndices).process();
    }

    static String processFinishTimeSpec(
            EntitySpec entitySpec, Set<Filter> filters, boolean first,
            TableAliaser referenceIndices) {
        return new FinishTimeSpecProcessor(entitySpec, filters, first,
                referenceIndices).process();
    }

    private static abstract class AbstractTimeSpecProcessor {

        private final EntitySpec entitySpec;
        private final Set<Filter> filters;
        private final boolean first;
        private final TableAliaser referenceIndices;

        protected AbstractTimeSpecProcessor(EntitySpec entitySpec, Set<Filter> filters,
                boolean first, TableAliaser referenceIndices) {
            this.entitySpec = entitySpec;
            this.filters = filters;
            this.first = first;
            this.referenceIndices = referenceIndices;
        }
        
        protected EntitySpec getEntitySpec() {
            return this.entitySpec;
        }

        protected abstract ColumnSpec getTimeSpec();

        protected abstract boolean outputStart(PositionFilter filter);

        protected abstract boolean outputFinish(PositionFilter filter);

        String process() {
            StringBuilder wherePart = new StringBuilder();
            ColumnSpec timeSpec = getTimeSpec();
            if (timeSpec != null) {
                while (true) {
                    if (timeSpec.getJoin() != null) {
                        timeSpec = timeSpec.getJoin().getNextColumnSpec();
                    } else {
                        for (Filter filter : filters) {
                            if (filter instanceof PositionFilter) {
                                Set<String> entitySpecPropIds = asSet(entitySpec
                                        .getPropositionIds());
                                if (containsAny(entitySpecPropIds,
                                        filter.getPropositionIds())) {
                                    PositionFilter pdsc2 = (PositionFilter) filter;

                                    boolean outputStart = outputStart(pdsc2);
                                    boolean outputFinish = outputFinish(pdsc2);

                                    if (outputStart) {
                                        if (!first) {
                                            wherePart.append(" AND ");
                                        }
                                        wherePart
                                                .append(referenceIndices
                                                        .generateColumnReferenceWithOp(timeSpec));
                                        wherePart.append(" >= ");
                                        wherePart
                                                .append(entitySpec
                                                        .getPositionParser()
                                                        .format(pdsc2
                                                                .getMinimumStart()));
                                    }
                                    if (outputFinish) {
                                        if (!first || outputStart) {
                                            wherePart.append(" AND ");
                                        }
                                        wherePart
                                                .append(referenceIndices
                                                        .generateColumnReferenceWithOp(timeSpec));
                                        wherePart.append(" <= ");
                                        wherePart.append(entitySpec
                                                .getPositionParser()
                                                .format(pdsc2
                                                        .getMaximumFinish()));
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
            return wherePart.toString();
        }
    }

    private static class StartTimeSpecProcessor extends AbstractTimeSpecProcessor {

        StartTimeSpecProcessor(EntitySpec entitySpec, Set<Filter> filters,
                boolean first, TableAliaser referenceIndices) {
            super(entitySpec, filters, first, referenceIndices);
        }

        @Override
        protected ColumnSpec getTimeSpec() {
            return getEntitySpec().getStartTimeSpec();
        }

        @Override
        protected boolean outputStart(PositionFilter filter) {
            return filter.getMinimumStart() != null
                    && (filter.getStartSide() == Side.START || getEntitySpec()
                            .getFinishTimeSpec() == null);
        }

        @Override
        protected boolean outputFinish(PositionFilter filter) {
            return filter.getMaximumFinish() != null
                    && (filter.getFinishSide() == Side.START || getEntitySpec()
                            .getFinishTimeSpec() == null);
        }
    }

    private static class FinishTimeSpecProcessor extends AbstractTimeSpecProcessor {

        FinishTimeSpecProcessor(EntitySpec entitySpec, Set<Filter> filters,
                boolean first, TableAliaser referenceIndices) {
            super(entitySpec, filters, first, referenceIndices);
        }

        @Override
        protected ColumnSpec getTimeSpec() {
            return getEntitySpec().getFinishTimeSpec();
        }

        @Override
        protected boolean outputStart(PositionFilter filter) {
            return filter.getMinimumStart() != null
                    && filter.getStartSide() == Side.FINISH;
        }

        @Override
        protected boolean outputFinish(PositionFilter filter) {
            return filter.getMaximumFinish() != null
                    && filter.getFinishSide() == Side.FINISH;
        }
    }
}
