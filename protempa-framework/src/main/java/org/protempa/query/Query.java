package org.protempa.query;

import org.protempa.dsb.filter.Filter;

/**
 * Main query interface.
 * 
 * @author Andrew Post
 */
public interface Query extends Cloneable {
    /**
     * Sets the key ids to be queried. An array of length 0 or 
     * <code>null</code> means that all key ids will be queried.
     *
     * @param keyIds a {@link String[]} of key ids. If <code>null</code>,
     * an empty {@link String[]} will be stored.
     */
    void setKeyIds(String[] keyIds);

    /**
     * Returns the key ids to be queried. An array of length 0 means that
     * all key ids will be queried.
     *
     * @return a {@link String[]}. Never returns <code>null</code>.
     */
    String[] getKeyIds();

    void setFilters(Filter filters);

    Filter getFilters();

    /**
     * Sets the proposition ids to be queried. An array of length 0 or
     * <code>null</code> means that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]} of proposition ids. If
     * <code>null</code>, an empty {@link String[]} will be stored.
     */
    void setPropIds(String[] propIds);

    /**
     * Returns the proposition ids to be queried. An array of length 0 means
     * that all proposition ids will be queried.
     *
     * @param propIds a {@link String[]}. Never returns <code>null</code>.
     */
    String[] getPropIds();

    public Query clone();
}
