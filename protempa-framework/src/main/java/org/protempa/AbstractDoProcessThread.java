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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractDoProcessThread<E extends ExecutionStrategy> extends AbstractThread {

    private final BlockingQueue<QueueObject> hqrQueue;
    private final QueueObject hqrPoisonPill;
    private final Thread producer;
    private E executionStrategy;
    private final List<QueryException> exceptions;
    private final Collection<PropositionDefinition> propositionDefinitionCache;
    private final KnowledgeSource knowledgeSource;
    private DerivationsBuilder derivationsBuilder;

    AbstractDoProcessThread(
            BlockingQueue<QueueObject> hqrQueue,
            QueueObject hqrPoisonPill, Query query,
            Thread producer,
            KnowledgeSource knowledgeSource,
            Collection<PropositionDefinition> propositionDefinitionCache,
            Logger logger) {
        super(query, logger, "protempa.executor.DoProcessThread");
        this.hqrQueue = hqrQueue;
        this.producer = producer;
        this.hqrPoisonPill = hqrPoisonPill;
        this.exceptions = new ArrayList<>();
        this.knowledgeSource = knowledgeSource;
        this.propositionDefinitionCache = propositionDefinitionCache;
    }

    @Override
    public void run() {
        log(Level.FINER, "Start do process thread");
        try {
            initialize();
            doProcessDataLoop();
            swallowHQRPoisonPill();
        } catch (InterruptedException ex) {
            handleInterrupted(ex);
        } catch (ExecutionStrategyInitializationException | KnowledgeSourceReadException ex) {
            handleException();
            this.exceptions.add(new QueryException(getQuery().getName(), ex));
        } catch (Error | RuntimeException t) {
            log(Level.SEVERE, "Do process thread threw runtime error", t);
            handleException();
            throw t;
        } finally {
            shutdownExecutionStrategy();
        }
        log(Level.FINER, "End do process thread");
    }

    protected void doProcessData(String keyId, Iterator<Proposition> dataItr, int sizeHint, Query query) throws InterruptedException {
        Iterator<Proposition> resultsItr;
        if (this.executionStrategy != null) {
            resultsItr = this.executionStrategy.execute(keyId, dataItr);
        } else {
            resultsItr = dataItr;
        }
        Map<Proposition, List<Proposition>> forwardDerivations
                = this.derivationsBuilder.toForwardDerivations();
        Map<Proposition, List<Proposition>> backwardDerivations
                = this.derivationsBuilder.toBackwardDerivations();
        Map<UniqueId, Proposition> refs = new HashMap<>();
        List<Proposition> filteredPropositions = extractRequestedPropositions(resultsItr, refs, sizeHint);
        if (isLoggable(Level.FINEST)) {
            log(Level.FINEST, "Proposition ids: {0}", String.join(", ", query.getPropositionIds()));
            log(Level.FINEST, "Filtered propositions: {0}", filteredPropositions);
            log(Level.FINEST, "Forward derivations: {0}", forwardDerivations);
            log(Level.FINEST, "Backward derivations: {0}", backwardDerivations);
            log(Level.FINEST, "References: {0}", refs);
        }
        this.hqrQueue.put(new QueueObject(keyId, filteredPropositions,
                forwardDerivations, backwardDerivations, refs));
        log(Level.FINER, "Results put on query result handler queue");
        this.derivationsBuilder.reset();
    }

    protected E getExecutionStrategy() {
        return this.executionStrategy;
    }

    protected abstract void doProcessDataLoop() throws InterruptedException;

    private void swallowHQRPoisonPill() throws InterruptedException {
        this.hqrQueue.put(this.hqrPoisonPill);
    }

    private void handleInterrupted(InterruptedException ex) {
        // by the HQR thread
        log(Level.FINER, "Do process thread interrupted", ex);
        if (producer != null) {
            producer.interrupt();
        }
    }

    private void shutdownExecutionStrategy() {
        if (executionStrategy != null) {
            try {
                executionStrategy.shutdown();
            } catch (ExecutionStrategyShutdownException ex) {
                this.exceptions.add(new QueryException(getQuery().getName(), ex));
            }
        }
    }

    private void handleException() {
        if (producer != null) {
            producer.interrupt();
        }
        try {
            swallowHQRPoisonPill();
        } catch (InterruptedException ignore) {
            log(Level.SEVERE, "Failed to stop the query results handler queue; the query may be hung", ignore);
        }
    }

    private void initialize() throws KnowledgeSourceReadException, ExecutionStrategyInitializationException {
        Query query = getQuery();
        if (hasSomethingToAbstract(query) || query.getDatabasePath() != null) {
            this.executionStrategy = selectExecutionStrategy();
            this.executionStrategy.initialize(this.propositionDefinitionCache);
            this.derivationsBuilder = this.executionStrategy.getDerivationsBuilder();
        } else {
            this.derivationsBuilder = new DerivationsBuilder();
        }
    }

    protected List<Proposition> extractRequestedPropositions(
            Iterator<Proposition> propositions, Map<UniqueId, Proposition> refs,
            int sizeHint) {
        List<Proposition> result = new ArrayList<>(sizeHint > -1 ? sizeHint : 200);
        if (propositions != null) {
            while (!isInterrupted() && propositions.hasNext()) {
                Proposition prop = propositions.next();
                refs.put(prop.getUniqueId(), prop);
                result.add(prop);
            }
        }
        return result;
    }

    protected void closeWorkingMemory() {
        if (this.executionStrategy != null) {
            this.executionStrategy.closeCurrentWorkingMemory();
        }
    }

    List<QueryException> getExceptions() {
        return this.exceptions;
    }
    
    abstract E selectExecutionStrategy();

    private boolean hasSomethingToAbstract(Query query) throws KnowledgeSourceReadException {
        if (!this.knowledgeSource.readAbstractionDefinitions(query.getPropositionIds()).isEmpty()
                || !this.knowledgeSource.readContextDefinitions(query.getPropositionIds()).isEmpty()) {
            return true;
        }
        for (PropositionDefinition propDef : query.getPropositionDefinitions()) {
            if (propDef instanceof AbstractionDefinition || propDef instanceof ContextDefinition) {
                return true;
            }
        }
        return false;
    }

}
