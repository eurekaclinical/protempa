package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public final class AtLeastNColumnSpec extends AbstractTableColumnSpec {
    private final int n;
    private final Link[] links;
    private final String columnNameOverride;

    public AtLeastNColumnSpec(int n, Link[] links) {
        this(null, n, links);
    }

    public AtLeastNColumnSpec(String columnNameOverride, int n, Link[] links) {
        this.n = n;
        ProtempaUtil.checkArray(links, "links");
        this.links = links.clone();
        this.columnNameOverride = columnNameOverride;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        StringBuilder builder = new StringBuilder();
        if (this.columnNameOverride != null) {
            builder.append(this.columnNameOverride);
        } else {
            builder.append("atleast");
            builder.append(this.n);
            builder.append('(');
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
        return new String[]{props.size() >= this.n ? "true" : "false"};
    }
}
