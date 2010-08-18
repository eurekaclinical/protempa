package org.protempa.bp.commons.dsb.sqlgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andrew Post
 */
final class ColumnSpecInfoFactory {

    ColumnSpecInfo newInstance(
            EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs) {
        if (entitySpecs == null)
            throw new IllegalArgumentException("propertySpecs cannot be null");
        if (entitySpecs.isEmpty())
            throw new IllegalArgumentException(
                    "propertySpecs must have at least one entry");
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        columnSpecInfo.setUnique(entitySpec.isUnique());
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        int i = 0;
        for (EntitySpec entitySpec2 : entitySpecs) {
            if (i == 0)
                i = processBaseSpec(entitySpec2, columnSpecs, i,
                        columnSpecInfo);
            i = processStartTimeOrTimestamp(entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            i = processFinishTimeSpec(entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            if (entitySpec2 == entitySpec)
                i = processPropertySpecs(entitySpec2, columnSpecs, i,
                    columnSpecInfo);
            i = processCodeSpec(entitySpec2, columnSpecs, i, columnSpecInfo);
            i = processConstraintSpecs(entitySpec2, columnSpecs, i);
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private int processCodeSpec(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
        ColumnSpecInfo columnSpecInfo) {
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        i = processConstraintSpec(codeSpec, columnSpecs, i);
        if (codeSpec != null)
            columnSpecInfo.setCodeIndex(i - 1);
        return i;
    }

    private int processConstraintSpecs(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        if (constraintSpecs != null) {
            for (ColumnSpec spec : constraintSpecs) {
                i = processConstraintSpec(spec, columnSpecs, i);
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
        if (propertySpecs != null) {
            Map<String, Integer> propertyIndices =
                    new HashMap<String, Integer>();
            for (PropertySpec propertySpec : propertySpecs) {
                ColumnSpec spec = propertySpec.getCodeSpec();
                if (spec != null) {
                    List<ColumnSpec> specAsList = spec.asList();
                    columnSpecs.addAll(specAsList);
                    i += specAsList.size();
                    propertyIndices.put(propertySpec.getName(), i - 1);
                }
            }
            columnSpecInfo.setPropertyIndices(propertyIndices);
        }
        return i;
    }

    private int processFinishTimeSpec(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getFinishTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            columnSpecInfo.setFinishTimeIndex(i - 1);
        }
        return i;
    }

    private int processStartTimeOrTimestamp(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getStartTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            columnSpecInfo.setStartTimeIndex(i - 1);
        }
        return i;
    }

    private int processBaseSpec(EntitySpec entitySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getBaseSpec();
        List<ColumnSpec> specAsList = spec.asList();
        columnSpecs.addAll(spec.asList());
        i += specAsList.size();
        return i;
    }

    
}
