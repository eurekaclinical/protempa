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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public class DoProcessThread extends AbstractDoProcessThread<ExecutionStrategy> {
    
    private static final Logger LOGGER = Logger.getLogger(DoProcessThread.class.getName());
    
    private final BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue;
    private final DataStreamingEvent<Proposition> doProcessPoisonPill;
    private final AlgorithmSource algorithmSource;

    DoProcessThread(BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue, 
            BlockingQueue<QueueObject> hqrQueue, 
            DataStreamingEvent<Proposition> doProcessPoisonPill, 
            QueueObject hqrPoisonPill, Query query, Thread producer, 
            AlgorithmSource algorithmSource, KnowledgeSource knowledgeSource, 
            Collection<PropositionDefinition> propositionDefinitionCache) {
        super(hqrQueue, hqrPoisonPill, query, producer, 
                knowledgeSource, propositionDefinitionCache, LOGGER);
        this.doProcessQueue = doProcessQueue;
        this.doProcessPoisonPill = doProcessPoisonPill;
        this.algorithmSource = algorithmSource;
    }
    
    @Override
    protected void doProcessDataLoop() throws InterruptedException {
        int count = 0;
        DataStreamingEvent<Proposition> dse;
        while (!isInterrupted() && ((dse = doProcessQueue.take()) != doProcessPoisonPill)) {
            try {
                List<Proposition> data = dse.getData();
                doProcessData(dse.getKeyId(), data.iterator(), data.size(), getQuery());
                count++;
            } finally {
                closeWorkingMemory();
            }
        }
        log(Level.INFO, "Processed {0} keys", count);
    }
    
    @Override
    ExecutionStrategy selectExecutionStrategy() {
        Query query = getQuery();
        if (query.getDatabasePath() != null) {
            log(Level.FINER, "Chosen stateful execution strategy");
            return new StatefulExecutionStrategy(
                    this.algorithmSource, query);
        } else {
            log(Level.FINER, "Chosen stateless execution strategy");
            return new StatelessExecutionStrategy(
                    this.algorithmSource, query);
        }
    }
    
}
