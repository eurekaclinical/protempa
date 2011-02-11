package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.proposition.Proposition;

/**
 * 
 * @author Andrew Post
 */
abstract class SQLGenResultProcessorFactory<P extends Proposition> {
    abstract AbstractMainResultProcessor<P> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ResultCache<P> cache);

    abstract RefResultProcessor<P> getRefInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            ResultCache<P> cache);
}
