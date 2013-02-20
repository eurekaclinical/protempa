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
package org.protempa.query.handler.table;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnNameOverride == null) ? 0 : columnNameOverride.hashCode());
        result = prime * result + ((falseOutput == null) ? 0 : falseOutput.hashCode());
        result = prime * result + Arrays.hashCode(links);
        result = prime * result + n;
        result = prime * result + ((trueOutput == null) ? 0 : trueOutput.hashCode());
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
        AtLeastNColumnSpec other = (AtLeastNColumnSpec) obj;
        if (columnNameOverride == null) {
            if (other.columnNameOverride != null) {
                return false;
            }
        } else if (!columnNameOverride.equals(other.columnNameOverride)) {
            return false;
        }
        if (falseOutput == null) {
            if (other.falseOutput != null) {
                return false;
            }
        } else if (!falseOutput.equals(other.falseOutput)) {
            return false;
        }
        if (!Arrays.equals(links, other.links)) {
            return false;
        }
        if (n != other.n) {
            return false;
        }
        if (trueOutput == null) {
            if (other.trueOutput != null) {
                return false;
            }
        } else if (!trueOutput.equals(other.trueOutput)) {
            return false;
        }
        return true;
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource,
            String[] inPropIds) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<String>();
        for (Link link : this.links) {
            inPropIds = link.getInferredPropositionIds(knowledgeSource, 
                    inPropIds);
            org.arp.javautil.arrays.Arrays.addAll(result, inPropIds);
        }
        return result.toArray(new String[result.size()]);
    }
}
