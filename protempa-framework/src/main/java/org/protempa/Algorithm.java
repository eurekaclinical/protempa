package org.protempa;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;

/**
 * Interface to algorithms for processing primitive parameters.
 * 
 * @author Andrew Post
 * 
 */
public interface Algorithm {

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

	ValueFactory getInValueType();

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

	DataSourceConstraint createDataSourceConstraint();

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