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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.dest.QueryResultsHandlerValidationFailedException;

/**
 * Specification of a column or sequence of columns in a delimited file
 * that represent a proposition or property.
 */
public interface TableColumnSpec {

    /**
     * Gets the names of the columns representing one instance of a
     * proposition or property. These columns may be repeated if the
     * specification results in matching more than one proposition or
     * property.
     *
     * @param knowledgeSource the active {@link KnowledgeSource}.
     * @return a {@link String[]} of column names.
     * @throws KnowledgeSourceReadException if an attempt at reading from
     * the knowledge source failed.
     */
    String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException;

    /**
     * Gets the values of the specified propositions or properties for
     * one row of data.
     *
     * @param key a key id {@link String}.
     * @param proposition a {@link List<Proposition>} for the specified
     * key with the specified proposition id.
     * @param knowledgeSource the active {@link KnowledgeSource}.
     * @return a {@link String[]} of column values.
     * @throws KnowledgeSourceReadException if an attempt at reading from
     * the knowledge source failed.
     */
    void columnValues(String key, Proposition proposition, 
            Map<Proposition, Set<Proposition>> forwardDerivations,
            Map<Proposition, Set<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSourceCache knowledgeSourceCache,
            TabularWriter writer)
            throws TabularWriterException;
    
    /**
     * Validates the fields of this column specification against the
     * knowledge source.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}. Guaranteed not
     * <code>null</code>.
     * 
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could
     * not be read.
     */
    void validate(KnowledgeSource knowledgeSource) throws
                TableColumnSpecValidationFailedException,
                KnowledgeSourceReadException;
    
    /**
     * Infers the ids of the propositions corresponding to the contents of this
     * column.
     * 
     * @return an array of proposition id {@link String}s. Guaranteed not
     * <code>null</code>.
     */
    String[] getInferredPropositionIds(KnowledgeSource knowledgeSource, 
            String[] inPropIds) throws KnowledgeSourceReadException;
}
