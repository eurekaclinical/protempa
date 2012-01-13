package org.protempa.bp.commons.dsb.relationaldb;

import static org.arp.javautil.collections.Collections.containsAny;
import static org.arp.javautil.collections.Collections.putList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleDataStager implements DataStager {

    private final StagingSpec[] stagingSpecs;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;

    private final Map<String, List<EntitySpec>> propIdToEntitySpecs;
    private final Map<StagingSpec, List<TableSpec>> tempTables = new HashMap<StagingSpec, List<TableSpec>>();

    private static final Logger logger = SQLGenUtil.logger();

    Ojdbc6OracleDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        this.stagingSpecs = stagingSpecs;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = entitySpecs;
        this.filters = filters;
        this.propIds = propIds;
        this.keyIds = keyIds;
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.propIdToEntitySpecs = new HashMap<String, List<EntitySpec>>();
        populatePropIdEntitySpecMap();
    }

    private void populatePropIdEntitySpecMap() {
        for (EntitySpec es : entitySpecs) {
            for (String propId : es.getPropositionIds()) {
                putList(propIdToEntitySpecs, propId, es);
            }
        }
    }

    @Override
    public void stageTables() {
        createTables();
        analyzeTables();
        indexTables();
        mergeTables();
    }

    @Override
    public void dropTables() {
        for (StagingSpec ss : stagingSpecs) {
            dropTables(ss);
        }
    }

    private void dropTables(StagingSpec stagingSpec) {

    }

    private void createTables() {
        for (StagingSpec stagingSpec : stagingSpecs) {
            int i = 0;
            for (EntitySpec es : stagingSpec.getEntitySpecs()) {
                Set<Filter> filtersCopy = new HashSet<Filter>(filters);
                removeNonApplicableFilters(filtersCopy, es);
                SimpleStagingSpec sss = new SimpleStagingSpec(
                        TableSpec.withSchemaAndTable(stagingSpec
                                .getStagingArea().getSchema(), stagingSpec
                                .getStagingArea().getTable() + "_" + i),
                        stagingSpec.getUniqueColumn(),
                        stagingSpec.getReplacedTable(),
                        stagingSpec.getStagedColumns(), es);
                CreateStatement stmt = new Ojdbc6OracleStagingCreateStatement(
                        sss, referenceSpec, Collections.singletonList(es),
                        filters, propIds, keyIds, order, resultProcessor);
                String sql = stmt.generateStatement();
                logger.log(Level.FINE,
                        "Creating staging area for entity spec {0}: {1}",
                        new Object[] { es.getName(), sql });
                putList(this.tempTables, stagingSpec, sss.getStagingArea());
                i++;
            }
        }
    }

    private void analyzeTables() {
        for (StagingSpec stagingSpec : stagingSpecs) {
            for (TableSpec tableSpec : this.tempTables.get(stagingSpec)) {
                String sql = "EXEC DMBS_STATS.gather_table_stats('"
                        + tableSpec.getSchema() + "', '" + tableSpec.getTable()
                        + "')";
                logger.log(Level.FINE, "Analyzing staging table {0}: {1}",
                        new Object[] { tableSpec, sql });
            }
        }
    }

    private void indexTables() {
        for (StagingSpec spec : stagingSpecs) {
            indexPrimaryKeys(spec);
            indexOtherColumns(spec);
        }
    }

    private void indexPrimaryKeys(StagingSpec stagingSpec) {
        // CREATE UNIQUE INDEX AIWSTG.FES_UIDX001 ON AIWSTG.FACT_ENCOUNTER_IN
        // (ENCOUNTER_KEY) TABLESPACE AIWDEV_TMP_IL01 NOLOGGING;
        for (TableSpec tableSpec : this.tempTables.get(stagingSpec)) {
            StringBuilder sql = new StringBuilder("CREATE UNIQUE INDEX ");
            sql.append(tableSpec.getSchema());
            sql.append(".");
            sql.append("FES_UIDX001"); // replace with variable
            sql.append(" ON ");
            sql.append(tableSpec.getSchema());
            sql.append(".");
            sql.append(tableSpec.getTable());
            sql.append("(");
            sql.append(stagingSpec.getUniqueColumn());
            sql.append(")");
            sql.append(" TABLESPACE ");
            sql.append("AIWDEV_TMP_IL01"); // replace with variable
            sql.append(" NOLOGGING ");

            logger.log(Level.FINE,
                    "Indexing primary key {0} for staging table {1}: {2}",
                    new Object[] { stagingSpec.getUniqueColumn(), tableSpec,
                            sql });
        }
    }

    private void indexOtherColumns(StagingSpec spec) {
        // CREATE BITMAP INDEX AIWSTG.FSE_BMIDX001 ON AIWSTG.FACT_ENCOUNTER_IN
        // (ENTITY_HEALTHCARE_KEY) TABLESPACE AIWDEV_TMP_IL01 NOLOGGING;
        for (TableSpec table : this.tempTables.get(spec)) {
            for (SimpleColumnSpec column : spec.getStagedColumns()) {
                if (!column.getColumn().equals(spec.getUniqueColumn())) {
                    StringBuilder sql = new StringBuilder("CREATE BITAP INDEX ");
                    sql.append(spec.getStagingArea().getSchema());
                    sql.append(".");
                    sql.append("FES_BMIDX001");
                    sql.append(" ON ");
                    sql.append(table.getSchema());
                    sql.append(".");
                    sql.append(table.getTable());
                    sql.append("(");
                    sql.append(column.getColumn());
                    sql.append(")");
                    sql.append(" TABLESPACE ");
                    sql.append("AIWDEV_TMP_IL01");
                    sql.append(" NOLOGGING");

                    logger.log(Level.FINE,
                            "Indexing column {0} for staging table {1}: {2}",
                            new Object[] { column.getColumn(),
                                    table.getTable(), sql });
                }
            }
        }
    }

    private void mergeTables() {
        for (StagingSpec ss : stagingSpecs) {
            StringBuilder result = new StringBuilder("CREATE OR REPLACE VIEW ");
            result.append(ss.getStagingArea().getSchema());
            result.append(".");
            result.append(ss.getStagingArea().getTable());
            result.append(" AS ");
            List<TableSpec> ssTables = this.tempTables.get(ss);
            for (int i = 0; i < ssTables.size(); i++) {
                result.append("SELECT * FROM ");
                result.append(ssTables.get(i).getSchema());
                result.append(".");
                result.append(ssTables.get(i).getTable());
                if (i < ssTables.size() - 1) {
                    result.append(" UNION ");
                }
            }

            logger.log(Level.FINE,
                    "Merging staging tables for staging area {0}: {1}",
                    new Object[] { ss.getStagingArea(), result });
        }
    }

    private void removeNonApplicableFilters(Set<Filter> filtersCopy,
            EntitySpec entitySpec) {
        Set<EntitySpec> entitySpecsSet = new HashSet<EntitySpec>();
        Set<String> filterPropIds = new HashSet<String>();
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
