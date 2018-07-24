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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
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
    public void columnValues(String key, Proposition proposition,
            Map<Proposition, Set<Proposition>> forwardDerivations,
            Map<Proposition, Set<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSourceCache ksCache,
            TabularWriter writer) throws TabularWriterException {
        Logger logger = Util.logger();
        Collection<Proposition> props = traverseLinks(this.links, proposition,
                forwardDerivations, backwardDerivations, references, 
                ksCache);
        if (this.countUnique) {
            Set<String> result = new HashSet<>();
            for (Proposition p : props) {
                String pId = p.getId();
                logger.log(Level.FINEST,
                        "Looking at proposition id {0}", pId);
                if (result.add(pId)) {
                    logger.log(Level.FINEST,
                            "Adding to count: {0}", pId);
                }
            }
            writer.writeString("" + result.size());
        } else {
            writer.writeString("" + props.size());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnNameOverride == null) ? 0 : columnNameOverride.hashCode());
		result = prime * result + (countUnique ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(links);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CountColumnSpec other = (CountColumnSpec) obj;
		if (columnNameOverride == null) {
			if (other.columnNameOverride != null)
				return false;
		} else if (!columnNameOverride.equals(other.columnNameOverride))
			return false;
		if (countUnique != other.countUnique)
			return false;
		if (!Arrays.equals(links, other.links))
			return false;
		return true;
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
}
