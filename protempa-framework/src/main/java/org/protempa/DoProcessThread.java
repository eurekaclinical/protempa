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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
class DoProcessThread extends AbstractThread {

    private static final Logger LOGGER = Logger.getLogger(DoProcessThread.class.getName());

    private final BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue;
    private final BlockingQueue<QueueObject> hqrQueue;
    private final QueueObject hqrPoisonPill;
    private final DataStreamingEvent<Proposition> doProcessPoisonPill;
    private final Thread producer;
    private final ExecutionStrategy executionStrategy;
    private int count;
    private final List<QueryException> exceptions;
    private final Query query;

    DoProcessThread(BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue,
            BlockingQueue<QueueObject> hqrQueue,
            DataStreamingEvent<Proposition> doProcessPoisonPill,
            QueueObject hqrPoisonPill, Query query, ExecutionStrategy executionStrategy,
            Thread producer) {
        super(query, LOGGER, "protempa.executor.DoProcessThread");
        this.doProcessQueue = doProcessQueue;
        this.hqrQueue = hqrQueue;
        this.doProcessPoisonPill = doProcessPoisonPill;
        this.producer = producer;
        this.hqrPoisonPill = hqrPoisonPill;
        this.executionStrategy = executionStrategy;
        this.exceptions = new ArrayList<>();
        this.query = query;
    }

    List<QueryException> getExceptions() {
        return this.exceptions;
    }

    int getCount() {
        return this.count;
    }
    
    @Override
    public void run() {
        log(Level.FINER, "Start do process thread");
        try {
            DataStreamingEvent<Proposition> dse;
            while (!isInterrupted() && ((dse = doProcessQueue.take()) != doProcessPoisonPill)) {
                String keyId = dse.getKeyId();
                try {
                    Iterator<Proposition> resultsItr;
                    List<Proposition> data = dse.getData();
                    if (this.executionStrategy != null) {
                        resultsItr = this.executionStrategy.execute(keyId, data);
                    } else {
                        resultsItr = data.iterator();
                    }
                    DerivationsBuilder derivationsBuilder = this.executionStrategy.getDerivationsBuilder();
                    Map<Proposition, List<Proposition>> forwardDerivations = derivationsBuilder.toForwardDerivations();
                    Map<Proposition, List<Proposition>> backwardDerivations = derivationsBuilder.toBackwardDerivations();
                    int inputSize = data.size();
                    Map<UniqueId, Proposition> refs = org.arp.javautil.collections.Collections.newHashMap(inputSize);
                    List<Proposition> filteredPropositions = extractRequestedPropositions(resultsItr, refs, inputSize);
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        log(Level.FINEST, "Proposition ids: {0}", String.join(", ", this.query.getPropositionIds()));
                        log(Level.FINEST, "Filtered propositions: {0}", filteredPropositions);
                        log(Level.FINEST, "Forward derivations: {0}", forwardDerivations);
                        log(Level.FINEST, "Backward derivations: {0}", backwardDerivations);
                        log(Level.FINEST, "References: {0}", refs);
                    }
                    this.hqrQueue.put(new QueueObject(keyId, filteredPropositions, forwardDerivations, backwardDerivations, refs));
                    log(Level.FINER, "Results put on query result handler queue");
                    this.count++;
                    derivationsBuilder.reset();
                } finally {
                    if (this.executionStrategy != null) {
                        this.executionStrategy.closeCurrentWorkingMemory();
                    }
                }
            }
            this.hqrQueue.put(this.hqrPoisonPill);
        } catch (InterruptedException ex) {
            // by the HQR thread
            log(Level.FINER, "Do process thread interrupted", ex);
            producer.interrupt();
        } catch (Error | RuntimeException t) {
            log(Level.SEVERE, "Do process thread threw runtime error", t);
            producer.interrupt();
            try {
                hqrQueue.put(hqrPoisonPill);
            } catch (InterruptedException ignore) {
                log(Level.SEVERE, "Failed to stop the query results handler queue; the query may be hung", ignore);
            }
            throw t;
        }
        log(Level.FINER, "End do process thread");
    }

    private List<Proposition> extractRequestedPropositions(Iterator<Proposition> propositions, Map<UniqueId, Proposition> refs, int inputSize) {
        int outputSize = inputSize + Math.round(inputSize * 0.20f);
        List<Proposition> result = new ArrayList<>(outputSize);
        while (!isInterrupted() && propositions.hasNext()) {
            Proposition prop = propositions.next();
            refs.put(prop.getUniqueId(), prop);
            result.add(prop);
        }
        return result;
    }

}
