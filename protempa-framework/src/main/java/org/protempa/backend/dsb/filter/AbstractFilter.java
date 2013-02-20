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
package org.protempa.backend.dsb.filter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.ProtempaUtil;

/**
 * An abstract class that makes implementing filters relatively easy.
 * 
 * @author Andrew Post.
 */
public abstract class AbstractFilter implements Filter {

	private String[] propositionIds;
	private Filter and;

	/**
	 * Instantiates the filter with the proposition ids that it is valid for.
	 * 
	 * @param propositionIds
	 *            a proposition id {@link String[]}. Cannot be <code>null</code>
	 *            , empty or contain <code>null</code> values.
	 */
	public AbstractFilter(String[] propositionIds) {
		ProtempaUtil.checkArray(propositionIds, "propositionIds");
		propositionIds = propositionIds.clone();
		ProtempaUtil.internAll(propositionIds);
		this.propositionIds = propositionIds;
	}

	@Override
	public String[] getPropositionIds() {
		return propositionIds.clone();
	}

	public void setAnd(Filter and) {
		this.and = and;
	}

	@Override
	public Filter getAnd() {
		return and;
	}

	private static class DataSourceConstraintAndIterator implements Iterator<Filter> {

		private Filter dataSourceConstraint;

		private DataSourceConstraintAndIterator(Filter dataSourceConstraint) {
			assert dataSourceConstraint != null : "dataSourceConstraint cannot be null";
			this.dataSourceConstraint = dataSourceConstraint;
		}

		@Override
		public boolean hasNext() {
			if (this.dataSourceConstraint != null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Filter next() {
			if (this.dataSourceConstraint != null) {
				Filter dsc = this.dataSourceConstraint;
				this.dataSourceConstraint = this.dataSourceConstraint.getAnd();
				return dsc;
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Iterator<Filter> andIterator() {
		return new DataSourceConstraintAndIterator(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * Convert an array of <code>AbstractFilter</code> objects to a chain of
	 * filters by calling the {@link #setAnd} the method of each filter object.
	 * 
	 * @param filterArray
	 *            An array of AbstractFilter objects having length greater than
	 *            zero.
	 * @return The first filter in the chain which will be == to filterArray[0]
	 */
	public static Filter filterArrayToChain(Filter[] filterArray) {
		for (int i = 0; i < (filterArray.length - 1); i++) {
			AbstractFilter thisFilter = (AbstractFilter) filterArray[i];
			thisFilter.setAnd(filterArray[i + 1]);
		}
		AbstractFilter lastFilter = (AbstractFilter) filterArray[filterArray.length - 1];
		lastFilter.setAnd(null);
		return filterArray[0];
	}

	/**
	 * @return the length of the filter chain that begins with this object.
	 */
	public int chainLength() {
		AbstractFilter thisFilter = (AbstractFilter) this.and;
		int length = 1;
		while (thisFilter.and != null) {
			length += 1;
			thisFilter = (AbstractFilter) thisFilter.and;
		}
		return length;
	}

	/**
	 * Return an array that contains all of the filters in the chain.
	 */
	@Override
	public Filter[] filterChainToArray() {
		int length = chainLength();
		Filter[] array = new Filter[length];
		Filter thisFilter = this;
		for (int i = 0; i < length; i++) {
			array[i] = thisFilter;
			thisFilter = thisFilter.getAnd();
		}
		return array;
	}
	
    
	abstract public int hashCode();
	
	abstract public boolean equals(Object obj);

}
