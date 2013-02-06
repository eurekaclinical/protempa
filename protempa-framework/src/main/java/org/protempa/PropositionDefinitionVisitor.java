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
 * Interface for classes that do processing on abstraction definitions.
 * 
 * @author Andrew Post
 * 
 */
public interface PropositionDefinitionVisitor {

    /**
     * Process a collection of proposition definitions.
     * 
     * @param propositionDefinitions
     *            a {@link Collection<PropositionDefinition>}. Cannot be
     *            <code>null</code>.
     */
    void visit(
            Collection<? extends PropositionDefinition> propositionDefinitions);

    /**
     * Process a low-level abstraction definition
     * 
     * @param def
     *            a {@link LowLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(LowLevelAbstractionDefinition def);

    /**
     * Process a compound low-level abstraction definition
     * 
     * @param def
     *            an {@link CompoundLowLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(CompoundLowLevelAbstractionDefinition def);

    /**
     * Process a high-level abstraction definition
     * 
     * @param def
     *            a {@link HighLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(HighLevelAbstractionDefinition def);

    /**
     * Process a slice definition.
     * 
     * @param def
     *            a {@link SliceDefinition}. Cannot be <code>null</code>.
     */
    void visit(SliceDefinition def);

    /**
     * Process an event definition.
     * 
     * @param def
     *            an {@link EventDefinition}. Cannot be <code>null</code>.
     */
    void visit(EventDefinition def);

    /**
     * Process a primitive parameter definition.
     * 
     * @param def
     *            a {@link PrimitiveParameterDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(PrimitiveParameterDefinition def);

    /**
     * Process a constant definition.
     * 
     * @param def
     *            a {@link ConstantDefinition}. Cannot be <code>null</code>.
     */

    void visit(ConstantDefinition def);

    /**
     * Process a pair definition.
     * 
     * @param def
     *            a {@link PairDefinition}. Cannot be <code>null</code>.
     */
    void visit(SequentialTemporalPatternDefinition def);
    
    /**
     * Process a temporal context.
     * 
     * @param def a {@link ContextDefinition}. Cannot be <code>null</code>.
     */
    void visit(ContextDefinition def);
}
