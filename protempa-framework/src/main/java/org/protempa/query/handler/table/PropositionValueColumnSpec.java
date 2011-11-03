package org.protempa.query.handler.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.NumericalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Creates a column with an aggregation of primitive parameters with numerical
 * values. For MAX and MIN on data with {@link InequalityNumberValue}s for
 * which a comparison between the inequality number and another number is
 * not possible, we convert to {@link NumberValue}s before comparison. For 
 * example, given two numbers <code>< 4</code> and <code>3</code>, we convert 
 * <code>< 4</code> to <code>4</code> before aggregation because otherwise the 
 * two numbers are not comparable.
 * 
 * @author Andrew Post
 */
public class PropositionValueColumnSpec extends AbstractTableColumnSpec {
    
    private static final Comparator<? super Proposition> comp =
            new AllPropositionIntervalComparator();
    private static final Comparator<? super Proposition> reverseComp =
            Collections.reverseOrder(comp);

    public enum Type {

        MAX, MIN, FIRST, LAST, SUM
    }

    private final Link[] links;
    private final String columnNamePrefixOverride;
    private Type type;

    public PropositionValueColumnSpec(Link[] links, Type aggregationType) {
        this(null, links, aggregationType);
    }

    public PropositionValueColumnSpec(String columnNamePrefixOverride,
            Link[] links, Type aggregationType) {

        if (links == null) {
            this.links = Util.EMPTY_LINK_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }

        // check metadata for compatibility (at the end of the links are all
        // primitive parameters with numerical values).

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
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        List<Proposition> propositions = traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations, 
                references, knowledgeSource);
        NumericalValue value = null;
        BigDecimal sumTotal = new BigDecimal(0.0);
        if (type == Type.FIRST) {
            propositions = new ArrayList<Proposition>(propositions);
            Collections.sort(propositions, comp);
        } else if (type == Type.LAST) {
            propositions = new ArrayList<Proposition>(propositions);
            Collections.sort(propositions, reverseComp);
        }
        for (Proposition prop : propositions) {
            if (prop instanceof TemporalParameter) {
                TemporalParameter pp = (TemporalParameter) prop;
                Value v = pp.getValue();
                if (v != null) {
                    if ((type == Type.MAX || type == Type.MIN)
                            && !ValueType.NUMERICALVALUE.isInstance(v)) {
                        continue;
                    } else if (type == Type.SUM
                            && !ValueType.NUMBERVALUE.isInstance(v)) {
                        continue;
                    }
                    if ((type == Type.MAX || type == Type.MIN) && !ValueType.NUMERICALVALUE.isInstance(v)) {
                        continue;
                    } else {
                        NumericalValue val = (NumericalValue) v;
                        if (value == null) {
                            value = val;
                            if (type == Type.FIRST || type == Type.LAST) {
                                break;
                            }
                        } else {
                            ValueComparator c = val.compare(value);
                            boolean unknown = c.test(ValueComparator.UNKNOWN);
                            if (unknown) {
                                c = val.getNumberValue().compare(
                                        value.getNumberValue());
                            }
                            switch (type) {
                                case MAX:
                                    if (c.test(ValueComparator.GREATER_THAN)) {
                                        value = val;
                                    }
                                    break;
                                case MIN:
                                    if (c.test(ValueComparator.LESS_THAN)) {
                                        value = val;
                                    }
                                    break;
                                case SUM:
                                    try {
                                        sumTotal = sumTotal.add(val.getBigDecimal());
                                        value = NumberValue
                                                .getInstance(sumTotal);
                                    } catch (NumberFormatException ex) {
                                        throw new IllegalStateException(
                                                "only number values allowed for SUM aggregation type; got "
                                                        + val
                                                        + " for proposition "
                                                        + pp.getId()
                                                        + " instead");
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
            return new String[] { value.getFormatted() };
        } else {
            return new String[]{null};
        }
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        String headerString = this.columnNamePrefixOverride != null ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links)
                        + (this.type == Type.MIN ? "_min" : "_max");
        return new String[] { headerString };
    }
    
    @Override
    public void validate(KnowledgeSource knowledgeSource) throws 
            TableColumnSpecValidationFailedException, 
            KnowledgeSourceReadException {
        int i = 1;
        for (Link link : this.links) {
            try {
                link.validate(knowledgeSource);
            } catch (LinkValidationFailedException ex) {
                throw new TableColumnSpecValidationFailedException(
                        "Validation of link " + i + " failed", ex);
            }
            i++;
        }
    }
}
