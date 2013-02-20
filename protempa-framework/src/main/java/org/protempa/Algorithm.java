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
package org.protempa;

import java.io.Serializable;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

/**
 * Interface to algorithms for processing primitive parameters.
 * 
 * @author Andrew Post
 * 
 */
public interface Algorithm extends Serializable {

	/**
	 * The algorithm's unique id.
	 * 
	 * @return an id {@link String}.
	 */
	String getId();

	/**
	 * Gets the default minimum sliding window width. May be overridden by low
	 * level abstraction definitions. If negative, the algorithm will process an
	 * entire sequence at once by default up to the
	 * <code>maximumNumberOfValues</code>.
	 * 
	 * @return an <code>int</code> != 0.
	 * @see org.protempa.LowLevelAbstractionValueDefinition#getMaximumNumberOfValues()
	 */
	int getMinimumNumberOfValues();

	/**
	 * Gets the default maximum sliding window width. May be overridden by low
	 * level abstraction definitions.
	 * 
	 * @return an <code>int</code> > 0.
	 * @see org.protempa.LowLevelAbstractionValueDefinition#getMaximumNumberOfValues()
	 */
	int getMaximumNumberOfValues();

	void close();

	ValueType getInValueType();

	AlgorithmParameter[] getParameters();

	/**
	 * Returns the named parameter.
	 * 
	 * @param name
	 *            a {@link String}.
	 * @return an {@link AlgorithmParameter}, or <code>null</code> if the named
	 *         parameter does not exist.
	 */
	AlgorithmParameter parameter(String name);

	Filter createDataSourceConstraint();

	int getAdvanceRowSkipEnd();

	void initialize(AlgorithmArguments arguments)
			throws AlgorithmInitializationException;

	/**
	 * Computes whether or not a temporal abstraction exists in a segment of a
	 * time series.
	 * 
	 * @param segment
	 *            a <code>Segment</code> of <code>Parameter</code> objects.
	 * @return <code>null</code> if the pattern does not exist, or an
	 *         algorithm-specific <code>Value</code> if it does.
	 */
	Value compute(Segment<PrimitiveParameter> segment,
			AlgorithmArguments arguments) throws AlgorithmProcessingException;

}
