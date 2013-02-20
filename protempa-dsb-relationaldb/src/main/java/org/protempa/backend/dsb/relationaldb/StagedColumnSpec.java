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

public final class StagedColumnSpec {
    private final ColumnSpec spec;
    private final String asName;
    private final String[] forEntitySpecs;

    public StagedColumnSpec(String schema, String table, String column,
            String asName, String[] forEntitySpecs) {
        if (asName != null
                && (forEntitySpecs == null || forEntitySpecs.length < 1)) {
            throw new IllegalArgumentException(
                    "The substitute name for a column must apply to at least one entity spec. 'forEntitySpecs' is null or has fewer than 1 element.");
        }
        this.spec = new ColumnSpec(schema, table, column);
        this.asName = asName;
        this.forEntitySpecs = forEntitySpecs;
    }

    public StagedColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null, null);
    }

    public String getSchema() {
        return spec.getSchema();
    }

    public String getTable() {
        return spec.getTable();
    }

    public String getColumn() {
        return spec.getColumn();
    }

    public String getAsName() {
        return asName;
    }

    public String[] getForEntitySpecs() {
        if (null == forEntitySpecs) {
            return null;
        }
        return forEntitySpecs.clone();
    }

    public ColumnSpec toColumnSpec() {
        return spec;
    }
}
