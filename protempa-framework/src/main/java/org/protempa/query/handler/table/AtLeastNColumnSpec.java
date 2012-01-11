package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

public final class AtLeastNColumnSpec extends AbstractTableColumnSpec {
    private final int n;
    private final Link[] links;
    private final String columnNameOverride;
    private final String trueOutput;
    private final String falseOutput;

    public AtLeastNColumnSpec(int n, Link[] links) {
        this(null, n, links);
    }

    public AtLeastNColumnSpec(String columnNameOverride, int n, Link[] links) {
        this(columnNameOverride, n, links, "true", "false");
    }
    
    public AtLeastNColumnSpec(String columnNameOverride, int n, Link[] links, 
            String trueOutput, String falseOutput) {
        this.n = n;
        ProtempaUtil.checkArray(links, "links");
        this.links = links.clone();
        this.columnNameOverride = columnNameOverride;        
        this.trueOutput = trueOutput;
        this.falseOutput = falseOutput;
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
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        Collection<Proposition> props = traverseLinks(this.links, proposition,
                forwardDerivations, backwardDerivations, references, 
                knowledgeSource);
        return new String[]{props.size() >= this.n ? this.trueOutput : this.falseOutput};
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

	public int getN() {
		return n;
	}

	public Link[] getLinks() {
		return links;
	}

	public String getColumnNameOverride() {
		return columnNameOverride;
	}

	public String getTrueOutput() {
		return trueOutput;
	}

	public String getFalseOutput() {
		return falseOutput;
	}
}
