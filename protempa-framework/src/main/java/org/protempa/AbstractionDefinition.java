/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import java.util.Set;

/**
 * Interface to definitions of the constraints required to infer an abstract
 * parameter.
 * 
 * @author Andrew Post
 * 
 */
public interface AbstractionDefinition extends PropositionDefinition,
        TemporalPropositionDefinition {

    public abstract String getDescription();

    /**
     * Returns all proposition ids from which this abstract parameter is
     * abstracted.
     *
     * @return an unmodifiable <code>Set</code> of proposition id
     *         <code>String</code>s. Guaranteed not null.
     */
    public abstract Set<String> getAbstractedFrom();

    public abstract GapFunction getGapFunction();
}