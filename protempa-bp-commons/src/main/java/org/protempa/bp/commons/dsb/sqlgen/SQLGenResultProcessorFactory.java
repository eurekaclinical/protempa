package org.protempa.bp.commons.dsb.sqlgen;

import java.util.Map;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
abstract class SQLGenResultProcessorFactory<P extends Proposition> {
    abstract AbstractMainResultProcessor<P> getInstance(
            String dataSourceBackendId,
            EntitySpec entitySpec);

    abstract RefResultProcessor<P> getRefInstance(
            String dataSourceBackendId,
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,P> cache);
}
