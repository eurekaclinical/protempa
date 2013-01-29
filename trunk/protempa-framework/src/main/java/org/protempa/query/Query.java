/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.backend.dsb.filter.Filter;

/**
 * Base query implementation.
 *
 * Queries are serializable provided that any filters that are set are also
 * serializable (see {@link #setFilters(org.protempa.dsb.filter.Filter)}).
 *
 * @author Andrew Post
 */
public class Query implements Serializable {

    private static final long serialVersionUID = -9007995369064299652L;
    private static final PropositionDefinition[] EMPTY_PROP_DEF_ARRAY =
            new PropositionDefinition[0];
    private final String[] keyIds;
    private final Filter filters;
    private final String[] propIds;
    private final And<String>[] termIds;
    private final PropositionDefinition[] propDefs;
    private String id;
    
    /**
     * Creates new Query instance with a default identifier.
     *
     * @param keyIds An array of key IDs. If this is null then the query will
     * include all keyIDs.
     * @param filters A chain of filters. The first filter's getAnd method
     * returns the second filter in the chain or null.
     * @param propIds The proposition IDs that the query will try to derive.
     * @param termIds
     */
    public Query(String[] keyIds, Filter filters, String[] propIds,
            And<String>[] termIds, PropositionDefinition[] propDefs) {
        this(null, keyIds, filters, propIds, termIds, propDefs);
    }

    /**
     * Creates new Query instance.
     *
     * @param id An identifier for this query. If <code>null</code>, a default 
     * identifier is assigned.
     * @param keyIds An array of key IDs. If this is null then the query will
     * include all keyIDs.
     * @param filters A chain of filters. The first filter's getAnd method
     * returns the second filter in the chain or null.
     * @param propIds The proposition IDs that the query will try to derive.
     * @param termIds
     */
    public Query(String id, String[] keyIds, Filter filters, String[] propIds,
            And<String>[] termIds, PropositionDefinition[] propDefs) {
        if (keyIds == null) {
            keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (propIds == null) {
            propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (termIds == null) {
            // Type safety: The expression of type And[] needs unchecked
            // conversion to conform to And<String>[]
            termIds = new And[0];
        }
        if (propDefs == null) {
            propDefs = EMPTY_PROP_DEF_ARRAY;
        }
        ProtempaUtil.checkArrayForNullElement(keyIds, "keyIds");
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        ProtempaUtil.checkArrayForNullElement(termIds, "termIds");
        ProtempaUtil.checkArrayForNullElement(propDefs, "propDefs");
        this.keyIds = keyIds.clone();
        this.filters = filters;
        this.propIds = propIds.clone();
        ProtempaUtil.internAll(this.propIds);
        this.termIds = termIds.clone();
        this.propDefs = propDefs.clone();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        this.id = id;
    }

    /**
     * Gets the filters to be applied to this query.
     *
     * @return a {@link Filter}.
     */
    public final Filter getFilters() {
        return this.filters;
    }

    /**
     * Returns the key ids to be queried. An array of length 0 means that all
     * key ids will be queried.
     *
     * @return a {@link String[]}. Never returns
     * <code>null</code>.
     */
    public final String[] getKeyIds() {
        return this.keyIds.clone();
    }

    /**
     * Returns the proposition ids to be queried. An array of length 0 means
     * that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]}. Never returns
     * <code>null</code>.
     */
    public final String[] getPropositionIds() {
        return this.propIds.clone();
    }

    /**
     * Gets the term ids to be queried in disjunctive normal form. PROTEMPA will
     * navigate these terms' subsumption hierarchy, find proposition definitions
     * that have been annotated with each term, and add those to the query.
     * <code>And</code>'d term ids will only match a proposition definition if
     * it is annotated with all of the specified term ids (or terms in their
     * subsumption hierarchies).
     *
     * @return a {@link String[]} of term ids representing disjunctive normal
     * form.
     */
    public final And<String>[] getTermIds() {
        return this.termIds.clone();
    }
    
    /**
     * Returns an optional set of user-specified proposition definitions.
     * 
     * @return an array of {@link PropositionDefinition}s.
     */
    public final PropositionDefinition[] getPropositionDefinitions() {
        return this.propDefs.clone();
    }
    
    /**
     * Returns this query's identifier. Guaranteed not <code>null</code>.
     * 
     * @return an identifier {@link String}. 
     */
    public final String getId() {
        return id;
    }

    /**
     * @return an array that references all of the filters in the chain of
     * filters.
     */
    Filter[] getFiltersArray() {
        if (filters == null) {
            return new Filter[0];
        }
        return filters.filterChainToArray();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((filters == null) ? 0 : filters.hashCode());
        result = prime * result + Arrays.hashCode(keyIds);
        result = prime * result + Arrays.hashCode(propIds);
        result = prime * result + Arrays.hashCode(termIds);
        result = prime * result + Arrays.hashCode(propDefs);
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
        Query other = (Query) obj;
        if (filters == null) {
            if (other.filters != null) {
                return false;
            }
        } else if (!filters.equals(other.filters)) {
            return false;
        }
        if (!Arrays.equals(keyIds, other.keyIds)) {
            return false;
        }
        if (!Arrays.equals(propIds, other.propIds)) {
            return false;
        }
        if (!Arrays.equals(termIds, other.termIds)) {
            return false;
        }
        if (!Arrays.equals(propDefs, other.propDefs)) {
            return false;
        }
        return true;
    }
}
