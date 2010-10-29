package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public final class CountColumnSpec extends AbstractTableColumnSpec {

    private final String columnNameOverride;
    private final Link[] links;

    public CountColumnSpec(Link[] links) {
        this(null, links);
    }

    public CountColumnSpec(String columnNameOverride, Link[] links) {
        ProtempaUtil.checkArray(links, "links");
        this.links = links.clone();
        this.columnNameOverride = columnNameOverride;
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        StringBuilder builder = new StringBuilder();
        if (this.columnNameOverride != null) {
            builder.append(this.columnNameOverride);
        } else {
            builder.append("count(");
            builder.append(generateLinksHeaderString(this.links));
            builder.append(')');
        }
        return new String[]{builder.toString()};
    }

    @Override
    public String[] columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        Collection<Proposition> props = traverseLinks(this.links, proposition,
                derivations, references, knowledgeSource);
        return new String[]{"" + props.size()};
    }
}