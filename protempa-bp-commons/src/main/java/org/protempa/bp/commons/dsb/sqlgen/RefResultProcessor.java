package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

abstract class RefResultProcessor<P extends Proposition> extends
        AbstractResultProcessor {

    private static final int FLUSH_SIZE = 25000;

    private ReferenceSpec referenceSpec;
    private ResultCache<P> cache;

    protected RefResultProcessor() {
    }

    @Override
    public final void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        int count = 0;
        String[] uniqueIds = new String[entitySpec.getUniqueIdSpecs().length];
        String[] refUniqueIds = new String[this.referenceSpec
                    .getUniqueIdSpecs().length];
        while (resultSet.next()) {
            int i = 1;
            
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifier = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);
            
            i = readUniqueIds(refUniqueIds, resultSet, i);
            UniqueIdentifier refUniqueIdentifier = generateUniqueIdentifier(
                    this.referenceSpec.getEntityName(), refUniqueIds);
            //addToReferences(uniqueIdentifier, refUniqueIdentifier);
            cache.addReference(uniqueIdentifier, refUniqueIdentifier);
            if (++count % FLUSH_SIZE == 0) {
                this.cache.flushReferences();
            }
        }
        this.cache.flushReferences();
        this.cache.flushReferencesFull(this);
    }

//    private final void addToReferences(UniqueIdentifier uniqueIdentifier,
//            UniqueIdentifier refUniqueIdentifier) {
//        P proposition =
//                this.cache.uidToProposition(uniqueIdentifier);
//        assert proposition != null : "No proposition for unique identifier "
//                + uniqueIdentifier;
//        addReference(proposition, this.referenceSpec.getReferenceName(),
//                refUniqueIdentifier);
//    }

    abstract void addReferences(P proposition, List<UniqueIdentifier> uids);

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
