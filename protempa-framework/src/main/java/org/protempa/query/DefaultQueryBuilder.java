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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;
import org.protempa.*;
import org.protempa.backend.dsb.filter.Filter;

/**
 *
 * @author Andrew Post
 */
public class DefaultQueryBuilder implements QueryBuilder, Serializable {

    private static final long serialVersionUID = -3920993703423486485L;
    private static final PropositionDefinition[] EMPTY_PROP_DEF_ARRAY
            = new PropositionDefinition[0];
    // Flag to control validation of proposition IDs. Should only be turned off
    // for testing.
    private static boolean validatePropositionIds = true;

    private String[] keyIds;
    private Filter filters;
    private String[] propIds;
    private PropositionDefinition[] propDefs;
    private String name;
    private String username;
    private QueryMode queryMode;
    private String databasePath;

    public DefaultQueryBuilder() {
        this.propDefs = EMPTY_PROP_DEF_ARRAY;
        this.keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        this.propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        this.queryMode = Query.DEFAULT_QUERY_MODE;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public QueryMode getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(QueryMode queryMode) {
        if (queryMode == null) {
            this.queryMode = Query.DEFAULT_QUERY_MODE;
        } else {
            this.queryMode = queryMode;
        }
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
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
     * Sets the filters to apply to this query.
     *
     * Note that in order to serialize this query, the supplied filters must
     * also implement {@link Serializable}.
     *
     * @param filters a {@link Filter}.
     */
    public final void setFilters(Filter filters) {
        this.filters = filters;
    }

    /**
     * Returns the key ids to be queried. An array of length 0 means that all
     * key ids will be queried.
     *
     * @return a {@link String[]}. Never returns <code>null</code>.
     */
    public final String[] getKeyIds() {
        return this.keyIds.clone();
    }

    /**
     * Sets the key ids to be queried. An array of length 0 or <code>null</code>
     * means that all key ids will be queried.
     *
     * @param keyIds a {@link String[]} of key ids. If <code>null</code>, an
     * empty {@link String[]} will be stored.
     */
    public final void setKeyIds(String[] keyIds) {
        if (keyIds == null) {
            keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        this.keyIds = keyIds.clone();
    }

    /**
     * Returns the proposition ids to be queried. An array of length 0 means
     * that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]}. Never returns <code>null</code>.
     */
    public final String[] getPropositionIds() {
        return this.propIds.clone();
    }

    /**
     * Sets the proposition ids to be queried. An array of length 0 or
     * <code>null</code> means that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]} of proposition ids. If
     * <code>null</code>, an empty {@link String[]} will be stored.
     */
    public final void setPropositionIds(String[] propIds) {
        if (propIds == null) {
            this.propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.propIds = propIds.clone();
            ProtempaUtil.internAll(this.propIds);
        }
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
     * Returns an optional set of user-specified proposition definitions.
     *
     * @param propDefs an array of {@link PropositionDefinition}s.
     */
    public final void setPropositionDefinitions(PropositionDefinition[] propDefs) {
        if (propDefs == null) {
            propDefs = EMPTY_PROP_DEF_ARRAY;
        }
        this.propDefs = propDefs.clone();
    }

    @Override
    public Query build(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws QueryBuildException {
        if (validatePropositionIds) {
            Set<String> userSpecifiedPropIds = new HashSet<>();
            for (PropositionDefinition propDef : this.propDefs) {
                userSpecifiedPropIds.add(propDef.getId());
            }
            List<String> propIdsToTest = new ArrayList<>(propIds.length);
            for (PropositionDefinition propDef : this.propDefs) {
                for (String propId : propDef.getChildren()) {
                    if (!userSpecifiedPropIds.contains(propId)) {
                        propIdsToTest.add(propId);
                    }
                }
            }
            for (String propId : propIds) {
                if (!userSpecifiedPropIds.contains(propId)) {
                    propIdsToTest.add(propId);
                }
            }
            try {
                List<PropositionDefinition> pds = knowledgeSource.readPropositionDefinitions(propIdsToTest.toArray(new String[propIdsToTest.size()]));
                List<String> actualPropIds = new ArrayList<>(pds.size());
                for (PropositionDefinition propDef : pds) {
                    actualPropIds.add(propDef.getId());
                }
                propIdsToTest.removeAll(actualPropIds);
                if (!propIdsToTest.isEmpty()) {
                    throw new QueryValidationException("Invalid proposition ids: " + propIdsToTest);
                }
            } catch (QueryValidationException | KnowledgeSourceReadException ex) {
                throw new QueryBuildException("Could not build query", ex);
            }
            if (Arrays.contains(QueryMode.reprocessModes(), this.queryMode) && this.databasePath == null) {
                throw new QueryBuildException("Could not build query", 
                        new QueryValidationException("Database path must be specified in reprocess mode"));
            }
        }
        return new Query(this.name, this.username, this.keyIds, this.filters,
                this.propIds, this.propDefs, this.queryMode, this.databasePath);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Set the value of the flag that determined whether the build method
     * validates proposition IDs.
     *
     * @param validatePropositionIds Proposition IDs are validated if, and only
     * if, this is true. The default value for this is true.
     */
    public static void setValidatePropositionIds(boolean validatePropositionIdsFlag) {
        validatePropositionIds = validatePropositionIdsFlag;
    }

    public static boolean isValidatePropositionIds() {
        return validatePropositionIds;
    }
}
