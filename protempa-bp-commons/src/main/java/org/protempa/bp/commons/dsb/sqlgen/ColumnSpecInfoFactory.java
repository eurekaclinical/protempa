package org.protempa.bp.commons.dsb.sqlgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.filter.PropertyValueFilter;

/**
 * Aggregates info for generating the SQL statement.
 *
 * @author Andrew Post
 */
final class ColumnSpecInfoFactory {

    ColumnSpecInfo newInstance(Set<String> propIds, EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs,
            Collection<Filter> filters, ReferenceSpec referenceSpec) {
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        if (referenceSpec == null) {
            columnSpecInfo.setUsingKeyIdIndex(true);
        }
        EntitySpec refEntitySpec = null;
        if (referenceSpec != null) {
            refEntitySpec = findRefEntitySpec(entitySpecs, referenceSpec);
            columnSpecInfo.setUnique(refEntitySpec.isUnique());
        } else {
            columnSpecInfo.setUnique(entitySpec.isUnique());
        }
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        int i = 0;
        for (EntitySpec entitySpec2 : entitySpecs) {
            i = processBaseSpec(entitySpec2, columnSpecs, i);
            i = processUniqueIds(entitySpec, entitySpec2, columnSpecs, i,
                    columnSpecInfo, referenceSpec);
            i = processStartTimeOrTimestamp(entitySpec, entitySpec2,
                    columnSpecs, i, columnSpecInfo, referenceSpec);
            i = processFinishTimeSpec(entitySpec, entitySpec2, columnSpecs,
                    i, columnSpecInfo, referenceSpec);
            if (entitySpec2 == entitySpec && referenceSpec == null) {
                i = processPropertyAndValueSpecs(entitySpec2, columnSpecs, i,
                        columnSpecInfo);
            }
            i = processCodeSpec(propIds, entitySpec, entitySpec2, columnSpecs, i,
                    columnSpecInfo, referenceSpec);
            i = processConstraintSpecs(entitySpec2, columnSpecs, i);
            i = processFilters(entitySpec2, filters, columnSpecs,
                    i);
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private static EntitySpec findRefEntitySpec(
            Collection<EntitySpec> entitySpecs, ReferenceSpec referenceSpec) {
        for (EntitySpec es : entitySpecs) {
            if (es.getName().equals(referenceSpec.getEntityName())) {
                return es;
            }
        }
        throw new AssertionError("invalid entity spec name in reference spec "
                + referenceSpec);
    }

    private static int processUniqueIds(EntitySpec entitySpec,
            EntitySpec entitySpec2, List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec[] codeSpecs = entitySpec.getUniqueIdSpecs();
        ColumnSpec[] refSpecs = null;
        if (referenceSpec != null) {
            refSpecs = referenceSpec.getUniqueIdSpecs();
        }
        int numUniqueIndices = codeSpecs.length;
        if (refSpecs != null) {
            numUniqueIndices += refSpecs.length;
        }
        int[] uniqueIndices = null;
        if (entitySpec == entitySpec2 || referenceSpec != null) {
            uniqueIndices = new int[numUniqueIndices];
        }
        int j = 0;
        if (codeSpecs != null && uniqueIndices != null) {
            for (ColumnSpec uniqueIdSpec : codeSpecs) {
                i = processColumnSpec(uniqueIdSpec, columnSpecs, i);
                uniqueIndices[j++] = i - 1;
            }
        }
        if (refSpecs != null && uniqueIndices != null) {
            for (ColumnSpec uniqueIdSpec : refSpecs) {
                i = processColumnSpec(uniqueIdSpec, columnSpecs, i);
                uniqueIndices[j++] = i - 1;
            }
        }
        if (uniqueIndices != null) {
            columnSpecInfo.setUniqueIdIndices(uniqueIndices);
        }
        return i;
    }

    private static int processCodeSpec(Set<String> propIds, EntitySpec entitySpec,
            EntitySpec entitySpec2, List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec codeSpec = entitySpec2.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> specAsList = codeSpec.asList();
            int specAsListSize = specAsList.size();
            if (referenceSpec == null
                    || !(specAsList.get(specAsListSize - 1).isPropositionIdsComplete()
                    && AbstractSQLGenerator.completeOrNoOverlap(propIds,
                    entitySpec2.getPropositionIds()))) {
                columnSpecs.addAll(specAsList);
                i += specAsListSize;
            } else {
                codeSpec = null;
            }
        }
        if (codeSpec != null && entitySpec == entitySpec2
                && referenceSpec == null) {
            columnSpecInfo.setCodeIndex(i - 1);
        }
        return i;
    }

    private static int processConstraintSpecs(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec spec : constraintSpecs) {
            i = processColumnSpec(spec, columnSpecs, i);
        }
        return i;
    }

    private static int processFilters(EntitySpec entitySpec,
            Collection<Filter> filters,
            List<ColumnSpec> columnSpecs, int i) {
        for (Filter filter : filters) {
            if (filter instanceof PropertyValueFilter) {
                PropertyValueFilter pvf = (PropertyValueFilter) filter;
                for (PropertySpec propertySpec :
                        entitySpec.getPropertySpecs()) {
                    if (propertySpec.getName().equals(pvf.getProperty())) {
                        List<ColumnSpec> specAsList =
                                propertySpec.getSpec().asList();
                        columnSpecs.addAll(specAsList);
                        i += specAsList.size();
                    }
                }
            }
        }
        return i;
    }

    private static int processColumnSpec(ColumnSpec spec,
            List<ColumnSpec> columnSpecs, int i) {
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
        }
        return i;
    }

    private static int processPropertyAndValueSpecs(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Map<String, Integer> propertyIndices =
                new HashMap<String, Integer>();
        for (PropertySpec propertySpec : propertySpecs) {
            ColumnSpec spec = propertySpec.getSpec();
            if (spec != null) {
                List<ColumnSpec> specAsList = spec.asList();
                columnSpecs.addAll(specAsList);
                i += specAsList.size();
                propertyIndices.put(propertySpec.getName(), i - 1);
            }
        }
        if (propertySpecs.length > 0) {
            columnSpecInfo.setPropertyIndices(propertyIndices);
        }

        ColumnSpec valueSpec = entitySpec.getValueSpec();
        if (valueSpec != null) {
            List<ColumnSpec> specAsList = valueSpec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            columnSpecInfo.setValueIndex(i - 1);
        }

        return i;
    }

    private static int processFinishTimeSpec(EntitySpec queryEntitySpec,
            EntitySpec entitySpec, List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec spec = entitySpec.getFinishTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            if (queryEntitySpec == entitySpec && referenceSpec == null) {
                columnSpecInfo.setFinishTimeIndex(i - 1);
            }
        }
        return i;
    }

    private static int processStartTimeOrTimestamp(EntitySpec queryEntitySpec,
            EntitySpec entitySpec, List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec spec = entitySpec.getStartTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            if (queryEntitySpec == entitySpec && referenceSpec == null) {
                columnSpecInfo.setStartTimeIndex(i - 1);
            }
        }
        return i;
    }

    private static int processBaseSpec(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec spec = entitySpec.getBaseSpec();
        List<ColumnSpec> specAsList = spec.asList();
        /*
         * We assume that the first column spec of the base spec is the
         * patient id/key.
         */
        int specAsListSize = specAsList.size();
        //if (referenceSpec == null || !keyIds.isEmpty()) {
            columnSpecs.addAll(specAsList);
            i += specAsListSize;
        //} //else {
        //    columnSpecs.addAll(specAsList.subList(1, specAsListSize));
        //    i += specAsListSize - 1;
        //}
        
        return i;
    }
}
