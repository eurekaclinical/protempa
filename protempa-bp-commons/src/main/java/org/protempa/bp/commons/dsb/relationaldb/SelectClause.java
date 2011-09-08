package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Collections;
import java.util.Map;

class SelectClause extends AbstractSQLClause {
    
    private final ColumnSpecInfo info;
    private final Map<ColumnSpec, Integer> referenceIndices;
    private final EntitySpec entitySpec;
    
    SelectClause(ColumnSpecInfo info, Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        this.info = info;
        this.referenceIndices = Collections.unmodifiableMap(referenceIndices);
        this.entitySpec = entitySpec;
    }
    
    public String generateClause() {
        StringBuilder selectClause = new StringBuilder();
        int i = 0;
        if (info.getFinishTimeIndex() > 0) {
            i++;
        }
        if (info.getPropertyIndices() != null) {
            i += info.getPropertyIndices().size();
        }
        if (info.getCodeIndex() > 0) {
            i++;
        }
        if (info.getStartTimeIndex() > 0) {
            i++;
        }
        int[] uniqueIdIndices = info.getUniqueIdIndices();
        if (uniqueIdIndices != null) {
            i += uniqueIdIndices.length;
        }
        if (info.isUsingKeyIdIndex()) {
            i++;
        }
        if (info.getValueIndex() > 0) {
            i++;
        }
        int[] indices = new int[i];
        String[] names = new String[i];
        int k = 0;
        indices[k] = 0;
        if (info.isUsingKeyIdIndex()) {
            names[k++] = "keyid";
        }
        if (uniqueIdIndices != null) {
            for (int m = 0; m < uniqueIdIndices.length; m++) {
                indices[k] = uniqueIdIndices[m];
                names[k++] = "uniqueid" + m;
            }
        }
        if (info.getCodeIndex() > 0) {
            indices[k] = info.getCodeIndex();
            names[k++] = "code";
        }
        if (info.getStartTimeIndex() > 0) {
            indices[k] = info.getStartTimeIndex();
            names[k++] = "starttime";
        }
        if (info.getFinishTimeIndex() > 0) {
            indices[k] = info.getFinishTimeIndex();
            names[k++] = "finishtime";
        }
        if (info.getValueIndex() > 0) {
            indices[k] = info.getValueIndex();
            names[k++] = "value";
        }
        if (info.getPropertyIndices() != null) {
            PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
            for (PropertySpec propertySpec : propertySpecs) {
                String propertyName = propertySpec.getName();
                int propertyIndex =
                        info.getPropertyIndices().get(propertyName);
                indices[k] = propertyIndex;
                names[k++] = propertyName + "_value";
            }
        }

        boolean unique = info.isUnique();
        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]);
            Integer index = referenceIndices.get(cs);
            assert index != null : "index is null for " + cs;
            String column = cs.getColumn();
            String name = names[j];
            boolean distinctRequested = (j == 0 && !unique);
            boolean hasNext = j < indices.length - 1;
            if (column == null) {
                throw new AssertionError("column cannot be null: "
                        + "index=" + index + "; name=" + name + "; cs=" + cs);
            }
            if (name == null) {
                throw new AssertionError("name cannot be null");
            }
            selectClause.append(generateColumn(distinctRequested, index,
                    column, name, hasNext));
        }
        
        return selectClause.toString();
    }
    
    StringBuilder generateColumn(boolean distinctRequested, int index, String column, String name, boolean hasNext) {
        StringBuilder result = new StringBuilder();
        if (distinctRequested) {
            result.append("distinct ");
        }
        
        result.append(generateColumnReference(index, column)).append(" as ").append(name);
        if (hasNext) {
            result.append(',');
        }

        return result;
    }
}
