package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataSourceType;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFormat;
import org.protempa.proposition.value.ValueType;

class PrimitiveParameterResultProcessor extends
        MainResultProcessor<PrimitiveParameter> {

    private static final int FLUSH_SIZE = 100000;

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<PrimitiveParameter> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        boolean hasRefs = entitySpec.getReferenceSpecs().length > 0;
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

        int timestampColumnType = -1;
        boolean timestampColumnTypeSet = false;
        DataSourceType dsType =
                DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        while (resultSet.next()) {
            int i = 1;

            String keyId = resultSet.getString(i++);
            
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifer = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    assert propIds.length == 1: "Don't know which proposition id to assign to";
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

            if (!timestampColumnTypeSet) {
                timestampColumnType = resultSetMetaData.getColumnType(i);
                timestampColumnTypeSet = true;
            }

            Long timestamp = null;
            try {
                timestamp = entitySpec.getPositionParser().toLong(resultSet,
                        i, timestampColumnType);
                i++;
            } catch (SQLException e) {
                logger.log(Level.WARNING,
                        "Could not parse timestamp. Leaving timestamp unset.", e);
            }
            
            ValueType valueType = entitySpec.getValueType();
            String cpValStr = resultSet.getString(i++);
            Value cpVal = ValueFormat.parse(cpValStr, valueType);
            
            i = extractPropertyValues(propertySpecs, resultSet, i,
                    propertyValues);

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
            p.setDataSourceType(dsType);

            logger.log(Level.FINEST, "Created primitive parameter {0}", p);
            results.add(keyId, p);
            if (++count % FLUSH_SIZE == 0) {
                results.flush(hasRefs);
            }
        }
        results.flush(hasRefs);
    }

    
}
