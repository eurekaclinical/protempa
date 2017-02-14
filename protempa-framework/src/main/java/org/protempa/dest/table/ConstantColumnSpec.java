package org.protempa.dest.table;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2017 Emory University
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

import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class ConstantColumnSpec extends AbstractTableColumnSpec {

    private final String value;
    private final String heading;
    
    public ConstantColumnSpec(String heading, String value) {
        if (heading == null) {
            throw new IllegalArgumentException("heading cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        
        this.heading = heading;
        this.value = value;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        return new String[]{this.heading};
    }

    @Override
    public void columnValues(String key, Proposition proposition, Map<Proposition, List<Proposition>> forwardDerivations, Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references, KnowledgeSourceCache knowledgeSourceCache, TabularWriter writer) throws TabularWriterException {
        writer.writeString(value);
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource) throws TableColumnSpecValidationFailedException, KnowledgeSourceReadException {
        
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource, String[] inPropIds) throws KnowledgeSourceReadException {
        return inPropIds;
    }
    
}
