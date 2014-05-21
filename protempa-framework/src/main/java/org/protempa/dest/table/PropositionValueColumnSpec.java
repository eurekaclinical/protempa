/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.dest.table;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.string.StringUtil;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.NumericalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Creates a column with an aggregation of primitive parameters with numerical
 * values. For MAX and MIN on data with {@link InequalityNumberValue}s for which
 * a comparison between the inequality number and another number is not
 * possible, we convert to {@link NumberValue}s before comparison. For example,
 * given two numbers
 * <code>< 4</code> and
 * <code>3</code>, we convert
 * <code>< 4</code> to
 * <code>4</code> before aggregation because otherwise the two numbers are not
 * comparable.
 *
 * @author Andrew Post
 */
public class PropositionValueColumnSpec extends AbstractTableColumnSpec {

    private static final Comparator<? super Proposition> comp = new AllPropositionIntervalComparator();
    private static final Comparator<? super Proposition> reverseComp = Collections
            .reverseOrder(comp);

    public enum Type {

        MAX, MIN, FIRST, LAST, SUM, AVG, MEDIAN
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
    public void columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource, Map<String, String> replace,
            char delimiter, Writer writer) throws KnowledgeSourceReadException,
            IOException {
        List<Proposition> propositions = traverseLinks(this.links, proposition,
                forwardDerivations, backwardDerivations, references,
                knowledgeSource);
        Value value = null;
        BigDecimal sumTotal = null;
        BigInteger count = BigInteger.ZERO;
        List<NumberValue> orderStats = new ArrayList<>();

        /*
         * Sort if needed.
         */
        if (propositions.size() > 1) {
            switch (this.type) {
                case FIRST:
                    propositions = new ArrayList<>(propositions);
                    Collections.sort(propositions, comp);
                    break;
                case LAST:
                    propositions = new ArrayList<>(propositions);
                    Collections.sort(propositions, reverseComp);
                    break;
                case SUM:
                // fall through
                case AVG:
                // fall through
                case MEDIAN:
                // fall through
                case MIN:
                // fall through
                case MAX:
                    break;
                default:
                    throw new AssertionError("Invalid aggregation type "
                            + this.type);
            }
        }

        LOOP_PROPOSITIONS:
        for (Proposition prop : propositions) {
            if (prop instanceof TemporalParameter) {
                TemporalParameter pp = (TemporalParameter) prop;
                Value val = pp.getValue();
                if (val != null) {
                    /*
                     * Check type preconditions.
                     */
                    switch (this.type) {
                        case MAX:
                        case MIN:
                            if (!ValueType.NUMERICALVALUE.isInstance(val)) {
                                continue LOOP_PROPOSITIONS;
                            }
                            break;
                        case MEDIAN:
                        // fall through
                        case AVG:
                        // fall through
                        case SUM:
                            if (!ValueType.NUMBERVALUE.isInstance(val)) {
                                continue LOOP_PROPOSITIONS;
                            }
                            break;
                        case FIRST:
                        // fall through
                        case LAST:
                            break;
                        default:
                            throw new AssertionError("Invalid aggregation type "
                                    + this.type);
                    }

                    /*
                     * Process first value.
                     */
                    if (value == null && sumTotal == null) {
                        switch (this.type) {
                            case FIRST:
                            // fall through
                            case LAST:
                                value = val;
                                break LOOP_PROPOSITIONS;
                            case MEDIAN:
                                orderStats.add((NumberValue) val);
                                break;
                            case AVG:
                                count = BigInteger.ONE;
                            // fall through
                            case SUM:
                                sumTotal = ((NumberValue) val).getBigDecimal();
                                break;
                            case MAX:
                            // fall through
                            case MIN:
                                value = val;
                                break;
                            default:
                                throw new AssertionError(
                                        "Invalid aggregation type " + this.type);
                        }
                        /*
                         * Process subsequent values.
                         */
                    } else {
                        switch (this.type) {
                            case MAX:
                                ValueComparator c = val.compare(value);
                                if (c.includes(ValueComparator.UNKNOWN)) {
                                    c = ((NumericalValue) val).getNumberValue()
                                            .compare(
                                            ((NumericalValue) value)
                                            .getNumberValue());
                                }
                                if (c.includes(ValueComparator.GREATER_THAN)) {
                                    value = val;
                                }
                                break;
                            case MIN:
                                c = val.compare(value);
                                if (c.includes(ValueComparator.UNKNOWN)) {
                                    c = ((NumericalValue) val).getNumberValue()
                                            .compare(
                                            ((NumericalValue) value)
                                            .getNumberValue());
                                }
                                if (c.includes(ValueComparator.LESS_THAN)) {
                                    value = val;
                                }
                                break;
                            case MEDIAN:
                                orderStats.add((NumberValue) val);
                                break;
                            case AVG:
                                count.add(BigInteger.ONE);
                            // fall through
                            case SUM:
                                try {
                                    sumTotal = sumTotal.add(((NumberValue) val)
                                            .getBigDecimal());
                                } catch (NumberFormatException ex) {
                                    throw new IllegalStateException(
                                            "only number values allowed for SUM or AVG aggregation type; got "
                                            + val + " for proposition "
                                            + pp.getId() + " instead");
                                }
                                break;
                            case FIRST:
                            // fall through
                            case LAST:
                                break;
                            default:
                                throw new AssertionError(
                                        "Invalid aggregation type " + this.type);
                        }
                    }
                }
            } else {
                throw new IllegalStateException(
                        "Only temporal parameters allowed");
            }
        }

        if (sumTotal != null) {
            if (this.type == Type.AVG) {
                StringUtil.escapeAndWriteDelimitedColumn(NumberValue.getInstance(
                    sumTotal.divide(BigDecimal.valueOf(count.longValue())))
                    .getFormatted(), delimiter, replace, writer);
            } else {
                StringUtil.escapeAndWriteDelimitedColumn(NumberValue.getInstance(sumTotal)
                    .getFormatted(), delimiter, replace, writer);
            }
        } else if (this.type == Type.MEDIAN) {
            NumberValue median = medianValue(orderStats);
            if (median != null) {
                StringUtil.escapeAndWriteDelimitedColumn(median.getFormatted(), delimiter, replace, writer);
            } else {
                StringUtil.escapeAndWriteDelimitedColumn(null, delimiter, replace, writer);
            }
        } else if (value != null) {
            StringUtil.escapeAndWriteDelimitedColumn(value.getFormatted(), delimiter, replace, writer);
        } else {
            StringUtil.escapeAndWriteDelimitedColumn(null, delimiter, replace, writer);
        }
    }

