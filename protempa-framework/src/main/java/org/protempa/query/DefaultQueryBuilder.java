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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashSet;
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
    private static final PropositionDefinition[] EMPTY_PROP_DEF_ARRAY =
            new PropositionDefinition[0];
    private String[] keyIds;
    private Filter filters;
    private String[] propIds;
    @SuppressWarnings("unchecked")
    private And<String>[] termIds;
    private PropositionDefinition[] propDefs;
    private String id;
    private final PropertyChangeSupport changes;
    // Flag to control validation of proposition IDs. Should only be turned off
    // for testing.
    private static boolean validatePropositionIds = true;

    public DefaultQueryBuilder() {
        this.changes = new PropertyChangeSupport(this);
        this.propDefs = EMPTY_PROP_DEF_ARRAY;
        this.keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        this.propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        this.termIds = new And[0];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
     * Sets the filters to apply to this query.
     *
     * Note that in order to serialize this query, the supplied filters must
     * also implement {@link Serializable}.
     *
     * @param filters a {@link Filter}.
     */
    public final void setFilters(Filter filters) {
        Filter old = this.filters;
        this.filters = filters;
        this.changes.firePropertyChange("filters", old, this.filters);
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
     * Sets the key ids to be queried. An array of length 0 or
     * <code>null</code> means that all key ids will be queried.
     *
     * @param keyIds a {@link String[]} of key ids. If <code>null</code>, an
     * empty {@link String[]} will be stored.
     */
    public final void setKeyIds(String[] keyIds) {
        if (keyIds == null) {
            keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        String[] old = this.keyIds;
        this.keyIds = keyIds.clone();
        this.changes.firePropertyChange("keyIds", old, this.keyIds);
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
        String[] old = this.propIds;
        if (propIds == null) {
            this.propIds = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.propIds = propIds.clone();
            ProtempaUtil.internAll(this.propIds);
        }
        this.changes.firePropertyChange("propIds", old, this.propIds);
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
     * Sets the term ids to be queried in disjunctive normal form. If any terms
     * are specified, PROTEMPA will navigate the term's subsumption hierarchy,
     * find proposition definitions that have been annotated with each term, and
     * add those to the query. If
     * <code>and</code>'d term ids are specified, proposition definitions will
     * only match if they are annotated with all of the specified term ids (or
     * terms in their subsumption hierarchies).
     *
     * @param termIds a {@link And[]} term ids representing disjunctive normal
     * form.
     */
    @SuppressWarnings("unchecked")
    public final void setTermIds(And<String>[] termIds) {
        if (termIds == null) {
            termIds = new And[0];
        }
        And<String>[] old = this.termIds;
        this.termIds = (And<String>[]) termIds.clone();
        this.changes.firePropertyChange("termIds", old, this.termIds);
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
        PropositionDefinition[] old = this.propDefs;
        this.propDefs = propDefs.clone();
        this.changes.firePropertyChange("propositionDefinitions", old,
                this.propDefs);
    }

    /**
     * Adds listeners for changes to this Query's properties.
     *
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes listeners for changes to this Query's properties.
     *
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds listeners for changes to the specified property.
     *
     * @param propertyName the name {@link String} of the property of interest.
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes listeners for changes to the specified property.
     *
     * @param propertyName the name {@link String} of the property that is no
     * longer of interest.
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public Query build(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws QueryBuildException {
        if (validatePropositionIds) {
            Set<String> userSpecifiedPropIds = new HashSet<String>();
            for (PropositionDefinition propDef : this.propDefs) {
                userSpecifiedPropIds.add(propDef.getId());
            }
            for (PropositionDefinition propDef : this.propDefs) {
                for (String propId : propDef.getChildren()) {
                    try {
                        if (!userSpecifiedPropIds.contains(propId)
                                && !knowledgeSource.hasPropositionDefinition(propId)) {
                            throw new QueryBuildException("Invalid proposition id: " + propId);
                        }
                    } catch (KnowledgeSourceReadException ex) {
                        throw new QueryBuildException("Could not build query", ex);
                    }
                }
            }
            for (String propId : propIds) {
                try {
                    boolean isUserSpecified = false;
                    if (userSpecifiedPropIds.contains(propId)) {
                        isUserSpecified = true;
                    }
                    if (!isUserSpecified
                            && !knowledgeSource.hasPropositionDefinition(propId)) {
                        throw new QueryBuildException(
                                "Invalid proposition id: " + propId);
                    }
                } catch (KnowledgeSourceReadException ex) {
                    throw new QueryBuildException("Could not build query", ex);
                }
            }
        }
        return new Query(this.id, this.keyIds, this.filters, this.propIds, 
                this.termIds, this.propDefs);
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
