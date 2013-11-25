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
package org.protempa.proposition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.value.Value;

/**
 * Represents an interval of multiple propositions and their values. The
 * propositions are expected to be {@link AbstractParameter}, specifically
 * low-level abstractions. Compound valued intervals are used by
 * {@link CompoundLowLevelAbstractionDefinition} to derive low-level
 * abstractions made up of multiple types of primitive parameters.
 */
public final class CompoundValuedInterval extends CompoundInterval<AbstractParameter> {
    private final Map<String, Value> values;

    /**
     * Constructor for this class. Initializes the object's fields with the
     * given parameters.
     * 
     * @param interval
     *            the {@link Interval} representing the time interval of this
     *            compound interval
     * @param parameters
     *            the {@link Proposition}s that belong to this interval. These
     *            will generally be {@link AbstractParameter}s
     */
    CompoundValuedInterval(Interval interval,
            Set<AbstractParameter> parameters) {
        super(interval, parameters);
        this.values = new HashMap<String, Value>();
        for (TemporalParameter p : parameters) {
            values.put(p.getId(), p.getValue());
        }
    }

    /**
     * Gets the proposition ID-value map for this interval
     * 
     * @return a {@link Map} linking proposition IDs to the values of those
     *         abstract parameters
     */
    public Map<String, Value> getValues() {
        return values;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
