package org.protempa;

import org.protempa.dsb.filter.Filter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;


/**
 * Provides a skeletal implementation of the <code>Algorithm</code> interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAlgorithm implements Algorithm {

	private ValueFactory inValueType;

	private String id;

	private AlgorithmParameter[] parameters;

	private final Map<String, AlgorithmParameter> parametersMap;

	private int advanceRowSkipEnd;

	private int minNumVal;

	private int maxNumVal;

	public AbstractAlgorithm(Algorithms algorithms, String id) {
		this.id = computeId(algorithms, id, false);
		this.parametersMap = new HashMap<String, AlgorithmParameter>();
		this.advanceRowSkipEnd = -1;
		if (algorithms != null) {
			algorithms.addAlgorithm(this);
		}
		this.minNumVal = 1;
		this.maxNumVal = Integer.MAX_VALUE;
	}

	private static String computeId(Algorithms algorithms, String id,
			boolean fail) {
		if (id == null || id.length() == 0) {
			return algorithms.getNextAlgorithmObjectId();
		} else if (!algorithms.isUniqueAlgorithmObjectId(id)) {
			if (fail) {
				throw new IllegalArgumentException("id " + id
						+ " is not unique.");
			} else {
				return algorithms.getNextAlgorithmObjectId();
			}
		} else {
			return id;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getId()
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Sets the default minimum sliding window width. May be overridden by low
	 * level abstraction definitions. If negative, the algorithm will process an
	 * entire sequence at once by default up to
	 * <code>maximumNumberOfValues</code>.
	 * 
	 * @param val
	 */
	public final void setMinimumNumberOfValues(int val) {
		if (val == 0) {
			throw new IllegalArgumentException("val cannot be 0");
		}
		this.minNumVal = val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getMinimumNumberOfValues()
	 */
	public final int getMinimumNumberOfValues() {
		return this.minNumVal;
	}

	/**
	 * Sets the default maximum sliding window width. May be overridden by low
	 * level abstraction definitions.
	 * 
	 * @param val
	 *            an <code>int</code> > 0.
	 * @throws IllegalArgumentException
	 *             if val is < 1.
	 */
	public final void setMaximumNumberOfValues(int val) {
		if (val < 1) {
			throw new IllegalArgumentException("val cannot be < 1");
		}
		this.maxNumVal = val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getMaximumNumberOfValues()
	 */
	public final int getMaximumNumberOfValues() {
		return this.maxNumVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#close()
	 */
	public void close() {

	}

	@Override
	protected void finalize() {
		try {
			close();
		} catch (Exception e) {
			ProtempaUtil.logger().log(Level.SEVERE,
					"Could not finalize " + toString(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getInValueType()
	 */
	public final ValueFactory getInValueType() {
		return inValueType;
	}

	public final void setInValueType(ValueFactory inValueType) {
		this.inValueType = inValueType;
	}

	public void setParameters(AlgorithmParameter[] parameters) {
		this.parameters = parameters;
		this.parametersMap.clear();
		if (parameters != null) {
			for (AlgorithmParameter p : parameters) {
				this.parametersMap.put(p.getName(), p);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getParameters()
	 */
	public AlgorithmParameter[] getParameters() {
		return this.parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#parameter(java.lang.String)
	 */
	public AlgorithmParameter parameter(String name) {
		return parametersMap.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#createDataSourceConstraint()
	 */
	public Filter createDataSourceConstraint() {
		return null;
	}

	protected void setAdvanceRowSkipEnd(int advanceRowSkipEnd) {
		this.advanceRowSkipEnd = advanceRowSkipEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.Algorithm#getAdvanceRowSkipEnd()
	 */
	public int getAdvanceRowSkipEnd() {
		return advanceRowSkipEnd;
	}

	@Override
	public String toString() {
		return "Algorithm " + getId();
	}

	public void initialize(AlgorithmArguments arguments)
			throws AlgorithmInitializationException {

	}

	public abstract Value compute(Segment<PrimitiveParameter> segment,
			AlgorithmArguments arguments) throws AlgorithmProcessingException;
}
