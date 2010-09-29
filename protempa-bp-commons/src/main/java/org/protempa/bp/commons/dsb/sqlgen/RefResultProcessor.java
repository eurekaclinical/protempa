package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.DataSourceBackendId;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

abstract class RefResultProcessor<P extends Proposition> extends
        AbstractResultProcessor {
    private ReferenceSpec referenceSpec;
    private Map<UniqueIdentifier, P> cache;
    private final Map<P, List<UniqueIdentifier>> references;

    protected RefResultProcessor() {
        this.references = new HashMap<P, List<UniqueIdentifier>>();
    }

    @Override
    public final void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        while (resultSet.next()) {
            int i = 1;
            String[] uniqueIds = generateUniqueIdsArray(entitySpec);
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifier = generateUniqueIdentifier(
                    entitySpec, uniqueIds);
            String[] refUniqueIds = generateRefUniqueIdsArray(this.referenceSpec);
            i = readUniqueIds(refUniqueIds, resultSet, i);
            UniqueIdentifier refUniqueIdentifier = generateRefUniqueIdentifier(
                    this.referenceSpec, refUniqueIds);
            addToReferences(uniqueIdentifier, refUniqueIdentifier);
        }
        setReferences();
    }

    private final void addToReferences(UniqueIdentifier uniqueIdentifier,
            UniqueIdentifier refUniqueIdentifier) {
        P proposition = this.cache.get(uniqueIdentifier);
        assert proposition != null : "No proposition for unique identifier " +
                uniqueIdentifier;
        List<UniqueIdentifier> l = this.references.get(proposition);
        if (l == null) {
            l = new ArrayList<UniqueIdentifier>();
            this.references.put(proposition, l);
        }
        l.add(refUniqueIdentifier);
    }

    private final void setReferences() {
        String referenceName = this.referenceSpec.getReferenceName();
        for (Map.Entry<P, List<UniqueIdentifier>> me : this.references
                .entrySet()) {
            setReferencesForProposition(referenceName, me.getKey(),
                    me.getValue());
        }
    }

    abstract void setReferencesForProposition(String referenceName,
            P proposition, List<UniqueIdentifier> uids);

    final void setCache(Map<UniqueIdentifier, P> cache) {
        this.cache = cache;
    }

    final Map<UniqueIdentifier, P> getCache() {
        return this.cache;
    }

    final ReferenceSpec getReferenceSpec() {
        return this.referenceSpec;
    }

    final void setReferenceSpec(ReferenceSpec referenceSpec) {
        this.referenceSpec = referenceSpec;
    }

    private final String[] generateRefUniqueIdsArray(ReferenceSpec referenceSpec) {
        return new String[referenceSpec.getUniqueIdSpecs().length];
    }

    private final UniqueIdentifier generateRefUniqueIdentifier(
            ReferenceSpec referenceSpec, String[] uniqueIds)
            throws SQLException {
        return new UniqueIdentifier(new DataSourceBackendId(
                getDataSourceBackendId()), new SQLGenUniqueIdentifier(
                referenceSpec.getEntityName(), uniqueIds));
    }
}
