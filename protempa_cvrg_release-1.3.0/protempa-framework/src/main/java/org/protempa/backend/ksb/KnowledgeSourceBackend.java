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
package org.protempa.backend.ksb;

import java.util.List;

import org.protempa.AbstractionDefinition;
import org.protempa.backend.Backend;
import org.protempa.ConstantDefinition;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSource;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.TermSubsumption;
import org.protempa.ValueSet;
import org.protempa.query.And;

/**
 * Translates from an arbitrary knowledge base to a PROTEMPA knowledge base.
 * Users of <code>KnowledgeSourceBackend</code> implementations must first call
 * {@link #initialize(Properties)} with a set of configuration properties that
 * is specific to the <code>KnowledgeSourceBackend</code> implementation.
 * 
 * @author Andrew Post
 */
public interface KnowledgeSourceBackend extends
        Backend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource> {

    /**
     * Reads a primitive parameter definition into the given PROTEMPA knowledge
     * base. This will only get called if the proposition definition has
     * not already been loaded.
     * 
     * @param id
     *            a primitive parameter id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use. Guaranteed not
     *            <code>null</code>.
     * @return the {@link PrimitiveParameterDefinition}, or <code>null</code> if
     *         none with the given id was found.
     */
    PrimitiveParameterDefinition readPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads an abstraction definition into the given PROTEMPA knowledge base.
     * This will only get called if the proposition definition has not already
     * been loaded.
     * 
     * @param id
     *            an abstraction id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use. Guaranteed not
     *            <code>null</code>.
     * @return the {@link AbstractionDefinition}, or <code>null</code> if none
     *         with the given id was found.
     */
    AbstractionDefinition readAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads an event definition into the given PROTEMPA knowledge base. This
     * will only get called if the proposition definition has not already been
     * loaded.
     * 
     * @param id
     *            an event id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use. Guaranteed not
     *            <code>null</code>.
     * @return the {@link EventDefinition}, or <code>null</code> if none with
     *         the given id was found.
     */
    EventDefinition readEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads a proposition definition into the given PROTEMPA knowledge base.
     * This will only get called if the proposition definition has not already
     * been loaded.
     *
     * @param id
     *            a proposition id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use. Guaranteed not
     *            <code>null</code>.
     * @return the {@link PropositionDefinition}, or <code>null</code> if none
     * with the given id was found.
     */
    PropositionDefinition readPropositionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads a constant definition into the given PROTEMPA knowledge base. This
     * only will get called if the proposition definition has not already been
     * loaded.
     * 
     * @param id a constant id {@link String}.  Guaranteed not
     *            <code>null</code>.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use. Guaranteed not
     *            <code>null</code>.
     * @return the {@link ConstantDefinition}, or <code>null</code> if none with
     *         the given id was found.
     */
    ConstantDefinition readConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions that are associated with the given AND
     * clause of term subsumptions. A proposition matches if and only if at
     * least one term in every subsumption is related to that proposition.
     * 
     * @param termSubsumptions
     *            the term subsumptions to match in the knowledge source
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use
     * 
     * @return a {@link List} of proposition definitions associated with the
     *         given term IDs
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the knowledge source
     */
    List<String> getPropositionsByTermSubsumption(
            And<TermSubsumption> termSubsumptions)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions for a given term.
     * 
     * @param termId
     *            the term ID to look up
     * @return a {@link List} of proposoition IDs related to the given term
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the Knowledge source
     */
    List<String> getPropositionsByTerm(String termId)
            throws KnowledgeSourceReadException;

    /**
     * Reads the value set from the knowledge base and returns it. This only
     * will get called if the value set has not already been loaded.
     * 
     * @param id
     *            The id of the value set to read
     * @param kb
     *            The knowledge base to read the value set from
     * @return The ValueSet object
     */
    ValueSet readValueSet(String id, KnowledgeBase kb)
            throws KnowledgeSourceReadException;

    /**
     * Reads all of the proposition definitions that have the
     * <code>inverseIsA> relationship with a given proposition definition.
     *
     * Implementing this is a little tricky because you need to check if
     * a proposition definition has already been loaded before trying to
     * create it.
     *
     * @param propDef a {@link PropositionDefinition}.
     * @param kb a PROTEMPA {@link KnowledgeBase}. This can be used to check
     * if a proposition definition has already been created.
     * @return a {@link List<PropositionDefinition>}.
     * @throws KnowledgeSourceReadException if an error occurred in executing
     * this operation.
     */
    List<PropositionDefinition> readInverseIsA(PropositionDefinition propDef,
            KnowledgeBase kb)
            throws KnowledgeSourceReadException;

    /**
     * Reads all of the proposition definitions that have the
     * <code>abstractedFrom> relationship with a given proposition definition.
     *
     * Implementing this is a little tricky because you need to check if
     * a proposition definition has already been loaded before trying to
     * create it.
     *
     * @param propDef a {@link PropositionDefinition}.
     * @param kb a PROTEMPA {@link KnowledgeBase}. This can be used to check
     * if a proposition definition has already been created.
     * @return a {@link List<PropositionDefinition>}.
     * @throws KnowledgeSourceReadException if an error occurred in executing
     * this operation.
     */
    List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition abstractionDefinition, KnowledgeBase kb)
            throws KnowledgeSourceReadException;
}
