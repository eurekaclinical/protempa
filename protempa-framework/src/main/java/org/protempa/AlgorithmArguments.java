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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * Stores arguments to be passed into an algorithm for a given low-level
 * abstraction value definition.
 * 
 * @author Andrew Post
 */
public final class AlgorithmArguments implements Serializable {

    private static final long serialVersionUID = 3993361731381010295L;

    private final Algorithm algorithm;
    private final Map<String, Value> parameterValues;
    private final Map<String, ValueComparator> parameterValueComps;

    AlgorithmArguments(Algorithm algorithm,
            LowLevelAbstractionValueDefinition def) {
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm cannot be null");
        }
        this.algorithm = algorithm;
        this.parameterValues = new HashMap<String, Value>();
        this.parameterValueComps = new HashMap<String, ValueComparator>();
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    

}
