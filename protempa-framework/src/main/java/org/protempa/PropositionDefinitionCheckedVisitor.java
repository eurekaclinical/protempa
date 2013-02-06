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
 * Interface for classes that do processing on abstraction definitions and may
 * throw an exception.
 * 
 * @author Andrew Post
 * 
 * @see PropositionDefinitionVisitor
 * 
 */
public interface PropositionDefinitionCheckedVisitor {

    /**
     * Process a collection of proposition definitions.
     * 
     * @param propositionDefinitions
     *            a {@link Collection<PropositionDefinition>}. Cannot be
     *            <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(Collection<PropositionDefinition> propositionDefinitions)
            throws ProtempaException;

    /**
     * Process a low-level abstraction definition
     * 
     * @param def
     *            a {@link LowLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(LowLevelAbstractionDefinition def) throws ProtempaException;

    /**
     * Process a compound low-level abstraction definition
     * 
     * @param def
     *            a {@link CompoundLowLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     * 
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(CompoundLowLevelAbstractionDefinition def)
            throws ProtempaException;

    /**
     * Process a high-level abstraction definition
     * 
     * @param def
     *            a {@link HighLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(HighLevelAbstractionDefinition def) throws ProtempaException;

    /**
     * Process a slice definition.
     * 
     * @param def
     *            a {@link SliceDefinition}. Cannot be <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(SliceDefinition def) throws ProtempaException;

    /**
     * Process an event definition.
     * 
     * @param def
     *            an {@link EventDefinition}. Cannot be <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(EventDefinition def) throws ProtempaException;

    /**
     * Process a primitive parameter definition.
     * 
     * @param def
     *            a {@link PrimitiveParameterDefinition}. Cannot be
     *            <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(PrimitiveParameterDefinition def) throws ProtempaException;

    /**
     * Process a constant definition.
     * 
     * @param def
     *            a {@link ConstantDefinition}. Cannot be <code>null</code>.
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(ConstantDefinition def) throws ProtempaException;

    /**
     * Process a pair abstraction definition.
     * 
     * @param def
     *            a {@link PairDefinition}. Cannot be <code>null</code>.
     * 
     * @throws ProtempaException
     *             if an error occurs.
     */
    void visit(SequentialTemporalPatternDefinition def) throws ProtempaException;
    
    /**
     * Process a context definition.
     * 
     * @param def a {@link ContextDefinition}. Cannot be <code>null</code>.
     * @throws ProtempaException if an error occurs.
     */
    void visit(ContextDefinition def) throws ProtempaException;
}
