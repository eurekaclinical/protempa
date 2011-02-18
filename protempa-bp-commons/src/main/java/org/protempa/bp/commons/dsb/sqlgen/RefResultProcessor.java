package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        while (resultSet.next()) {
            int i = 1;
            String[] uniqueIds = new String[entitySpec.getUniqueIdSpecs().length];
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifier = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);
            String[] refUniqueIds = new String[this.referenceSpec
                    .getUniqueIdSpecs().length];
            i = readUniqueIds(refUniqueIds, resultSet, i);
            UniqueIdentifier refUniqueIdentifier = generateUniqueIdentifier(
                    this.referenceSpec.getEntityName(), refUniqueIds);
            addToReferences(uniqueIdentifier, refUniqueIdentifier);
            if (++count % FLUSH_SIZE == 0) {
                this.cache.flushReferences();
            }
        }
        this.cache.flushReferences();
    }

    private final void addToReferences(UniqueIdentifier uniqueIdentifier,
            UniqueIdentifier refUniqueIdentifier) {
        P proposition = this.cache.addReference(uniqueIdentifier, refUniqueIdentifier);
        assert proposition != null : "No proposition for unique identifier "
                + uniqueIdentifier;
        addReferenceForProposition(this.referenceSpec.getReferenceName(),
                proposition, refUniqueIdentifier);
    }

    abstract void addReferenceForProposition(String referenceName,
            P proposition, UniqueIdentifier uid);

    final void setCache(ResultCache<P> cache) {
        this.cache = cache;
    }

    final ResultCache<P> getCache() {
        return this.cache;
    }

    final ReferenceSpec getReferenceSpec() {
        return this.referenceSpec;
    }

    final void setReferenceSpec(ReferenceSpec referenceSpec) {
        this.referenceSpec = referenceSpec;
    }
}
