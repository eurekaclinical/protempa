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
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public class DoReprocessThread extends DoProcessThread<StatefulExecutionStrategy> {

    private static final Logger LOGGER = Logger.getLogger(DoReprocessThread.class.getName());

    DoReprocessThread(
            BlockingQueue<QueueObject> hqrQueue,
            QueueObject hqrPoisonPill, Query query,
            AlgorithmSource algorithmSource, KnowledgeSource knowledgeSource,
            PropositionDefinitionCache propositionDefinitionCache) throws QueryException {
        super(hqrQueue, hqrPoisonPill, query, null,
                knowledgeSource, propositionDefinitionCache, algorithmSource, LOGGER);
    }

    @Override
    protected void doProcessDataLoop() throws InterruptedException {
        int count = 0;
        StatefulExecutionStrategy executionStrategy = getExecutionStrategy();
        Iterator<String> iterator = executionStrategy.getDataStore().keySet().iterator();
        while (!isInterrupted() && iterator.hasNext()) {
            String keyId = iterator.next();
            try {
                doProcessData(keyId, null, -1, getQuery());
                count++;
            } finally {
                closeWorkingMemory();
            }
        }
        log(Level.INFO, "Processed {0} keys", count);
    }

    @Override
    StatefulExecutionStrategy selectExecutionStrategy() {
        return new StatefulExecutionStrategy(getAlgorithmSource(), getQuery());
    }

}
