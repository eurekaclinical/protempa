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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id.intern();
        this.lowLevelAbstractionDefinition.addValueDefinition(this);
        this.parameterValues = new HashMap<>();
        this.parameterValueComps = new HashMap<>();
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
     * @param segment a time series <code>Segment</code>, cannot be
     * <code>null</code>.
     * @param algorithm an <code>Algorithm</code>, or <code>null</code> to
     * specify no algorithm.
     * @return <code>true</code> if the time series segment satisfies the
     * constraints of this detector, <code>false</code> otherwise
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

    public final void setValue(Value value) {
        this.value = value;
    }

    public final Value getValue() {
        return value;
    }

    public final String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    
}
