package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
public final class Reference extends Link {
    private final String referenceName;
    

    public Reference(String referenceName) {
        this(referenceName, null);
    }

    public Reference(String referenceName, String[] propositionIds) {
        this(referenceName, propositionIds, null);
    }

    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints) {
        this(referenceName, propositionIds, constraints, null, -1, -1);
    }

    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index) {
        this(referenceName, propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1);
    }

    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (referenceName == null)
            throw new IllegalArgumentException("referenceName cannot be null");
        this.referenceName = referenceName;
    }

    public String getReferenceName() {
        return this.referenceName;
    }

    @Override
    String headerFragment() {
        return createHeaderFragment(this.referenceName);
    }

    @Override
    public Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        List<UniqueIdentifier> uids = proposition.getReferences(
                this.referenceName);
        List<Proposition> props = new ArrayList<Proposition>();
        for (UniqueIdentifier uid : uids) {
            Proposition prop = references.get(uid);
	    assert prop != null : "prop cannot be null";
            props.add(prop);
        }

        return createResults(props);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
