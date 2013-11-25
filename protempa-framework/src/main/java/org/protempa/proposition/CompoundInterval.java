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

import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.interval.Interval;

/**
 *
 * @author Andrew Post
 */
public class CompoundInterval<E extends TemporalProposition> {
    private final Interval interval;
    private final Set<E> parameters;

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
    CompoundInterval(Interval interval, Set<E> parameters) {
        this.interval = interval;
        this.parameters = parameters;

    }

    /**
     * Gets the interval for this compound interval.
     * 
     * @return an {@link Interval}
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Gets the abstract parameters that belong to this interval
     * 
     * @return the {@link Set} of {@link AbstractParameter}s
     */
    public Set<E> getTemporalPropositions() {
        return parameters;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
