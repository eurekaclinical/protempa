package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.proposition.Proposition;

/**
 * 
 * @author Andrew Post
 */
abstract class SQLGenResultProcessorFactory<P extends Proposition> {
    abstract MainResultProcessor<P> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ResultCache<P> cache);

    abstract RefResultProcessor<P> getRefInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            ResultCache<P> cache);
}