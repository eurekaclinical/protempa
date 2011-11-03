package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

abstract class AbstractSelectClause implements SelectClause {

    private final ColumnSpecInfo info;
    private final TableAliaser referenceIndices;
    private final EntitySpec entitySpec;
    private CaseClause caseClause;

    AbstractSelectClause(ColumnSpecInfo info, TableAliaser referenceIndices,
            EntitySpec entitySpec) {
        this.info = info;
        this.referenceIndices = referenceIndices;
        this.entitySpec = entitySpec;
        this.caseClause = null;
    }

    public String generateClause() {
        StringBuilder selectClause = new StringBuilder("SELECT ");
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
                int propertyIndex = info.getPropertyIndices().get(propertyName);
                indices[k] = propertyIndex;
                names[k++] = propertyName + "_value";
            }
        }

        boolean unique = info.isUnique();
        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]);
            String name = names[j];
            boolean distinctRequested = (j == 0 && !unique);
            boolean hasNext = j < indices.length - 1;
            if (name == null) {
                throw new AssertionError("name cannot be null");
            }
            selectClause.append(generateColumn(distinctRequested, cs, name,
                    hasNext));
        }

        if (caseClause != null) {
            selectClause.append(caseClause.generateClause());
        }

        return selectClause.toString();
    }

    StringBuilder generateColumn(boolean distinctRequested,
            ColumnSpec columnSpec, String name, boolean hasNext) {
        StringBuilder result = new StringBuilder();
        if (distinctRequested) {
            result.append("DISTINCT ");
        }

        result.append(referenceIndices.generateColumnReference(columnSpec))
                .append(" AS ").append(name);
        if (hasNext) {
            result.append(", ");
        }

        return result;
    }

    protected abstract CaseClause getCaseClause(Object[] sqlCodes,
            TableAliaser referenceIndices, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues);

    private void setCaseClause(CaseClause caseClause) {
        this.caseClause = caseClause;
    }

    @Override
    public void setCaseClause(Object[] sqlCodes, TableAliaser referenceIndices,
            ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        setCaseClause(getCaseClause(sqlCodes, referenceIndices, columnSpec,
                filteredConstraintValues));

    }
}
