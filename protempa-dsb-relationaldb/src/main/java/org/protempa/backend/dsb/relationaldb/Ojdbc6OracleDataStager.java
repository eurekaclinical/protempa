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

import static org.arp.javautil.collections.Collections.containsAny;
import static org.arp.javautil.collections.Collections.putList;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.io.Retryer;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleDataStager implements DataStager {

    private final StagingSpec[] stagingSpecs;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final ConnectionSpec connectionSpec;

    private final Map<String, List<EntitySpec>> propIdToEntitySpecs;
    private final Map<StagingSpec, List<TableSpec>> tempTables = new HashMap<>();

    private final Map<TableSpec, Integer> indexIds = new HashMap<>();

    private static final Logger logger = SQLGenUtil.logger();
    private final boolean streamingMode;

    Ojdbc6OracleDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, ConnectionSpec connectionSpec,
            boolean streamingMode) {
        this.stagingSpecs = stagingSpecs;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = entitySpecs;
        this.filters = filters;
        this.propIds = propIds;
        this.keyIds = keyIds;
        this.order = order;
        this.connectionSpec = connectionSpec;
        this.propIdToEntitySpecs = new HashMap<>();
        populatePropIdEntitySpecMap();
        this.streamingMode = streamingMode;
    }

    private void populatePropIdEntitySpecMap() {
        for (EntitySpec es : entitySpecs) {
            for (String propId : es.getPropositionIds()) {
                putList(propIdToEntitySpecs, propId, es);
            }
        }
    }

    @Override
    public void stageTables() throws SQLException {
        createTables();
        indexTables();
        analyzeTables();
        mergeTables();
    }

    @Override
    public void cleanup() throws SQLException {
        for (StagingSpec ss : stagingSpecs) {
            dropTables(ss);
        }
    }

    private void execute(String sql) throws SQLException {
        if (this.connectionSpec != null) {
            RetryableSQLExecutor operation = new RetryableSQLExecutor(
                    this.connectionSpec, sql, null);
            Retryer<SQLException> retryer = new Retryer<>(3);
            if (!retryer.execute(operation)) {
                SQLException ex = SQLExecutor.assembleSQLException(retryer
                        .getErrors());
                throw ex;
            }
        }
    }

    private void dropTables(StagingSpec stagingSpec) throws SQLException {
        String dropView = "DROP VIEW IF EXISTS "
                + TableSpec.withSchemaAndTable(stagingSpec.getStagingArea().getSchema(), stagingSpec.getStagingArea().getTable());

        logger.log(Level.INFO, "Dropping view {0}: {1}", new Object[] {
                stagingSpec.getStagingArea(), dropView });

        execute(dropView);
        
        for (TableSpec table : this.tempTables.get(stagingSpec)) {
            doDropForStagingSpec(table);
        }
    }

    private void doDropForStagingSpec(TableSpec table) throws SQLException {
        String dropTable = "DROP TABLE IF EXISTS " + TableSpec.withSchemaAndTable(table.getSchema(),
                table.getTable());

        logger.log(Level.INFO, "Dropping table {0}: {1}", new Object[] {
            table, dropTable });

        execute(dropTable);
        
    }

    private void createTables() throws SQLException {
        for (StagingSpec stagingSpec : stagingSpecs) {
            int i = 0;
            for (EntitySpec es : stagingSpec.getEntitySpecs()) {
                Set<Filter> filtersCopy = new HashSet<>(filters);
                removeNonApplicableFilters(filtersCopy, es);
                String stgTableName = stagingSpec.getStagingArea().getTable()
                                + "_" + i;
                StagingSpec newStagingSpec = StagingSpec.newTableName(
                        stagingSpec, stgTableName);
                
                doDropForStagingSpec(newStagingSpec.getStagingArea());
                
                CreateStatement stmt = new Ojdbc6OracleStagingCreateStatement(
                        newStagingSpec,
                        es,
                        referenceSpec,
                        Arrays.<EntitySpec> asList(stagingSpec.getEntitySpecs()),
                        filters, propIds, keyIds, order, null,
                        this.streamingMode);
                String sql = stmt.generateStatement();
                logger.log(Level.INFO,
                        "Creating staging area for entity spec {0}: {1}",
                        new Object[] { es.getName(), sql });
                execute(sql);
                
                putList(this.tempTables, stagingSpec,
                        newStagingSpec.getStagingArea());
                
                this.indexIds.put(newStagingSpec.getStagingArea(), 0);
                i++;
            }
        }
    }

    private void analyzeTables() throws SQLException {
        for (StagingSpec stagingSpec : stagingSpecs) {
            for (TableSpec tableSpec : this.tempTables.get(stagingSpec)) {
                String sql = "begin DBMS_STATS.gather_table_stats('"
                        + tableSpec.getSchema() + "', '" + tableSpec.getTable()
                        + "'); end;";
                logger.log(Level.INFO, "Analyzing staging table {0}: {1}",
                        new Object[] { tableSpec, sql });
                execute(sql);
            }
        }
    }

    private void indexTables() throws SQLException {
        for (StagingSpec spec : stagingSpecs) {
            indexPrimaryKeys(spec);
            indexOtherColumns(spec);
        }
    }

    private String generateUniqueIndex(TableSpec table) {
        this.indexIds.put(table, this.indexIds.get(table) + 1);
        return table.getTable().toUpperCase() + "_IDX"
                + this.indexIds.get(table);
    }

    private void indexPrimaryKeys(StagingSpec stagingSpec) throws SQLException {
        for (TableSpec tableSpec : this.tempTables.get(stagingSpec)) {
            StringBuilder sql = new StringBuilder("CREATE UNIQUE INDEX ");
            sql.append(tableSpec.getSchema());
            sql.append(".");
            sql.append(generateUniqueIndex(tableSpec));
            sql.append(" ON ");
            sql.append(tableSpec.getSchema());
            sql.append(".");
            sql.append(tableSpec.getTable());
            sql.append(" (");
            sql.append(stagingSpec.getUniqueColumn());
            sql.append(")");
            if (stagingSpec.getIndexTablespace() != null) {
                sql.append(" TABLESPACE ");
                sql.append(stagingSpec.getIndexTablespace());
            }
            sql.append(" NOLOGGING ");

            logger.log(Level.INFO,
                    "Indexing primary key {0} for staging table {1}: {2}",
                    new Object[] { stagingSpec.getUniqueColumn(), tableSpec,
                            sql });
            execute(sql.toString());
        }
    }

    private void indexOtherColumns(StagingSpec stagingSpec) throws SQLException {
        for (TableSpec table : this.tempTables.get(stagingSpec)) {
            HashSet<String> indexed = new HashSet<>();
            for (StagedColumnSpec column : stagingSpec.getStagedColumns()) {
                String realColumn = getRealColumn(column);
                if (!realColumn.equals(stagingSpec.getUniqueColumn())
                        && !indexed.contains(realColumn)) {
                    StringBuilder sql = new StringBuilder(
                            "CREATE BITMAP INDEX ");
                    sql.append(stagingSpec.getStagingArea().getSchema());
                    sql.append(".");
                    sql.append(generateUniqueIndex(table));
                    sql.append(" ON ");
                    sql.append(table.getSchema());
                    sql.append(".");
                    sql.append(table.getTable());
                    sql.append(" (");
                    sql.append(realColumn);
                    sql.append(")");
                    if (stagingSpec.getIndexTablespace() != null) {
                        sql.append(" TABLESPACE ");
                        sql.append(stagingSpec.getIndexTablespace());
                    }
                    sql.append(" NOLOGGING");

                    logger.log(
                            Level.INFO,
                            "Indexing column {0} (as {1}) for staging table {2}: {3}",
                            new Object[] { column.getColumn(), realColumn,
                                    table.getTable(), sql });

                    execute(sql.toString());
                    indexed.add(realColumn);
                }
            }
        }
    }

    private static String getRealColumn(StagedColumnSpec spec) {
        if (null != spec.getAsName() && !spec.getAsName().isEmpty()) {
            return spec.getAsName();
        } else {
            return spec.getColumn();
        }
    }

    private void mergeTables() throws SQLException {
        for (StagingSpec ss : stagingSpecs) {
            StringBuilder sql = new StringBuilder("CREATE OR REPLACE VIEW ");
            sql.append(ss.getStagingArea().getSchema());
            sql.append(".");
            sql.append(ss.getStagingArea().getTable());
            sql.append(" AS ");
            List<TableSpec> ssTables = this.tempTables.get(ss);
            for (int i = 0; i < ssTables.size(); i++) {
                sql.append("SELECT * FROM ");
                sql.append(ssTables.get(i).getSchema());
                sql.append(".");
                sql.append(ssTables.get(i).getTable());
                if (i < ssTables.size() - 1) {
                    sql.append(" UNION ");
                }
            }

            logger.log(Level.INFO,
                    "Merging staging tables for staging area {0}: {1}",
                    new Object[] { ss.getStagingArea(), sql });
            execute(sql.toString());
        }
    }

    private void removeNonApplicableFilters(Set<Filter> filtersCopy,
            EntitySpec entitySpec) {
        Set<EntitySpec> entitySpecsSet = new HashSet<>();
        Set<String> filterPropIds = new HashSet<>();
        String[] entitySpecPropIds = entitySpec.getPropositionIds();
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            for (String filterPropId : f.getPropositionIds()) {
                filterPropIds.add(filterPropId);
            }
            for (EntitySpec es : this.entitySpecs) {
                if (containsAny(filterPropIds, es.getPropositionIds())) {
                    entitySpecsSet.add(es);
                }
            }
            if (!containsAny(filterPropIds, entitySpecPropIds)) {
                itr.remove();
            }
            entitySpecsSet.clear();
            filterPropIds.clear();
        }
    }
}
