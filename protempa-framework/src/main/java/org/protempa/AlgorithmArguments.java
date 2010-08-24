package org.protempa;

import java.util.HashMap;
import java.util.Map;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * Stores arguments to be passed into an algorithm for a given low-level
 * abstraction value definition.
 * 
 * @author Andrew Post
 */
public final class AlgorithmArguments {

    private final Algorithm algorithm;
    private final Map<String, Value> parameterValues;
    private final Map<String, ValueComparator> parameterValueComps;
    private final Map<String, Object> attributes;

    AlgorithmArguments(Algorithm algorithm,
            LowLevelAbstractionValueDefinition def) {
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm cannot be null");
        }
        this.algorithm = algorithm;
        this.parameterValues = new HashMap<String, Value>();
        this.parameterValueComps = new HashMap<String, ValueComparator>();
        this.attributes = new HashMap<String, Object>();
        for (AlgorithmParameter d : algorithm.getParameters()) {
            String name = d.getName();
            setArgument(name, def.getParameterComp(name), def.getParameterValue(name));
        }
    }

    private void setArgument(String name, ValueComparator comp, Value value) {
        if (name != null
                && value != null
                && this.algorithm.parameter(name) != null
                && (this.algorithm.parameter(name).getValueType()
                .isInstance(value))
                && (this.algorithm.parameter(name).hasComparator(comp))) {
            parameterValues.put(name, value);
            parameterValueComps.put(name, comp);
        }
    }

    public Value value(String name) {
        return this.parameterValues.get(name);
    }

    public ValueComparator valueComp(String name) {
        return this.parameterValueComps.get(name);
    }

    public Object putAttribute(String name, Object value) {
        return this.attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }
}
