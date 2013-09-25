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

import java.util.List;
import java.util.Map;

final class ColumnSpecInfo {
    private boolean unique;
    private List<ColumnSpec> columnSpecs;
    private int startTimeIndex = -1;
    private int finishTimeIndex = -1;
    private Map<String, Integer> propertyIndices;
    private int valueIndex = -1;
    private int codeIndex = -1;
    private int[] uniqueIdIndices;
    private Map<String, Integer> referenceIndices;
    private boolean usingKeyIdIndex;

    ColumnSpecInfo() {

    }

    boolean isUnique() {
        return unique;
    }

    void setUnique(boolean unique) {
        this.unique = unique;
    }

    int getCodeIndex() {
        return codeIndex;
    }

    void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }

    void setColumnSpecs(List<ColumnSpec> columnSpecs) {
        this.columnSpecs = columnSpecs;
    }

    int getFinishTimeIndex() {
        return finishTimeIndex;
    }

    void setFinishTimeIndex(int finishTimeIndex) {
        this.finishTimeIndex = finishTimeIndex;
    }

    int getStartTimeIndex() {
        return startTimeIndex;
    }

    void setStartTimeIndex(int startTimeIndex) {
        this.startTimeIndex = startTimeIndex;
    }

    Map<String, Integer> getPropertyIndices() {
        return propertyIndices;
    }

    void setPropertyIndices(Map<String, Integer> index) {
        this.propertyIndices = index;
    }

    int getValueIndex() {
        return valueIndex;
    }

    void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }

    void setUniqueIdIndices(int[] uniqueIdIndices) {
        this.uniqueIdIndices = uniqueIdIndices;
    }

    int[] getUniqueIdIndices() {
        return this.uniqueIdIndices;
    }

    Map<String, Integer> getReferenceIndices() {
        return referenceIndices;
    }

    void setReferenceIndices(Map<String, Integer> referenceIndices) {
        this.referenceIndices = referenceIndices;
    }

    boolean isUsingKeyIdIndex() {
        return this.usingKeyIdIndex;
    }

    void setUsingKeyIdIndex(boolean usingKeyIdIndex) {
        this.usingKeyIdIndex = usingKeyIdIndex;
    }
}
