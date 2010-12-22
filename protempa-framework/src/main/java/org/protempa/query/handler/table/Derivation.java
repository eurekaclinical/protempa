package org.protempa.query.handler.table;

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
public final class Derivation extends Link {

    public Derivation(String[] propositionIds) {
        this(propositionIds, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints) {
        this(propositionIds, constraints, null, -1, -1);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, 
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        
    }

    @Override
    String headerFragment() {
        return createHeaderFragment("derived");
    }



    @Override
    public Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        return createResults(derivations.get(proposition));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
