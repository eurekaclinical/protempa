package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
class ConstantResultProcessorFactory
        extends SQLGenResultProcessorFactory<Constant> {

    @Override
    AbstractMainResultProcessor<Constant> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        Map<String, List<Constant>> results =
                new HashMap<String, List<Constant>>();

        ConstantResultProcessor resultProcessor =
                new ConstantResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<Constant> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,Constant> cache) {
        ConstantRefResultProcessor resultProcessor =
                new ConstantRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
