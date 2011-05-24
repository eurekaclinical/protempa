package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public final class Derivation extends Link {

    private Value[] allowedValues;

    public Derivation(String[] propositionIds) {
        this(propositionIds, null, null);
    }

    public Derivation(String[] propositionIds, Value[] allowedValues) {
        this(propositionIds, null, allowedValues);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints) {
        this(propositionIds, constraints, null, -1, -1, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Value[] allowedValues) {
        this(propositionIds, constraints, null, -1, -1, allowedValues);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index,
            Value[] allowedValues) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, allowedValues);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);

    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex,
            Value[] allowedValues) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (allowedValues == null) {
            this.allowedValues = new Value[0];
        } else {
            this.allowedValues = allowedValues.clone();
        }
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
        return createResults(
                filterAllowedValues(derivations.get(proposition)));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private Collection<Proposition> filterAllowedValues(
            List<Proposition> propositions) {
        if (propositions == null) {
            return null;
        } else if (this.allowedValues.length == 0) {
            return propositions;
        } else {
            List<Proposition> result =
                    new ArrayList<Proposition>(propositions.size());
            for (Proposition prop : propositions) {
                if (prop instanceof Parameter
                        && Arrays.contains(this.allowedValues,
                        ((Parameter) prop).getValue())) {
                    result.add(prop);
                }
            }
            return result;
        }
    }
}
