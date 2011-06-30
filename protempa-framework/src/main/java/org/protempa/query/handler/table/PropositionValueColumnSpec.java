package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Creates a column with an aggregation of primitive parameters with numerical
 * values.
 *
 * @author Andrew Post
 */
public class PropositionValueColumnSpec extends AbstractTableColumnSpec {

    

    public enum Type {

        MAX,
        MIN,
        FIRST,
        LAST
    }
    private final Link[] links;
    private final String columnNamePrefixOverride;
    private Type type;

    public PropositionValueColumnSpec(Link[] links, Type aggregationType) {
        this(null, links, aggregationType);
    }

    public PropositionValueColumnSpec(String columnNamePrefixOverride,
            Link[] links,
            Type aggregationType) {

        if (links == null) {
            this.links = Util.EMPTY_LINK_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }

        //check metadata for compatibility (at the end of the links are all
        //primitive parameters with numerical values).

        if (aggregationType == null) {
            throw new IllegalArgumentException("aggregationType cannot be null");
        }
        this.type = aggregationType;
        this.columnNamePrefixOverride = columnNamePrefixOverride;
    }

    @Override
    public String[] columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        Collection<Proposition> propositions = traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations, 
                references, knowledgeSource);
        Value value = null;
        if (type == Type.FIRST) {
            propositions = new ArrayList(propositions);
            Collections.sort((List) propositions,
                    new AllPropositionIntervalComparator());
        } else if (type == Type.LAST) {
            propositions = new ArrayList(propositions);
            Collections.sort((List) propositions,
                    Collections.reverseOrder(
                    new AllPropositionIntervalComparator()));
        }
        for (Proposition prop : propositions) {
            if (prop instanceof TemporalParameter) {
                TemporalParameter pp = (TemporalParameter) prop;
                Value val = pp.getValue();
                if (val != null) {
                    if ((type == Type.MAX || type == Type.MIN) && !ValueType.NUMERICALVALUE.isInstance(val)) {
                        continue;
                    } else {
                        if (value == null) {
                            value = val;
                            if (type == Type.FIRST || type == Type.LAST) {
                                break;
                            }
                        } else {
                            switch (type) {
                                case MAX:
                                    if (val.compare(value).is(ValueComparator.GREATER_THAN)) {
                                        value = val;
                                    }
                                    break;
                                case MIN:
                                    if (val.compare(value).is(ValueComparator.LESS_THAN)) {
                                        value = val;
                                    }
                                    break;
                                default:
                                    throw new AssertionError("invalid aggregation type: " + type);

                            }
                        }
                    }
                }
            } else {
                throw new IllegalStateException("only primitive parameters allowed");
            }
        }
        if (value != null) {
            return new String[]{value.getFormatted()};
        } else {
            return new String[]{null};
        }
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        String headerString =
                this.columnNamePrefixOverride != null
                ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links)
                + (this.type == Type.MIN ? "_min"
                : "_max");
        return new String[]{headerString};
    }
}
