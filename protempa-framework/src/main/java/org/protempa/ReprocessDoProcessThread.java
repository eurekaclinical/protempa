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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.StatefulSession;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public class ReprocessDoProcessThread extends AbstractDoProcessThread {

    private static final Logger LOGGER = Logger.getLogger(ReprocessDoProcessThread.class.getName());

    ReprocessDoProcessThread(
            BlockingQueue<QueueObject> hqrQueue,
            QueueObject hqrPoisonPill, Query query,
            AlgorithmSource algorithmSource, KnowledgeSource knowledgeSource,
            Collection<PropositionDefinition> propositionDefinitionCache) {
        super(hqrQueue, hqrPoisonPill, query, null, algorithmSource,
                knowledgeSource, propositionDefinitionCache, LOGGER);
    }

    @Override
    protected void doProcessDataLoop() throws InterruptedException {
        int count = 0;
        ExecutionStrategy executionStrategy = getExecutionStrategy();
        if (executionStrategy instanceof StatefulExecutionStrategy) {
            Iterator<Map.Entry<String, StatefulSession>> iterator = 
                    ((StatefulExecutionStrategy) executionStrategy).getDataStore().entrySet().iterator();
            while (!isInterrupted() && iterator.hasNext()) {
                Map.Entry<String, StatefulSession> next = iterator.next();
                try {
                    doProcessData(next.getKey(), next.getValue().iterateObjects(), -1, getQuery());
                    count++;
                } finally {
                    closeWorkingMemory();
                }
            }
        }
        log(Level.INFO, "Processed {0}", count);
    }

}
