package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Counts the number of instances of a proposition.
 * 
 * @author arpost
 */
public final class CountColumnSpec extends AbstractTableColumnSpec {

    private final String columnNameOverride;
    private final Link[] links;
    private final boolean countUnique;

    public CountColumnSpec(Link[] links) {
        this(null, links, false);
    }

    /**
     * Constructs a count column spec with an optional header name, the links
     * to traverse and whether to count the number of unique proposition ids
     * rather than the number of propositions.
     * 
     * @param columnNameOverride an optional header name. Will use a default
     * that is computed from the specified links (below).
     * @param links the links to traverse.
     * @param countUnique if <code>true</code>, count the unique ids of the 
     * propositions at the end of the link traversal.
     */
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
        Logger logger = Util.logger();
        Collection<Proposition> props = traverseLinks(this.links, proposition,
                forwardDerivations, backwardDerivations, references, 
                knowledgeSource);
        if (this.countUnique) {
            Set<String> result = new HashSet<String>();
            for (Proposition p : props) {
                String pId = p.getId();
                logger.log(Level.FINEST,
                        "Looking at proposition id {0}", pId);
                if (result.add(pId)) {
                    logger.log(Level.FINEST,
                            "Adding to count: {0}", pId);
                }
            }
            return new String[] { "" + result.size() };
        } else {
            return new String[] { "" + props.size() };
        }
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

	public String getColumnNameOverride() {
		return columnNameOverride;
	}

	public Link[] getLinks() {
		return links;
	}

	public boolean isCountUnique() {
		return countUnique;
	}
}
