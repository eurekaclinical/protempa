package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.NumericalValue;
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

    public enum AggregationType {

        MAX,
        MIN
    }
    private final Link[] links;
    private final String columnNamePrefixOverride;
    private AggregationType aggregationType;

    public PropositionValueColumnSpec(Link[] links,
            AggregationType aggregationType) {
        this(null, links, aggregationType);
    }

    public PropositionValueColumnSpec(String columnNamePrefixOverride,
            Link[] links,
            AggregationType aggregationType) {

        if (links == null) {
            this.links = new Link[0];
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }

        //check metadata for compatibility (at the end of the links are all
        //primitive parameters with numerical values).

        if (aggregationType == null) {
            throw new IllegalArgumentException("aggregationType cannot be null");
        }
        this.aggregationType = aggregationType;
        this.columnNamePrefixOverride = columnNamePrefixOverride;
    }

    @Override
    public String[] columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        Collection<Proposition> propositions = traverseLinks(this.links,
                proposition, derivations, references, knowledgeSource);
        NumericalValue value = null;
        for (Proposition prop : propositions) {
            if (prop instanceof PrimitiveParameter) {
                PrimitiveParameter pp = (PrimitiveParameter) prop;
                Value val = pp.getValue();
                if (val != null) {
                    if (!ValueType.NUMERICALVALUE.isInstance(val)) {
                        throw new IllegalStateException("only numerical values allowed");
                    } else {
                        NumericalValue nv = (NumericalValue) val;
                        if (value == null) {
                            value = nv;
                        } else if (aggregationType == AggregationType.MAX) {
                            if (nv.compare(value).is(ValueComparator.GREATER_THAN)) {
                                value = nv;
                            }
                        } else {
                            if (nv.compare(value).is(ValueComparator.LESS_THAN)) {
                                value = nv;
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
                + (this.aggregationType == AggregationType.MIN ? "_min"
                : "_max");
        return new String[]{headerString};
    }
}
