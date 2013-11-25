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
import org.apache.commons.lang3.StringUtils;
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
                    assert propIds.length == 1 :
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
