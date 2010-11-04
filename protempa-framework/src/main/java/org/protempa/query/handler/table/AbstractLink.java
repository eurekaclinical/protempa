package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractLink implements Link {

    private final String[] propositionIds;
    private final PropertyConstraint[] constraints;
    private final Comparator<Proposition> comparator;
    private final int fromIndex;
    private final int toIndex;

    AbstractLink(String[] propositionIds, PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        if (propositionIds == null) {
            this.propositionIds = new String[0];
        } else {
            ProtempaUtil.checkArrayForNullElement(propositionIds,
                    "propositionIds");
            this.propositionIds = propositionIds.clone();
        }
        if (constraints == null) {
            this.constraints = new PropertyConstraint[0];
        } else {
            ProtempaUtil.checkArrayForNullElement(constraints, "constraints");
            this.constraints = constraints.clone();
        }
        this.comparator = comparator;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public final String[] getPropositionIds() {
        return this.propositionIds.clone();
    }

    public final PropertyConstraint[] getConstraints() {
        return this.constraints.clone();
    }

    public final Comparator<Proposition> getComparator() {
        return this.comparator;
    }

    public final int getFromIndex() {
        return this.fromIndex;
    }

    public final int getToIndex() {
        return this.toIndex;
    }

    final String createHeaderFragment(String ref) {
        boolean sep1Needed = this.propositionIds.length > 0
                && this.constraints.length > 0;
        String sep1 = sep1Needed ? ", " : "";
        String id = this.propositionIds.length > 0 ? "id=" : "";
        boolean parenNeeded = this.propositionIds.length > 0
                || this.constraints.length > 0;
        String startParen = parenNeeded ? "(" : "";
        String finishParen = parenNeeded ? ")" : "";

        String range = rangeString();

        boolean sep2Needed = sep1Needed && range.length() > 0;
        String sep2 = sep2Needed ? ", " : "";

        return '.' + ref + startParen + id +
                StringUtils.join(this.propositionIds, ',') + sep1 +
                constraintHeaderString(this.constraints) + finishParen +
                sep2 + range;
    }

    final List<Proposition> createResults(Collection<Proposition> propositions) {
        List<Proposition> result = new ArrayList<Proposition>();
        for (Proposition derivedProp : propositions) {
            addToResults(derivedProp, result);
        }
        
        result = filterResults(result);

        return result;
    }
    
    private String constraintHeaderString(PropertyConstraint[] constraints) {
        List<String> constraintsL = new ArrayList<String>(constraints.length);
        for (int i = 0; i < constraints.length; i++) {
            PropertyConstraint ccc = constraints[i];
            constraintsL.add(ccc.getFormatted());
        }
        return StringUtils.join(constraintsL, ',');
    }

    private String rangeString() {
        boolean rangeSpecified = this.fromIndex >= 0 || this.toIndex >= 0;
        String range = rangeSpecified ? "range=" : "";
        if (rangeSpecified) {
            if (this.fromIndex >= 0) {
                range += this.fromIndex;
            } else {
                range += 0;
            }
            range += ",";
            if (this.toIndex >= 0) {
                range += this.toIndex;
            } else {
                range += "end";
            }
        }
        return range;
    }

    private List<Proposition> filterResults(List<Proposition> result) {
        if (this.comparator != null) {
            Collections.sort(result, this.comparator);
        }
        if (this.fromIndex >= 0 || this.toIndex >= 0) {
            return result.subList(this.fromIndex >= 0 ? this.fromIndex : 0,
                    this.toIndex >= 0 ? this.toIndex : result.size());
        } else {
            return result;
        }
    }

    private void addToResults(Proposition prop, Collection<Proposition> result) {
        if (this.propositionIds.length == 0
                || Arrays.contains(this.propositionIds, prop.getId())) {
            boolean compatible = constraintsCheckCompatible(prop,
                    this.constraints);
            if (compatible) {
                result.add(prop);
            }
        }
    }

    private boolean constraintsCheckCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            PropertyConstraint ccc = constraints[i];
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.contains(value.compare(ccc.getValue()))) {
                return false;
            }
        }
        return true;
    }
}
