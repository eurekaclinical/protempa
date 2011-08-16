package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

public final class CountColumnSpec extends AbstractTableColumnSpec {

    private final String columnNameOverride;
    private final Link[] links;
    private final boolean countUnique;

    public CountColumnSpec(Link[] links) {
        this(null, links, false);
    }

    public CountColumnSpec(String columnNameOverride, Link[] links,
            boolean countUnique) {
        ProtempaUtil.checkArray(links, "links");
        this.links = links.clone();
        this.columnNameOverride = columnNameOverride;
        this.countUnique = countUnique;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        StringBuilder builder = new StringBuilder();
        if (this.columnNameOverride != null) {
            builder.append(this.columnNameOverride);
        } else {
            if (this.countUnique) {
                builder.append("countUnique(");
            } else {
                builder.append("count(");
            }
            builder.append(generateLinksHeaderString(this.links));
            builder.append(')');
        }
        return new String[] { builder.toString() };
    }

    @Override
    public String[] columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<String>();
        Collection<Proposition> props = traverseLinks(this.links, proposition,
                forwardDerivations, backwardDerivations, references, 
                knowledgeSource);
        if (this.countUnique) {
            for (Proposition p : props) {
                Util.logger().log(Level.FINEST,
                        "Looking at proposition id " + p.getId());
                if (!result.contains(p.getId())) {
                    Util.logger().log(Level.FINEST,
                            "Adding to count: " + p.getId());
                    result.add(p.getId());
                }
            }
            return new String[] { "" + result.size() };
        } else {
            return new String[] { "" + props.size() };
        }
    }
}
