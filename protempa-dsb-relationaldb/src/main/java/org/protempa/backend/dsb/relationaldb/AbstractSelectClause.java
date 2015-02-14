/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.dsb.relationaldb;

import java.util.Map;
import org.protempa.backend.dsb.relationaldb.mappings.Mappings;

abstract class AbstractSelectClause implements SelectClause {

    private final ColumnSpecInfo info;
    private final TableAliaser referenceIndices;
    private final EntitySpec entitySpec;
    private CaseClause caseClause;
    private final boolean wrapKeyId;

    AbstractSelectClause(ColumnSpecInfo info, TableAliaser referenceIndices,
            EntitySpec entitySpec, boolean wrapKeyId) {
        this.info = info;
        this.referenceIndices = referenceIndices;
        this.entitySpec = entitySpec;
        this.caseClause = null;
        this.wrapKeyId = wrapKeyId;
    }

    protected TableAliaser getReferenceIndices() {
        return referenceIndices;
    }

    @Override
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
        if (info.getReferenceIndices() != null) {
            i += info.getReferenceIndices().size();
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

        // process inbound references to the entity spec
        if (info.getReferenceIndices() != null) {
            for (Map.Entry<String, Integer> entry : info.getReferenceIndices().entrySet()) {
                indices[k] = entry.getValue();
                names[k++] = entry.getKey() + "_ref";
            }
        }

        boolean unique = info.isUnique();
        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]).getColumnSpec();
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
    
    protected abstract String wrapKeyIdInConversion(String columnStr);

    StringBuilder generateColumn(boolean distinctRequested,
            ColumnSpec columnSpec, String name, boolean hasNext) {
        StringBuilder result = new StringBuilder();
        if (distinctRequested) {
            result.append("DISTINCT ");
        }

        if (name.equals("keyid") && this.wrapKeyId) {
            result.append(wrapKeyIdInConversion(referenceIndices.generateColumnReference(columnSpec))).append(" AS ").append(name);
        } else {
            result.append(referenceIndices.generateColumnReference(columnSpec)).append(" AS ").append(name);
        }
        if (hasNext) {
            result.append(", ");
        }

        return result;
    }

    protected abstract CaseClause getCaseClause(Object[] sqlCodes,
            ColumnSpec columnSpec, Mappings mappings);

    private void setCaseClause(CaseClause caseClause) {
        this.caseClause = caseClause;
    }

    @Override
    public void setCaseClause(Object[] sqlCodes, ColumnSpec columnSpec,
            Mappings mappings) {
        setCaseClause(getCaseClause(sqlCodes, columnSpec, mappings));

    }
}
