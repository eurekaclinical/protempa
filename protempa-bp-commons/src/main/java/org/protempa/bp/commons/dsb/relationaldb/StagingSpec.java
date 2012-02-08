/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Represents a spec for staging an entity spec. It requires {@link TableSpec}s
 * to describe which table to stage and where to stage it, and
 * {@link ColumnSpec}s indicating which columns in the table to stage. An
 * {@link EntitySpec} describes the relationships this spec has to other
 * <tt>EntitySpec</tt>s, so that queries will be properly generated. The
 * <tt>EntitySpec</tt> must have the same proposition IDs as some other
 * <tt>EntitySpec</tt> (constant, event, or primitive parameter) defined in the
 * data source backend.
 */
public final class StagingSpec {

    private final TableSpec stagingArea;
    private final String indexTablespace;
    private final String uniqueColumn;
    private final TableSpec replacedTable;
    private final StagedColumnSpec[] stagedColumns;
    private final EntitySpec[] entitySpecs;

    /**
     * Creates a new staging spec for use in a relational database data source
     * backend.
     * 
     * @param stagingArea
     *            a {@link TableSpec} defining where to stage the data
     * @param indexTablespace
     *            the name of the tablespace where the indexes will be stored
     * @param uniqueColumn
     *            the unique key of the table
     * @param replacedTable
     *            a {@link TableSpec}s defining which table is to be replaced in
     *            queries
     * @param stagedColumns
     *            an array of {@link StagedColumnSpec}s specifying which columns
     *            from the table to stage
     * @param entitySpecs
     *            the {@link EntitySpec} the staged data will represent. This
     *            entity spec must map to the same proposition IDs as some other
     *            entity spec (constant, event, or primitive parameter) defined
     *            in the data source backend. Reference specs are not necessary.
     *            The only property specs needed are those that will constrain
     *            the data to be staged. Otherwise, the <tt>EntitySpec</tt>
     *            should be identical to the original.
     */
    public StagingSpec(TableSpec stagingArea, String indexTablespace,
            String uniqueColumn, TableSpec replacedTable,
            StagedColumnSpec[] stagedColumns, EntitySpec[] entitySpecs) {
        if (!validateStagedColumns(stagedColumns, entitySpecs)) {
            throw new IllegalArgumentException(
                    "All staging columns that specify substitute column names must apply only to entity specs that are specified in the staging spec.");
        }
        this.stagingArea = stagingArea;
        this.indexTablespace = indexTablespace;
        this.uniqueColumn = uniqueColumn;
        this.replacedTable = replacedTable;
        this.stagedColumns = stagedColumns;
        this.entitySpecs = entitySpecs;
    }

    static StagingSpec newTableName(StagingSpec stagingSpec, String newTableName) {
        return new StagingSpec(TableSpec.withSchemaAndTable(stagingSpec
                .getStagingArea().getSchema(), newTableName),
                stagingSpec.getIndexTablespace(),
                stagingSpec.getUniqueColumn(), stagingSpec.getReplacedTable(),
                stagingSpec.getStagedColumns(), stagingSpec.getEntitySpecs());
    }

    /**
     * If any staged columns specify substitute column names, then the entity
     * specs the substitution applies to must be in the array of entity specs
     */
    private boolean validateStagedColumns(StagedColumnSpec[] stagedColumns,
            EntitySpec[] entitySpecs) {
        for (StagedColumnSpec cs : stagedColumns) {
            if (cs.getForEntitySpecs() != null) {
                for (String esName : cs.getForEntitySpecs()) {
                    boolean match = false;
                    for (EntitySpec es : entitySpecs) {
                        if (esName.equals(es.getName())) {
                            match = true;
                        }
                    }
                    if (!match) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the staging area.
     * 
     * @return a {@link TableSpec} describing the staging area
     */
    public TableSpec getStagingArea() {
        return stagingArea;
    }

    /**
     * Gets the tablespace where the indexes will live.
     * 
     * @return the index tablespace
     */
    public String getIndexTablespace() {
        return indexTablespace;
    }

    /**
     * Gets the unique column.
     * 
     * @return the unique column name
     */
    public String getUniqueColumn() {
        return uniqueColumn;
    }

    /**
     * Gets the replaced table
     * 
     * @return a {@link TableSpec} describing the table being replaced
     */
    public TableSpec getReplacedTable() {
        return replacedTable;
    }

    /**
     * Gets the staged columns
     * 
     * @return the array of {@link ColumnSpec}s which are the columns being
     *         staged
     */
    public StagedColumnSpec[] getStagedColumns() {
        return stagedColumns.clone();
    }

    /**
     * Gets the entity spec
     * 
     * @return the {@link EntitySpec} being staged
     */
    public EntitySpec[] getEntitySpecs() {
        return entitySpecs;
    }

}
