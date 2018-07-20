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
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eurekaclinical.datastore.DataStore;
import org.eurekaclinical.datastore.DataStoreFactory;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public class DoReprocessThread extends AbstractDoProcessThread<StatefulExecutionStrategy> {

    private static final Logger LOGGER = Logger.getLogger(DoReprocessThread.class.getName());
    private final AlgorithmSource algorithmSource;

    DoReprocessThread(
            BlockingQueue<QueueObject> hqrQueue,
            QueueObject hqrPoisonPill, Query query,
            AlgorithmSource algorithmSource, KnowledgeSource knowledgeSource,
            Collection<PropositionDefinition> propositionDefinitionCache) {
        super(hqrQueue, hqrPoisonPill, query, null,
                knowledgeSource, propositionDefinitionCache, LOGGER);
        this.algorithmSource = algorithmSource;
    }

    @Override
    protected void doProcessDataLoop() throws InterruptedException {
//        try {
            int count = 0;
            StatefulExecutionStrategy executionStrategy = getExecutionStrategy();
        Iterator<String> iterator = executionStrategy.getDataStore().keySet().iterator();
            while (!isInterrupted() && iterator.hasNext()) {
                String keyId = iterator.next();
                try {
                    System.err.println("About to process next record");
                    doProcessData(keyId, null, -1, getQuery());
                    count++;
                    System.err.println("Processed " + count + " records");
                } finally {
                    closeWorkingMemory();
                }
                System.err.println("Closed " + count + " records");
            }
            log(Level.INFO, "Processed {0} keys", count);
//        } catch (IOException ex) {
//            Logger.getLogger(DoReprocessThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    StatefulExecutionStrategy selectExecutionStrategy() {
        return new StatefulExecutionStrategy(this.algorithmSource, getQuery());
    }

}
