package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.collections.Collections;
import org.protempa.DatabaseDataSourceType;
import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

class ConstantResultProcessor extends
        AbstractMainResultProcessor<Constant> {

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        Map<String, List<Constant>> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String[] propIds = entitySpec.getPropositionIds();
        Logger logger = SQLGenUtil.logger();
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);
            String[] uniqueIds = generateUniqueIdsArray(entitySpec);
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifer = generateUniqueIdentifier(
                    entitySpec, uniqueIds);
            String propId = null;
            if (!isCasePresent()) {
                if (propIds.length == 1) {
                    propId = propIds[0];
                } else {
                    propId = resultSet.getString(i++);
                }
            } else {
                i++;
            }
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
            Constant cp = new Constant(propId);
            cp.setUniqueIdentifier(uniqueIdentifer);
            cp.setDataSourceType(new DerivedDataSourceType());
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                cp.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            cp.setDataSourceType(
                    new DatabaseDataSourceType(getDataSourceBackendId()));
            logger.log(Level.FINEST, "Created constant {0}", cp);
            Collections.putList(results, keyId, cp);
        }
    }
}
