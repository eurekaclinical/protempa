package org.protempa.dsb.filter;

import java.util.Iterator;

/**
 * Specifies a filter on what propositions to return from a data source.
 * Filters may be chained together with boolean 'and'. Each filter type must be
 * matched in data source backends by corresponding code to translate them
 * into data source query code.
 *
 * @author Andrew Post
 */
public interface Filter {

    /**
     * Returns the proposition ids for which this filter is valid. Implementors
     * of this class are not required to validate supplied proposition ids
     * to ensure that they are all supplied by a data source directly (i.e.,
     * not derived), thus it could potentially return the ids of derived
     * propositions.
     *
     * @return a proposition id {@link String[]}. Is guaranteed not to return
     * <code>null</code>, an empty array or an array containing
     * <code>null</code> values.
     */
    String[] getPropositionIds();

    /**
     * Returns the next filter in the chain, if specified.
     *
     * @return a {@link Filter}, or <code>null</code> if there are no more
     * filters in the chain.
     */
    Filter getAnd();

    /**
     * Performs some processing on this filter.
     * 
     * @param visitor a {@link FilterVisitor}. Cannot be <code>null</code>.
     */
    void accept(FilterVisitor visitor);

    /**
     * Returns an iterator over this filter and all subsequent filters in
     * the chain.
     *
     * @return an {@link Iterator<Filter>}.
     */
    Iterator<Filter> andIterator();
}
