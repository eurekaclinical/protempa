package org.protempa.bp.commons.dsb.sqlgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.filter.PropertyValueFilter;

/**
 * Aggregates info for generating the SQL statement.
 *
 * @author Andrew Post
 */
final class ColumnSpecInfoFactory {

    ColumnSpecInfo newInstance(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs,
            Collection<Filter> filters) {
        if (entitySpecs == null) {
            throw new IllegalArgumentException("propertySpecs cannot be null");
        }
        if (entitySpecs.isEmpty()) {
            throw new IllegalArgumentException(
                    "propertySpecs must have at least one entry");
        }
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        columnSpecInfo.setUnique(entitySpec.isUnique());
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        int i = 0;
        for (EntitySpec entitySpec2 : entitySpecs) {
            if (i == 0) {
                i = processBaseSpec(entitySpec2, columnSpecs, i);
            }
            i = processUniqueId(entitySpec, entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            i = processStartTimeOrTimestamp(entitySpec, entitySpec2, 
                    columnSpecs, i, columnSpecInfo);
            i = processFinishTimeSpec(entitySpec, entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            if (entitySpec2 == entitySpec) {
                i = processPropertySpecs(entitySpec2, columnSpecs, i,
                        columnSpecInfo);
            }
            i = processCodeSpec(entitySpec, entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            i = processConstraintSpecs(entitySpec2, columnSpecs, i);
            i = processFilters(entitySpec2, filters, columnSpecs,
                    i);
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private int processUniqueId(EntitySpec entitySpec, EntitySpec entitySpec2,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec[] codeSpecs = entitySpec2.getUniqueIdSpecs();
        if (codeSpecs != null) {
            for (ColumnSpec uniqueIdSpec : codeSpecs)
                i = processConstraintSpec(uniqueIdSpec, columnSpecs, i);
            if (entitySpec == entitySpec2) {
                columnSpecInfo.setUniqueIdIndex(i - 1);
                columnSpecInfo.setNumberOfUniqueIdColumns(codeSpecs.length);
            }
        }
        return i;
    }

    private int processCodeSpec(EntitySpec entitySpec, EntitySpec entitySpec2,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec codeSpec = entitySpec2.getCodeSpec();
        i = processConstraintSpec(codeSpec, columnSpecs, i);
        if (codeSpec != null && entitySpec == entitySpec2) {
            columnSpecInfo.setCodeIndex(i - 1);
        }
        return i;
    }

    private int processConstraintSpecs(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec spec : constraintSpecs) {
            i = processConstraintSpec(spec, columnSpecs, i);
        }
        return i;
    }

    private int processFilters(EntitySpec entitySpec,
            Collection<Filter> filters,
            List<ColumnSpec> columnSpecs, int i) {
        if (filters != null) {
            for (Iterator<Filter> itr = filters.iterator();
                    itr.hasNext();) {
                Filter filter = itr.next();
                if (filter instanceof PropertyValueFilter) {
                    PropertyValueFilter pvf =
                            (PropertyValueFilter) filter;
                    for (PropertySpec propertySpec :
                            entitySpec.getPropertySpecs()) {
                        if (propertySpec.getName().equals(
                                pvf.getProperty())) {
                            List<ColumnSpec> specAsList =
                                    propertySpec.getSpec().asList();
                            columnSpecs.addAll(specAsList);
                            i += specAsList.size();
                        }
                    }
                }
            }
        }
        return i;
    }

    private int processConstraintSpec(ColumnSpec spec,
            List<ColumnSpec> columnSpecs, int i) {
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
        }
        return i;
    }

    private int processPropertySpecs(EntitySpec entitySpec,
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
        if (propertySpecs.length > 0)
            columnSpecInfo.setPropertyIndices(propertyIndices);
        return i;
    }

    private int processFinishTimeSpec(EntitySpec queryEntitySpec,
            EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getFinishTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            if (queryEntitySpec == entitySpec) {
                columnSpecInfo.setFinishTimeIndex(i - 1);
            }
        }
        return i;
    }

    private int processStartTimeOrTimestamp(EntitySpec queryEntitySpec,
            EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getStartTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            if (queryEntitySpec == entitySpec) {
                columnSpecInfo.setStartTimeIndex(i - 1);
            }
        }
        return i;
    }

    private int processBaseSpec(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec spec = entitySpec.getBaseSpec();
        List<ColumnSpec> specAsList = spec.asList();
        columnSpecs.addAll(spec.asList());
        i += specAsList.size();
        return i;
    }
}
