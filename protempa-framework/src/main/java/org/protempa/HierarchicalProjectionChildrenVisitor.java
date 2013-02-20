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

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates the children of proposition definitions.
 *
 * @author Andrew Post
 */
public final class HierarchicalProjectionChildrenVisitor extends AbstractPropositionDefinitionCheckedVisitor {

    private final List<PropositionDefinition> propDefs;
    private final KnowledgeSource knowledgeSource;

    public HierarchicalProjectionChildrenVisitor(KnowledgeSource knowledgeSource) {
        this.knowledgeSource = knowledgeSource;
        this.propDefs = new ArrayList<PropositionDefinition>();
    }

    @Override
    public void visit(EventDefinition eventDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(eventDefinition));
    }

    @Override
    public void visit(HighLevelAbstractionDefinition highLevelAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(highLevelAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(highLevelAbstractionDefinition));
    }

    @Override
    public void visit(LowLevelAbstractionDefinition lowLevelAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(lowLevelAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(lowLevelAbstractionDefinition));
    }
    
    @Override
    public void visit(CompoundLowLevelAbstractionDefinition compoundLowLevelAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(compoundLowLevelAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(compoundLowLevelAbstractionDefinition));
    }

    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(primitiveParameterDefinition));
    }

    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(sliceAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(sliceAbstractionDefinition));
    }

    @Override
    public void visit(ConstantDefinition constantDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(constantDefinition));
    }

    @Override
    public void visit(SequentialTemporalPatternDefinition pairDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(pairDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(pairDefinition));
    }
    
    @Override
    public void visit(ContextDefinition def) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(def));
        this.propDefs.addAll(knowledgeSource.readSubContexts(def));
        this.propDefs.addAll(knowledgeSource.readInducedBy(def));
    }

    /**
     * Gets the children.
     *
     * @return a {@link List<PropositionDefinition>} of the direct children.
     */
    public List<PropositionDefinition> getChildren() {
        return this.propDefs;
    }

    public void clear() {
        this.propDefs.clear();
    }

    

    
}
