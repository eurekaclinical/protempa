package org.protempa.bp.commons.dsb.sqlgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Andrew Post
 */
class ColumnSpecInfoFactory {

    ColumnSpecInfo newInstance(
            Map<PropertySpec, List<String>> specs) {
        if (specs == null)
            throw new IllegalArgumentException("specs cannot be null");
        if (specs.isEmpty())
            throw new IllegalArgumentException(
                    "specs must have at least one entry");
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        Set<PropertySpec> propSpecKeys = specs.keySet();
        Iterator<PropertySpec> psItr = propSpecKeys.iterator();
        PropertySpec propositionSpec = psItr.next();
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        int i = 0;
        
        while (true) {
            i = processKeySpec(propositionSpec, columnSpecs, i);
            i = processStartTimeOrTimestamp(propositionSpec, columnSpecs, i,
                    columnSpecInfo);
            i = processFinishTimeSpec(propositionSpec, columnSpecs, i,
                    columnSpecInfo);
            i = processPropertyValueSpecs(propositionSpec, columnSpecs, i, 
                    columnSpecInfo);
            i = processConstraintSpecs(propositionSpec, columnSpecs, i);

            if (psItr.hasNext()) {
                propositionSpec = psItr.next();
            } else {
                break;
            }
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private int processConstraintSpecs(PropertySpec propositionSpec,
            List<ColumnSpec> columnSpecs, int i) {
        ColumnSpec[] constraintSpecs = propositionSpec.getConstraintSpecs();
        if (constraintSpecs != null) {
            for (ColumnSpec spec : constraintSpecs) {
                if (spec != null) {
                    List<ColumnSpec> specAsList = spec.asList();
                    columnSpecs.addAll(specAsList);
                    i += specAsList.size();
                }
            }
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
