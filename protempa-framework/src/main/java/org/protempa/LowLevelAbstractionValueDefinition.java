package org.protempa;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;


/**
 * @author Andrew Post
 */
public class LowLevelAbstractionValueDefinition implements Serializable {

	private static final long serialVersionUID = 667871001125802717L;

	private Value value = null;

	private final String id;

	private final LowLevelAbstractionDefinition lowLevelAbstractionDefinition;

	private final Map<String, Value> parameterValues;

	private final Map<String, ValueComparator> parameterValueComps;

	private AlgorithmArguments algorithmArguments;

	public LowLevelAbstractionValueDefinition(
			LowLevelAbstractionDefinition lowLevelAbstractionDefinition,
			String id) {
		if (lowLevelAbstractionDefinition == null) {
			throw new IllegalArgumentException(
					"A low level abstraction definition must be specified");
		}
		this.lowLevelAbstractionDefinition = lowLevelAbstractionDefinition;

		this.id = setId0(lowLevelAbstractionDefinition, id, false);
		this.lowLevelAbstractionDefinition.addValueDefinition(this);
		this.lowLevelAbstractionDefinition.addPropertyChangeListener(
				"algorithmId", new PropertyChangeListener() {

					@Override
                    public void propertyChange(PropertyChangeEvent evt) {
						LowLevelAbstractionValueDefinition.this.algorithmArguments = null;
						LowLevelAbstractionValueDefinition.this.parameterValueComps
								.clear();
						LowLevelAbstractionValueDefinition.this.parameterValues
								.clear();
					}
				});
		this.parameterValues = new HashMap<String, Value>();
		this.parameterValueComps = new HashMap<String, ValueComparator>();
	}

	public final void setParameterValue(String str, Value val) {
		this.parameterValues.put(str, val);
		this.algorithmArguments = null;
	}

	public final Value getParameterValue(String str) {
		return this.parameterValues.get(str);
	}

	public final Set<String> getParameters() {
		return this.parameterValues.keySet();
	}

	public final void setParameterComp(String str, ValueComparator comp) {
		this.parameterValueComps.put(str, comp);
		this.algorithmArguments = null;
	}

	public final Set<String> getParameterComps() {
		return this.parameterValueComps.keySet();
	}

	public final ValueComparator getParameterComp(String str) {
		return this.parameterValueComps.get(str);
	}

	public final LowLevelAbstractionDefinition getLowLevelAbstractionDefinition() {
		return lowLevelAbstractionDefinition;
	}

	/**
	 * Test whether or not the given time series satisfies the constraints of
	 * this detector and an optional algorithm. If no algorithm is specified,
	 * then this test just uses the detector's constraints.
	 * 
	 * @param segment
	 *            a time series <code>Segment</code>, cannot be
	 *            <code>null</code>.
	 * @param algorithm
	 *            an <code>Algorithm</code>, or <code>null</code> to
	 *            specify no algorithm.
	 * @return <code>true</code> if the time series segment satisfies the
	 *         constraints of this detector, <code>false</code> otherwise
	 * @throws AlgorithmInitializationException
	 * @throws AlgorithmProcessingException
	 */
	final boolean satisfiedBy(
			Segment<PrimitiveParameter> segment, Algorithm algorithm)
			throws AlgorithmInitializationException,
			AlgorithmProcessingException {
		Object result = null;
		if (algorithm != null) {
			if (this.algorithmArguments == null) {
				this.algorithmArguments = new AlgorithmArguments(algorithm,
						this);
				algorithm.initialize(this.algorithmArguments);
			}
			result = algorithm.compute(segment, this.algorithmArguments);
		} else {
			result = BooleanValue.TRUE;
		}
		return result != null;
	}

	private static String setId0(LowLevelAbstractionDefinition def, String id,
			boolean fail) {
		if (id == null || id.length() == 0) {
			return def.getNextLowLevelAbstractionValueDefinitionId();
		} else if (!def.isUniqueLowLevelAbstractionValueDefinitionId(id)) {
			if (fail) {
				throw new IllegalArgumentException("id " + id
						+ " is not unique in this knowledge base.");
			} else {
				return def.getNextLowLevelAbstractionValueDefinitionId();
			}
		} else {
			return id;
		}
	}

	public final void setValue(Value value) {
		this.value = value;
	}

	public final Value getValue() {
		return value;
	}

	public final String getId() {
		return id;
	}

	protected String debugMessage() {
		StringBuilder buffer = new StringBuilder(id + "-");
		buffer.append("value=" + value);
		return buffer.toString();
	}
}
