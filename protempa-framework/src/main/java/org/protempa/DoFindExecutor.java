package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
import java.util.Set;
import org.protempa.dest.Destination;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author arpost
 */
class DoFindExecutor extends ExecutorWithResultsHandler {

    DoFindExecutor(Query query, Destination resultsHandlerFactory, QuerySession querySession, ExecutorStrategy strategy, AbstractionFinder abstractionFinder) throws FinderException {
        super(query, resultsHandlerFactory, querySession, strategy, abstractionFinder);
    }
    
    @Override
    protected void doExecute(Set<String> keyIds,
            final DerivationsBuilder derivationsBuilder,
            final ExecutionStrategy strategy)
            throws ProtempaException {
        new DataStreamingEventProcessor(newDataIterator()) {
            @Override
            void doProcess(DataStreamingEvent next,
                    Set<String> propositionIds)
                    throws FinderException {
                String keyId = next.getKeyId();
                Iterator<Proposition> resultsItr;
                if (strategy != null) {
                    resultsItr = strategy.execute(
                        keyId, propositionIds, next.getData(),
                        null);
                } else {
                    resultsItr = next.getData().iterator();
                }
                processResults(resultsItr, keyId);
            }
        }.process();

    }
    
}
