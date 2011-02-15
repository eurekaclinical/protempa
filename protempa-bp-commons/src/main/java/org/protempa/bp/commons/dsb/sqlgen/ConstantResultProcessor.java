package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.DatabaseDataSourceType;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

class ConstantResultProcessor extends AbstractMainResultProcessor<Constant> {

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<Constant> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String[] propIds = entitySpec.getPropositionIds();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            codeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        Logger logger = SQLGenUtil.logger();
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Value[] propertyValues = new Value[propertySpecs.length];
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
            
            i = extractPropertyValues(propertySpecs, resultSet, i, propertyValues);
            
            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }
            
            Constant cp = new Constant(propId);
            cp.setUniqueIdentifier(uniqueIdentifer);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                cp.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            cp.setDataSourceType(
                DatabaseDataSourceType.getInstance(getDataSourceBackendId()));
            logger.log(Level.FINEST, "Created constant {0}", cp);
            List<Constant> propList = results.getPatientPropositions(keyId);
            if (propList == null) {
                propList = new ArrayList<Constant>(500);
            }
            propList.add(cp);
            results.put(keyId, propList);
        }
    }
}
