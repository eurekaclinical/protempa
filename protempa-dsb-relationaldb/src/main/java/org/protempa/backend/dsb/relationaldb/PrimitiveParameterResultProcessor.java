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

import java.io.IOException;
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
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

class PrimitiveParameterResultProcessor extends
        MainResultProcessor<PrimitiveParameter> {

    private static final int FLUSH_SIZE = 1000000;
    
    PrimitiveParameterResultProcessor(ResultCache<PrimitiveParameter> results,
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(results, entitySpec, dataSourceBackendId);
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<PrimitiveParameter> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String entitySpecName = entitySpec.getName();
        //boolean hasRefs = entitySpec.getInboundRefSpecs().length > 0;
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
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int[] columnTypes = new int[resultSetMetaData.getColumnCount()];
        for (int i = 0; i < columnTypes.length; i++) {
            columnTypes[i] = resultSetMetaData.getColumnType(i + 1);
        }
        DataSourceType dsType =
                DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        
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

            Long timestamp = null;
            try {
                timestamp = entitySpec.getPositionParser().toPosition(resultSet,
                        i, columnTypes[i - 1]);
                i++;
            } catch (SQLException e) {
                logger.log(Level.WARNING,
                    "Could not parse timestamp. Leaving timestamp unset.", e);
            }
            
            ValueType valueType = entitySpec.getValueType();
            String cpValStr = resultSet.getString(i++);
            Value cpVal = valueType.parse(cpValStr);
            
            i = extractPropertyValues(resultSet, i,
                    propertyValues, columnTypes);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            PrimitiveParameter p = new PrimitiveParameter(propId, uniqueId);
            p.setPosition(timestamp);
            p.setGranularity(entitySpec.getGranularity());
            p.setValue(cpVal);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                p.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            p.setDataSourceType(dsType);

            logger.log(Level.FINEST, "Created primitive parameter {0}", p);
            results.add(keyId, p);
            if (++count % FLUSH_SIZE == 0) {
                try {
                    results.flush(this);
                } catch (IOException ex) {
                    throw new QueryResultsCacheException("Flushing primitive parameters to cache failed", ex);
                }
                if (logger.isLoggable(Level.FINE)) {
                    Logging.logCount(logger, Level.FINE, count, 
                        "Retrieved {0} record",
                        "Retrieved {0} records");
                }
            }
        }
        try {
            results.flush(this);
        } catch (IOException ex) {
            throw new QueryResultsCacheException("Flushing primitive parameters to cache failed", ex);
        }
        if (logger.isLoggable(Level.FINE)) {
            Logging.logCount(logger, Level.FINE, count, 
                "Retrieved {0} record total",
                "Retrieved {0} records total");
        }
    }

    
}
