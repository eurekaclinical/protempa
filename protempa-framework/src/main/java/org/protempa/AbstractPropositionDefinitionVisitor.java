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

import java.util.Collection;

/**
 * An abstract base class for implementing proposition definition visitors.
 * Except for {@link #visit(Collection)}, the default implementations throw
 * {@link UnsupportedOperationException}. Override those methods to implement
 * your visitor's functionality.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractPropositionDefinitionVisitor implements
        PropositionDefinitionVisitor {

    /**
     * Processes a collection of proposition definitions.
     *
     * @param propositionDefinitions
     *            a {@link Collection<PropositionDefinition>}.
     */
    @Override
    public void visit(Collection<? extends PropositionDefinition> propositionDefinitions) {
        for (PropositionDefinition def : propositionDefinitions) {
            def.accept(this);
        }
    }

    /**
     * Processes event definitions. This default implementation throws an
     * {@link UnsupportedOperationException).
     *
     * @param eventDefinition
     *            an {@link EventDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(EventDefinition eventDefinition) {
        throw new UnsupportedOperationException(
                "Visiting EventDefinitions is unsupported");
    }

    /**
     * Processes high-level abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param highLevelAbstractionDefinition
     *            an {@link HighLevelAbstractionDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(
            HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting HighLevelAbstractionDefinitions is unsupported");

    }

    /**
     * Processes low-level abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param lowLevelAbstractionDefinition
     *            a {@link LowLevelAbstractionDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(
            LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting LowLevelAbstractionDefinitions is unsupported");

    }
    
    /**
     * Processes compound low-level abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param compoundLowLevelAbstractionDefinition
     *            a {@link CompoundLowLevelAbstractionDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(
            CompoundLowLevelAbstractionDefinition compoundLowLevelAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting CompoundLowLevelAbstractionDefinitions is unsupported");

    }

    /**
     * Processes primitive parameter definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param primitiveParameterDefinition
     *            a {@link PrimitiveParameterDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
        throw new UnsupportedOperationException(
                "Visiting PrimitiveParameterDefinitions is unsupported");

    }

    /**
     * Processes slice abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param sliceAbstractionDefinition
     *            a {@link SliceDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting SliceAbstractionDefinitions is unsupported");

    }

    /**
     * Processes constant definitions. This default implementation throws an
     * {@link UnsupportedOperationException).
     *
     * @param constantDefinition
     *            a {@link ConstantDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(ConstantDefinition constantDefinition) {
        throw new UnsupportedOperationException(
                "Visiting ConstantDefinitions is unsupported");
    }

    /**
     * Processes constant definitions. This default implementation throws an
     * {@link UnsupportedOperationException).
     *
     * @param constantDefinition
     *            a {@link ConstantDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(SequentialTemporalPatternDefinition pairDefinition) {
        throw new UnsupportedOperationException(
                "Visiting PairDefinitions is unsupported");
    }
}
