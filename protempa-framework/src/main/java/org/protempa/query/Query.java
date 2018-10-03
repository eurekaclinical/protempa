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
package org.protempa.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
    public static QueryMode DEFAULT_QUERY_MODE = QueryMode.REPLACE;
    
    private static final long serialVersionUID = -9007995369064299652L;
    private static final PropositionDefinition[] EMPTY_PROP_DEF_ARRAY =
            new PropositionDefinition[0];
    private final String[] keyIds;
    private final Filter filters;
    private final String[] propIds;
    private final PropositionDefinition[] propDefs;
    private String name;
    private String username;
    private QueryMode queryMode;
    private String databasePath;
    
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
            PropositionDefinition[] propDefs,
            QueryMode queryMode) {
        this(null, keyIds, filters, propIds, propDefs, queryMode);
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
            PropositionDefinition[] propDefs,
            QueryMode queryMode) {
        this(id, null, keyIds, filters, propIds, propDefs, queryMode);
    }
    
    public Query(String id, String username, String[] keyIds, Filter filters, String[] propIds,
            PropositionDefinition[] propDefs,
            QueryMode queryMode) {
        this(id, username, keyIds, filters, propIds, propDefs, queryMode, null);
    }
    
    public Query(String id, String username, String[] keyIds, Filter filters, String[] propIds,
            PropositionDefinition[] propDefs, QueryMode queryMode, String databasePath) {
        if (keyIds == null) {
            keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (propIds == null) {
            propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (propDefs == null) {
            propDefs = EMPTY_PROP_DEF_ARRAY;
        }
        ProtempaUtil.checkArrayForNullElement(keyIds, "keyIds");
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        ProtempaUtil.checkArrayForNullElement(propDefs, "propDefs");
        this.keyIds = keyIds.clone();
        this.filters = filters;
        this.propIds = propIds.clone();
        ProtempaUtil.internAll(this.propIds);
        this.propDefs = propDefs.clone();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        this.name = id;
        if (queryMode == null) {
            this.queryMode = DEFAULT_QUERY_MODE;
        } else {
            this.queryMode = queryMode;
        }
        this.username = username;
        this.databasePath = databasePath;
        if (this.databasePath == null && org.arp.javautil.arrays.Arrays.contains(QueryMode.reprocessModes(), this.queryMode)) {
            throw new IllegalArgumentException("Must specify a database path when in reprocess mode!");
        }
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
    public final String getName() {
        return name;
    }

    public String getUsername() {
        return username;
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

    public QueryMode getQueryMode() {
        return queryMode;
    }

    public String getDatabasePath() {
        return databasePath;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + (this.filters != null ? this.filters.hashCode() : 0);
        result = prime * result + Arrays.hashCode(this.keyIds);
        result = prime * result + Arrays.hashCode(this.propIds);
        result = prime * result + Arrays.hashCode(this.propDefs);
        result = prime * result + this.queryMode.hashCode();
        result = prime * result + (this.username != null ? this.username.hashCode() : 0);
        result = prime * result + (this.databasePath != null ? this.databasePath.hashCode() : 0);
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
        if (this.filters == null) {
            if (other.filters != null) {
                return false;
            }
        } else if (!this.filters.equals(other.filters)) {
            return false;
        }
        if (!Arrays.equals(this.keyIds, other.keyIds)) {
            return false;
        }
        if (!Arrays.equals(this.propIds, other.propIds)) {
            return false;
        }
        if (!Arrays.equals(this.propDefs, other.propDefs)) {
            return false;
        }
        if (!this.queryMode.equals(other.queryMode)) {
            return false;
        }
        if (this.username != null ? !this.username.equals(other.username) : other.username != null) {
            return false;
        }
        if (this.databasePath != null ? this.databasePath.equals(other.databasePath) : other.databasePath != null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    
}
