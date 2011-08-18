package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;

import org.arp.javautil.log.Logging;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataSourceType;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;

class ConstantResultProcessor extends MainResultProcessor<Constant> {

    private static final int FLUSH_SIZE = 1000000;
    
    ConstantResultProcessor(ResultCache<Constant> results,
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(results, entitySpec, dataSourceBackendId);
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<Constant> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String entitySpecName = entitySpec.getName();
        //boolean hasRefs = entitySpec.getReferenceSpecs().length > 0;
        String[] propIds = entitySpec.getPropositionIds();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            codeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        Logger logger = SQLGenUtil.logger();
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Value[] propertyValues = new Value[propertySpecs.length];
        int count = 0;
        String[] uniqueIds =
                    new String[entitySpec.getUniqueIdSpecs().length];
        DataSourceType dsType =
                DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int[] columnTypes = new int[resultSetMetaData.getColumnCount()];
        for (int i = 0; i < columnTypes.length; i++) {
            columnTypes[i] = resultSetMetaData.getColumnType(i + 1);
        }
        
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);
            if (keyId == null) {
                logger.warning("A keyId is null. Skipping record.");
                continue;
            }
            
            i = readUniqueIds(uniqueIds, resultSet, i);
            if (Arrays.contains(uniqueIds, null)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, 
                        "Unique ids contain null ({0}). Skipping record.", 
                        StringUtils.join(uniqueIds, ", "));
                    continue;
                }
            }
            UniqueId uniqueId = generateUniqueId(entitySpecName, uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    assert propIds.length == 1: 
                            "Don't know which proposition id to assign to";
                    propId = propIds[0];
                } else {
                    String code = resultSet.getString(i++);
                    propId = sqlCodeToPropositionId(codeSpec, code);
                    if (propId == null) {
                        continue;
                    }
                }
            } else {
                i++;
            }

            i = extractPropertyValues(resultSet, i,
                    propertyValues, columnTypes);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            Constant cp = new Constant(propId, uniqueId);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                cp.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            cp.setDataSourceType(dsType);
            logger.log(Level.FINEST, "Created constant {0}", cp);
            results.add(keyId, cp);
            if (++count % FLUSH_SIZE == 0) {
                results.flush(this);
                if (logger.isLoggable(Level.FINE)) {
                    Logging.logCount(logger, Level.FINE, count, 
                        "Retrieved {0} record",
                        "Retrieved {0} records");
                }
            }
        }
        results.flush(this);
        if (logger.isLoggable(Level.FINE)) {
            Logging.logCount(logger, Level.FINE, count, 
                            "Retrieved {0} record total",
                            "Retrieved {0} records total");
        }
    }
}
