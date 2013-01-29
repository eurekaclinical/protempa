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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa;

import java.util.List;
import java.util.Set;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.query.And;

/**
 *
 * @author Andrew Post
 */
public interface KnowledgeSource extends Source<KnowledgeSourceUpdatedEvent, KnowledgeSourceBackend, KnowledgeSourceBackendUpdatedEvent>{

    /**
     * Gets the mappings from term IDs to proposition IDs for each backend.
     *
     * @return a {@link Map} of {@link String}s to a {@link List} of
     * <code>String</code>s, with the keys being {@link Term} IDs and the values
     * being lists of {@link PropositionDefinition} IDs.
     */
    List<String> getPropositionDefinitionsByTerm(And<TermSubsumption> termSubsumptionClause) throws KnowledgeSourceReadException;

    boolean hasAbstractionDefinition(String id) throws KnowledgeSourceReadException;

    boolean hasPropositionDefinition(String id) throws KnowledgeSourceReadException;

    boolean hasValueSet(String id) throws KnowledgeSourceReadException;

    Set<PropositionDefinition> inDataSourcePropositionDefinitions(String... propIds) throws KnowledgeSourceReadException;

    Set<String> inDataSourcePropositionIds(String... propIds) throws KnowledgeSourceReadException;

    List<PropositionDefinition> readAbstractedFrom(AbstractionDefinition propDef) throws KnowledgeSourceReadException;

    List<PropositionDefinition> readAbstractedFrom(String id) throws KnowledgeSourceReadException;

    List<AbstractionDefinition> readAbstractedInto(PropositionDefinition propDef) throws KnowledgeSourceReadException;
    
    List<AbstractionDefinition> readAbstractedInto(String propId) throws KnowledgeSourceReadException;
    
    AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException;

    List<PropositionDefinition> readInverseIsA(PropositionDefinition propDef) throws KnowledgeSourceReadException;

    List<PropositionDefinition> readInverseIsA(String id) throws KnowledgeSourceReadException;
    
    List<PropositionDefinition> readIsA(PropositionDefinition propDef) throws KnowledgeSourceReadException;
    
    List<PropositionDefinition> readIsA(String id) throws KnowledgeSourceReadException;

    /**
     * Returns the specified proposition definition.
     *
     * @param id a proposition id {@link String}. Cannot be <code>null</code>.
     * @return a {@link PropositionDefinition}, or <code>null</code> if none was
     * found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
     */
    PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException;

    ValueSet readValueSet(String id) throws KnowledgeSourceReadException;
    
    List<PropositionDefinition> readParents(PropositionDefinition propDef)
            throws KnowledgeSourceReadException;
    
    List<PropositionDefinition> readParents(String propId)
            throws KnowledgeSourceReadException;
    
}
