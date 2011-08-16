package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.log.Logging;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

abstract class RefResultProcessor<P extends Proposition> extends AbstractResultProcessor {

    private static final int FLUSH_SIZE = 2000000;
    private ReferenceSpec referenceSpec;
    private ResultCache<P> cache;

    protected RefResultProcessor() {
    }

    @Override
    public final void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        int count = 0;
        String[] uniqueIds = new String[entitySpec.getUniqueIdSpecs().length];
        String[] refUniqueIds =
                new String[this.referenceSpec.getUniqueIdSpecs().length];
        UniqueId uniqueIdentifier = null;
        String[] uniqueIdsCopy = new String[uniqueIds.length];
        String entitySpecName = entitySpec.getName();
        String refSpecEntityName = this.referenceSpec.getEntityName();
        Logger logger = SQLGenUtil.logger();
        
        while (resultSet.next()) {
            int i = 1;

            i = readUniqueIds(uniqueIds, resultSet, i);
            if (org.arp.javautil.arrays.Arrays.contains(uniqueIds, null)) {
                /**
                 * We skip records with null values in the unique id field, and
                 * fail if appropriate in 
                 * Event/Constant/PrimitiveParameterResultProcessor. We need to
                 * do this because it is okay to include a potentially null
                 * column as part of the unique id when the column is the
                 * keyid.
                 */
                continue;
            }
            if (uniqueIdentifier == null ||
                    !Arrays.equals(uniqueIds, uniqueIdsCopy)) {
                uniqueIdentifier = generateUniqueIdentifier(
                        entitySpecName, uniqueIds);
                System.arraycopy(uniqueIds, 0, uniqueIdsCopy, 0,
                        uniqueIds.length);
            }

            i = readUniqueIds(refUniqueIds, resultSet, i);
            if (org.arp.javautil.arrays.Arrays.contains(refUniqueIds, null)) {
                /**
                 * We skip records with null values in the unique id field, and
                 * fail if appropriate in 
                 * Event/Constant/PrimitiveParameterResultProcessor. We need to
                 * do this because it is okay to include a potentially null
                 * column as part of the unique id when the column is the
                 * keyid.
                 */
                continue;
            }
            UniqueId refUniqueIdentifier = generateUniqueIdentifier(
                    refSpecEntityName, refUniqueIds);
            this.cache.addReference(uniqueIdentifier, refUniqueIdentifier);
            if (++count % FLUSH_SIZE == 0) {
                this.cache.flushReferences(this);
                if (logger.isLoggable(Level.FINE)) {
                    Logging.logCount(logger, Level.FINE, count, 
                            "Retrieved {0} reference",
                            "Retrieved {0} references");
                }
            }
        }
        this.cache.flushReferences(this);
        //this.cache.flushReferencesFull(this);
        if (logger.isLoggable(Level.FINE)) {
            Logging.logCount(logger, Level.FINE, count, 
                            "Retrieved {0} reference total",
                            "Retrieved {0} references total");
        }
        
    }

    abstract void addReferences(P proposition, List<UniqueId> uids);

    final void setCache(ResultCache<P> cache) {
        assert cache != null : "cache cannot be null";
        this.cache = cache;
    }

    final ResultCache<P> getCache() {
        return this.cache;
    }

    final ReferenceSpec getReferenceSpec() {
        return this.referenceSpec;
    }

    final void setReferenceSpec(ReferenceSpec referenceSpec) {
        assert referenceSpec != null : "referenceSpec cannot be null";
        this.referenceSpec = referenceSpec;
    }
}