    private NumberValue medianValue(List<NumberValue> values) {
        Collections.sort(values);
        if (values.size() == 0) {
            return null;
        } else if (values.size() % 2 == 1) {
            return values.get((1 + values.size()) / 2);
        } else {
            // median of an even-length list is the mean of the 2 median values
            NumberValue median1 = values.get(values.size() / 2);
            NumberValue median2 = values.get(values.size() / 2 + 1);
            return NumberValue
                    .getInstance(median1.getBigDecimal()
                    .add(median2.getBigDecimal())
                    .divide(BigDecimal.valueOf(2)));
        }
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        String headerString = this.columnNamePrefixOverride != null ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links)
                + (this.type == Type.MIN ? "_min" : "_max");
        return new String[]{headerString};
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws TableColumnSpecValidationFailedException,
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Link[] getLinks() {
        return links;
    }

    public String getColumnNamePrefixOverride() {
        return columnNamePrefixOverride;
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource,
            String[] inPropIds) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<>();
        for (Link link : this.links) {
            inPropIds = link.getInferredPropositionIds(knowledgeSource,
                    inPropIds);
            org.arp.javautil.arrays.Arrays.addAll(result, inPropIds);
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((columnNamePrefixOverride == null) ? 0
                : columnNamePrefixOverride.hashCode());
        result = prime * result + Arrays.hashCode(links);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PropositionValueColumnSpec other = (PropositionValueColumnSpec) obj;
        if (columnNamePrefixOverride == null) {
            if (other.columnNamePrefixOverride != null) {
                return false;
            }
        } else if (!columnNamePrefixOverride
                .equals(other.columnNamePrefixOverride)) {
            return false;
        }
        if (!Arrays.equals(links, other.links)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }
}
