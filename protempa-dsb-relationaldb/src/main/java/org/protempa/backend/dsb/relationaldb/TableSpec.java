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

/**
 * Wrapper for {@link ColumnSpec} that only allows a schema and table.
 * 
 * @author Michel Mansour
 * 
 */
public final class TableSpec {
    private final ColumnSpec spec;

    private TableSpec(String schema, String table) {
        this.spec = new ColumnSpec(schema, table);
    }

    private TableSpec(ColumnSpec spec) {
        this.spec = spec;
    }

    /**
     * Creates and returns a new {@link TableSpec} with the given schema and
     * table names.
     * 
     * @param schema
     *            the schema name to use
     * @param table
     *            the table name to use
     * @return a new {@link TableSpec} with the given schema and table names
     */
    public static TableSpec withSchemaAndTable(String schema, String table) {
        return new TableSpec(schema, table);
    }

    /**
     * Creates and returns a new {@link TableSpec} from the given
     * {@link ColumnSpec}. The <tt>TableSpec</tt> will have the same schema and
     * table name as the <tt>ColumnSpec</tt>.
     * 
     * @param columnSpec
     *            the {@link ColumnSpec} the new {@link TableSpec} will be based
     *            upon
     * @return a new {@link TableSpec} with the same schema and table as the
     *         given {@link ColumnSpec}.
     */
    public static TableSpec fromColumnSpec(ColumnSpec columnSpec) {
        return new TableSpec(columnSpec);
    }

    /**
     * Gets the schema
     * 
     * @return the schema name
     */
    public String getSchema() {
        return spec.getSchema();
    }

    /**
     * Gets the table
     * 
     * @return the table name
     */
    public String getTable() {
        return spec.getTable();
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof TableSpec) {
            TableSpec other = (TableSpec) o;
            return other.getSchema().equals(getSchema())
                    && other.getTable().equals(getTable());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + spec.getSchema().hashCode();
        result = 31 * result + spec.getTable().hashCode();

        return result;
    }

    @Override
    public String toString() {
        return spec.getSchema() + "." + spec.getTable();
    }
}
