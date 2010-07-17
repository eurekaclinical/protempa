package org.protempa.bp.commons.dsb.sqlgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andrew Post
 */
class ColumnSpecInfoFactory {

    ColumnSpecInfo newInstance(
            Map<PropertySpec, List<String>> propertySpecToPropIdMap) {
        if (propertySpecToPropIdMap == null)
            throw new IllegalArgumentException(
                    "propertySpecToPropIdMap cannot be null");
        if (propertySpecToPropIdMap.isEmpty())
            throw new IllegalArgumentException(
                    "propertySpecToPropIdMap must have at least one entry");
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        int i = 0;
        for (Iterator<PropertySpec> itr = 
                propertySpecToPropIdMap.keySet().iterator();
                itr.hasNext();) {
            PropertySpec propertySpec = itr.next();
            if (i == 0)
                i = processKeySpec(propertySpec, columnSpecs, i);
            i = processStartTimeOrTimestamp(propertySpec, columnSpecs, i,
                    columnSpecInfo);
            i = processFinishTimeSpec(propertySpec, columnSpecs, i,
                    columnSpecInfo);
            i = processPropertyValueSpecs(propertySpec, columnSpecs, i,
                    columnSpecInfo);
            i = processCodeSpec(propertySpec, columnSpecs, i, columnSpecInfo);
            i = processConstraintSpecs(propertySpec, columnSpecs, i,
                    columnSpecInfo);
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private int processCodeSpec(PropertySpec propertySpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec codeSpec = propertySpec.getCodeSpec();
        i = processConstraintSpec(codeSpec, columnSpecs, i);
        columnSpecInfo.setCodeIndex(i - 1);
        return i;
    }

    private int processConstraintSpecs(PropertySpec propertySpec,
            List<ColumnSpec> columnSpecs, int i, 
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec[] constraintSpecs = propertySpec.getConstraintSpecs();
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

    private int processPropertyValueSpecs(PropertySpec propositionSpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        Map<String, ColumnSpec> propertyValueSpecs =
                propositionSpec.getPropertyValueSpecs();
        if (propertyValueSpecs != null) {
            Map<String, Integer> propertyValueIndices =
                    new HashMap<String, Integer>();
            for (Map.Entry<String, ColumnSpec> e :
                propertyValueSpecs.entrySet()) {
                ColumnSpec spec = e.getValue();
                if (spec != null) {
                    List<ColumnSpec> specAsList = spec.asList();
                    columnSpecs.addAll(specAsList);
                    i += specAsList.size();
                    propertyValueIndices.put(e.getKey(), i - 1);
                }
            }
            columnSpecInfo.setPropertyValueIndices(propertyValueIndices);
        }
        return i;
    }

    private int processFinishTimeSpec(PropertySpec propositionSpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = propositionSpec.getFinishTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            columnSpecInfo.setFinishTimeIndex(i - 1);
        }
        return i;
    }

    private int processStartTimeOrTimestamp(PropertySpec propositionSpec,
            List<ColumnSpec> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = propositionSpec.getStartTimeSpec();
        if (spec != null) {
            List<ColumnSpec> specAsList = spec.asList();
            columnSpecs.addAll(specAsList);
            i += specAsList.size();
            columnSpecInfo.setStartTimeIndex(i - 1);
        }
        return i;
    }

    private int processKeySpec(PropertySpec propositionSpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec spec = propositionSpec.getEntitySpec().getKeySpec();
        List<ColumnSpec> specAsList = spec.asList();
        columnSpecs.addAll(spec.asList());
        i += specAsList.size();
        return i;
    }

    
}
