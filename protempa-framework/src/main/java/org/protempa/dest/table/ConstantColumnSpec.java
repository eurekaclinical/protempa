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

import java.util.Map;
import java.util.Set;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.pool.Pool;
import org.protempa.pool.PoolException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class ConstantColumnSpec extends AbstractTableColumnSpec {

    private final String value;
    private final String heading;
    private final Pool<String> pool;
    
    public ConstantColumnSpec(String heading, String value) {
        this(heading, value, null);
    }
    
    public ConstantColumnSpec(String heading, String value, Pool<String> pool) {
        if (heading == null) {
            throw new IllegalArgumentException("heading cannot be null");
        }
        
        this.heading = heading;
        this.pool = pool;
        this.value = value;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        return new String[]{this.heading};
    }

    @Override
    public void columnValues(String key, Proposition proposition, 
            Map<Proposition, Set<Proposition>> forwardDerivations, 
            Map<Proposition, Set<Proposition>> backwardDerivations, 
            Map<UniqueId, Proposition> references, 
            KnowledgeSourceCache knowledgeSourceCache, TabularWriter writer) throws TabularWriterException {
        try {
            writer.writeString(this.pool != null ? this.pool.valueFor(this.value) : this.value);
        } catch (PoolException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource) throws TableColumnSpecValidationFailedException, KnowledgeSourceReadException {
        
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource, String[] inPropIds) throws KnowledgeSourceReadException {
        return inPropIds;
    }
    
}
