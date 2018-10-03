package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.util.HashMap;
import java.util.Map;

/**
 * Checks whether an algorithm specified in a low level abstraction definition
 * exists in the given algorithm source.
 * 
 * @author Andrew Post
 */
class ValidateAlgorithmCheckedVisitor extends AbstractPropositionDefinitionCheckedVisitor {
    
    private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
    private final AlgorithmSource algorithmSource;

    /**
     * Instantiates the class with the algorithm source to check.
     * 
     * @param algorithmSource the algorithm source. Cannot be <code>null</code>.
     */
    ValidateAlgorithmCheckedVisitor(AlgorithmSource algorithmSource) {
        assert algorithmSource != null : "algorithmSource cannot be null";
        this.algorithms = new HashMap<>();
        this.algorithmSource = algorithmSource;
    }

    /**
     * For every low level abstraction definition that has been checked, 
     * returns the corresponding algorithm from the algorithm source.
     * 
     * @return a map from low level abstraction definition to algorithm.
     * Guaranteed not <code>null</code>.
     */
    Map<LowLevelAbstractionDefinition, Algorithm> getAlgorithms() {
        return this.algorithms;
    }

    /**
     * Throws an exception if no algorithm was found for the given low level
     * abstraction definition.
     * 
     * @param lowLevelAbstractionDefinition the low level abstraction definition
     * to check.
     * @throws NoSuchAlgorithmException if no algorithm was found in the 
     * algorithm source.
     * @throws AlgorithmSourceReadException if an error occurred while reading
     * from the algorithm source.
     */
    @Override
    public void visit(LowLevelAbstractionDefinition lowLevelAbstractionDefinition) throws NoSuchAlgorithmException, AlgorithmSourceReadException {
        String algorithmId = lowLevelAbstractionDefinition.getAlgorithmId();
        Algorithm algorithm = this.algorithmSource.readAlgorithm(algorithmId);
        if (algorithm == null && algorithmId != null) {
            throw new NoSuchAlgorithmException("Low level abstraction definition " + lowLevelAbstractionDefinition.getId() + " wants the algorithm " + algorithmId + ", but no such algorithm is available.");
        }
        this.algorithms.put(lowLevelAbstractionDefinition, algorithm);
    }

    /**
     * Does nothing.
     * 
     * @param extendedLowLevelAbstractionDefinition a compound low level abstraction definition.
     */
    @Override
    public void visit(CompoundLowLevelAbstractionDefinition extendedLowLevelAbstractionDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param eventDefinition an event definition.
     */
    @Override
    public void visit(EventDefinition eventDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param highLevelAbstractionDefinition a high level abstraction definitions.
     */
    @Override
    public void visit(HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param primitiveParameterDefinition a primitive parameter definition.
     */
    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param sliceAbstractionDefinition a slice abstraction definition.
     */
    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param pairAbstractionDefinition a sequential temporal pattern definition.
     */
    @Override
    public void visit(SequentialTemporalPatternDefinition pairAbstractionDefinition) {
    }

    /**
     * Does nothing.
     * 
     * @param def a constant definition.
     */
    @Override
    public void visit(ConstantDefinition def) {
    }

    /**
     * Does nothing.
     * 
     * @param def a context definition.
     */
    @Override
    public void visit(ContextDefinition def) {
    }
    
}
