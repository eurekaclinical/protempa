package org.protempa.query;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.protempa.AlgorithmSource;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.backend.dsb.filter.Filter;

/**
 *
 * @author Andrew Post
 */
public class DefaultQueryBuilder implements QueryBuilder, Serializable {

    private static final long serialVersionUID = -3920993703423486485L;
    private String[] keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
    private Filter filters;
    private String[] propIds = ArrayUtils.EMPTY_STRING_ARRAY;
    private And[] termIds = new And[0];
    private final PropertyChangeSupport changes;

    public DefaultQueryBuilder() {
        this.changes = new PropertyChangeSupport(this);
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
     * Returns the key ids to be queried. An array of length 0 means that
     * all key ids will be queried.
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
     * @param keyIds a {@link String[]} of key ids. If <code>null</code>,
     * an empty {@link String[]} will be stored.
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
    public final String[] getPropIds() {
        return this.propIds.clone();
    }

    /**
     * Sets the proposition ids to be queried. An array of length 0 or
     * <code>null</code> means that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]} of proposition ids. If
     * <code>null</code>, an empty {@link String[]} will be stored.
     */
    public final void setPropIds(String[] propIds) {
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
     * Gets the term ids to be queried in disjunctive normal form. PROTEMPA
     * will navigate these terms' subsumption hierarchy, find proposition
     * definitions that have been annotated with each term, and add those
     * to the query. <code>And</code>'d term ids will only match a proposition
     * definition if it is annotated with all of the specified term ids (or
     * terms in their subsumption hierarchies).
     *
     * @return a {@link String[]} of term ids representing disjunctive
     * normal form.
     */
    public final And[] getTermIds() {
        return this.termIds.clone();
    }

    /**
     * Sets the term ids to be queried in disjunctive normal form. If any terms
     * are specified, PROTEMPA
     * will navigate the term's subsumption hierarchy, find proposition
     * definitions that have been annotated with each term, and add those
     * to the query. If <code>and</code>'d term ids are specified,
     * proposition definitions will only match if they are annotated with all
     * of the specified term ids (or terms in their subsumption hierarchies).
     *
     * @param termIds a {@link And[]} term ids representing disjunctive
     * normal form.
     */
    public final void setTermIds(And[] termIds) {
        if (termIds == null) {
            termIds = new And[0];
        }
        And[] old = this.termIds;
        this.termIds = termIds.clone();
        this.changes.firePropertyChange("termIds", old, this.termIds);
    }

    /**
     * Adds listeners for changes to this Query's properties.
     *
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void addPropertyChangeListener(
            PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes listeners for changes to this Query's properties.
     *
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void removePropertyChangeListener(
            PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds listeners for changes to the specified property.
     *
     * @param propertyName the name {@link String} of the property of interest.
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes listeners for changes to the specified property.
     *
     * @param propertyName the name {@link String} of the property that is no
     * longer of interest.
     * @param listener a {@link PropertyChangeListener}.
     */
    public final void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public Query build(KnowledgeSource knowledgeSource, 
                AlgorithmSource algorithmSource)
            throws QueryBuildException {
        for (String propId : propIds) {
            try {
                if (!knowledgeSource.hasPropositionDefinition(propId)) {
                    throw new QueryBuildException(
                            "Invalid proposition id: " + propId);

                }
            } catch (KnowledgeSourceReadException ex) {
                throw new QueryBuildException("Could not build query", ex);
            }
        }
        return new Query(this.keyIds, this.filters, this.propIds,
                this.termIds);
    }
}
