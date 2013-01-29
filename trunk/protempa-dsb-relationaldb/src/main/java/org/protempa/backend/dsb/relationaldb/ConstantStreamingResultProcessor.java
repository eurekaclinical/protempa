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
package org.protempa.backend.dsb.relationaldb;

import org.protempa.DataStreamingEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataStreamingEventIterator;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;

class ConstantStreamingResultProcessor extends StreamingMainResultProcessor<Constant> {

    private DataStreamingEventIterator<Constant> itr;

    ConstantStreamingResultProcessor(
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(entitySpec, dataSourceBackendId);
    }

    class ConstantIterator extends PropositionResultSetIterator<Constant> {

        private final Logger logger;
        private final DataSourceBackendDataSourceType dsType;

        ConstantIterator(Statement statement, ResultSet resultSet, 
                EntitySpec entitySpec) throws SQLException {
            super(statement, resultSet, entitySpec, getDataSourceBackendId());
            this.logger = SQLGenUtil.logger();
            this.dsType = DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        }

        @Override
        void doProcess(ResultSet resultSet,
                String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
                int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
                Value[] propertyValues) throws SQLException {
            int i = 1;
            String kId = resultSet.getString(i++);
            if (kId == null) {
                logger.warning("A keyId is null. Skipping record.");
                return;
            }
            handleKeyId(kId);

            i = readUniqueIds(uniqueIds, resultSet, i);
            if (Arrays.contains(uniqueIds, null)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Unique ids contain null ({0}). Skipping record.",
                            StringUtils.join(uniqueIds, ", "));
                    return;
                }
            }
            UniqueId uniqueId = generateUniqueId(entitySpec.getName(), uniqueIds);

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
                        return;
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
            handleProposition(cp);

            logger.log(Level.FINEST, "Created constant {0}", cp);
        }
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        this.itr = new ConstantIterator(getStatement(), resultSet, entitySpec);
    }

    @Override
    final DataStreamingEventIterator<Constant> getResults() {
        return this.itr;
    }
}
