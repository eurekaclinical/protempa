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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.string.StringUtil;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionUtil;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.Unit;

/**
 *
 * @author Andrew Post
 */
public final class DistanceBetweenColumnSpec extends AbstractTableColumnSpec {
    
    private static final Comparator<Proposition> comp =
            new AllPropositionIntervalComparator();
    
    private final Link[] links;
    private final String columnNamePrefixOverride;
    private final Unit units;
    
    public DistanceBetweenColumnSpec(Link[] links) {
        this(null, links);
    }
    
    public DistanceBetweenColumnSpec(String columnNamePrefixOverride, 
            Link[] links) {
        this(columnNamePrefixOverride, links, null);
    }
    
    public DistanceBetweenColumnSpec(String columnNamePrefixOverride, 
            Link[] links, Unit units) {
        if (links == null) {
            this.links = Util.EMPTY_LINK_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }
        this.columnNamePrefixOverride = columnNamePrefixOverride;
        this.units = units;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource) 
            throws KnowledgeSourceReadException {
        String headerString = this.columnNamePrefixOverride != null 
                ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links) + "_lengthBetween";
        return new String[] { headerString };
    }

    @Override
    public void columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource, Map<String, String> replace,
            char delimiter, Writer writer) throws KnowledgeSourceReadException, 
            IOException {
        Logger logger = Util.logger();
        List<Proposition> propositions = traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations, 
                references, knowledgeSource);
        Collections.sort(propositions, comp);
        int size = propositions.size();
        if (size > 2) {
            logger.log(Level.WARNING, 
                "There should be two or less propositions but there are {0}", 
                size);
        }
        if (size < 2) {
            StringUtil.escapeAndWriteDelimitedColumn(null, delimiter, replace, writer);
            return;
        }
        
        Iterator<Proposition> itr = propositions.iterator();
        TemporalProposition first = null;
        TemporalProposition second = null;
        
        Proposition tmpFirst = itr.next();
        if (tmpFirst instanceof TemporalProposition) {
            first = (TemporalProposition) tmpFirst;
        } else {
            logger.log(Level.WARNING, 
                    "The first proposition is not temporal: ", 
                    tmpFirst.getId());
        }
        Proposition tmpSecond = itr.next();
        if (tmpSecond instanceof TemporalProposition) {
            second = (TemporalProposition) tmpSecond;
        } else {
            logger.log(Level.WARNING, 
                    "The second proposition is not temporal: ", 
                    tmpSecond.getId());
        }
        
        String distance = PropositionUtil
                            .distanceBetweenFormattedShort(first, second, 
                                this.units);
        StringUtil.escapeAndWriteDelimitedColumn(distance, delimiter, replace, 
                writer);
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

	public Link[] getLinks() {
		return links;
	}

	public String getColumnNamePrefixOverride() {
		return columnNamePrefixOverride;
	}

	public Unit getUnits() {
		return units;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnNamePrefixOverride == null) ? 0 : columnNamePrefixOverride.hashCode());
		result = prime * result + Arrays.hashCode(links);
		result = prime * result + ((units == null) ? 0 : units.hashCode());
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
		DistanceBetweenColumnSpec other = (DistanceBetweenColumnSpec) obj;
		if (columnNamePrefixOverride == null) {
			if (other.columnNamePrefixOverride != null)
				return false;
		} else if (!columnNamePrefixOverride.equals(other.columnNamePrefixOverride))
			return false;
		if (!Arrays.equals(links, other.links))
			return false;
		if (units == null) {
			if (other.units != null)
				return false;
		} else if (!units.equals(other.units))
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
