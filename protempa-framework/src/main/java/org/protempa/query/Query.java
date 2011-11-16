package org.protempa.query;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
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
	private String[] keyIds;
	private Filter filters;
	private String[] propIds;
	private And<String>[] termIds;

	/**
	 * Creates new Query instance.
	 * 
	 * @param keyIds
	 *            An array of key IDs. If this is null then the query will
	 *            include all keyIDs.
	 * @param filters
	 *            A chain of filters. The first filter's getAnd method returns
	 *            the second filter in the chain or null.
	 * @param propIds
	 *            The proposition IDs that the query will try to derive.
	 * @param termIds
	 */
	@SuppressWarnings("unchecked")
	public Query(String[] keyIds, Filter filters, String[] propIds, And<String>[] termIds) {
		if (keyIds == null)
			keyIds = ArrayUtils.EMPTY_STRING_ARRAY;
		if (propIds == null)
			propIds = ArrayUtils.EMPTY_STRING_ARRAY;
		if (termIds == null) {
			// Type safety: The expression of type And[] needs unchecked
			// conversion to conform to And<String>[]
			termIds = new And[0];
		}
		ProtempaUtil.checkArrayForNullElement(keyIds, "keyIds");
		ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
		ProtempaUtil.checkArrayForNullElement(termIds, "termIds");
		this.keyIds = keyIds.clone();
		this.filters = filters;
		this.propIds = propIds.clone();
		ProtempaUtil.internAll(this.propIds);
		this.termIds = termIds.clone();
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
	 * @return a {@link String[]}. Never returns <code>null</code>.
	 */
	public final String[] getKeyIds() {
		return this.keyIds.clone();
	}

	/**
	 * Returns the proposition ids to be queried. An array of length 0 means
	 * that all proposition ids will be queried.
	 * 
	 * @param propIds
	 *            a {@link String[]}. Never returns <code>null</code>.
	 */
	public final String[] getPropIds() {
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
	 *         form.
	 */
	public final And<String>[] getTermIds() {
		return this.termIds.clone();
	}

	/**
	 * @return an array that references all of the filters in the chain of
	 *         filters.
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
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (!Arrays.equals(keyIds, other.keyIds))
			return false;
		if (!Arrays.equals(propIds, other.propIds))
			return false;
		if (!Arrays.equals(termIds, other.termIds))
			return false;
		return true;
	}

}

