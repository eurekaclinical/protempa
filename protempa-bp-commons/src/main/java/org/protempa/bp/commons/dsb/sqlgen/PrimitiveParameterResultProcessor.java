package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.collections.Collections;
import org.protempa.DatabaseDataSourceType;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

class PrimitiveParameterResultProcessor extends
        AbstractMainResultProcessor<PrimitiveParameter> {

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<PrimitiveParameter> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String[] propIds = entitySpec.getPropositionIds();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            codeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        Logger logger = SQLGenUtil.logger();
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);

            String[] uniqueIds = 
                    new String[entitySpec.getUniqueIdSpecs().length];
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifer = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    propId = propIds[0];
                } else {
                    String code = resultSet.getString(i++);
                    propId = sqlCodeToPropositionId(codeSpec, code);
                }
            } else {
                i++;
            }

            Long timestamp = null;
            try {
                timestamp = entitySpec.getPositionParser().toLong(resultSet,
                        i++);
            } catch (SQLException e) {
                logger.log(Level.WARNING,
                        "Could not parse timestamp. Ignoring data value.", e);
                continue;
            }

            ValueType vf = entitySpec.getValueType();
            Value cpVal = ValueFactory.get(vf).parseValue(
                    resultSet.getString(i++));

            PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
            Value[] propertyValues = new Value[propertySpecs.length];
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                ValueType valueType = propertySpec.getValueType();
                Value value = ValueFactory.get(valueType).parseValue(
                        resultSet.getString(i++));
                propertyValues[j] = value;
            }

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            PrimitiveParameter p = new PrimitiveParameter(propId);
            p.setTimestamp(timestamp);
            p.setUniqueIdentifier(uniqueIdentifer);
            p.setGranularity(entitySpec.getGranularity());
            p.setValue(cpVal);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                p.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            p.setDataSourceType(DatabaseDataSourceType
                    .getInstance(getDataSourceBackendId()));
            logger.log(Level.FINEST, "Created primitive parameter {0}", p);
            // Collections.putList(results, keyId, p);
            List<PrimitiveParameter> propList = results
                    .getPatientPropositions(keyId);
            if (propList == null) {
                propList = new ArrayList<PrimitiveParameter>();
            }
            propList.add(p);
            results.put(keyId, propList);
        }
    }
}
