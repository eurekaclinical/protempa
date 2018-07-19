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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author arpost
 */
class RetrieveDataThread extends AbstractThread {

    private static final Logger LOGGER = Logger.getLogger(RetrieveDataThread.class.getName());

    private final BlockingQueue<DataStreamingEvent<Proposition>> queue;
    private final DataStreamingEvent<Proposition> poisonPill;
    private final List<QueryException> exceptions;
    private final Iterable<PropositionDefinition> propositionDefinitionCache;
    private final DataSource dataSource;
    private final QueryResultsHandler queryResultsHandler;
    private final Filter filters;

    RetrieveDataThread(BlockingQueue<DataStreamingEvent<Proposition>> queue,
            DataStreamingEvent<Proposition> poisonPill, Query query,
            DataSource dataSource, 
            Iterable<PropositionDefinition> propositionDefinitionCache,
            Filter filters,
            QueryResultsHandler queryResultsHandler) throws QueryException {
        super(query, LOGGER, "protempa.executor.RetrieveDataThread");
        this.queue = queue;
        this.poisonPill = poisonPill;
        this.exceptions = new ArrayList<>();
        this.dataSource = dataSource;
        this.propositionDefinitionCache = propositionDefinitionCache;
        this.filters = filters;
        this.queryResultsHandler = queryResultsHandler;
    }

    public List<QueryException> getExceptions() {
        return this.exceptions;
    }

    @Override
    public void run() {
        log(Level.FINER, "Start retrieve data thread");
        Query query = getQuery();
        boolean itrClosed = false;
        DataStreamingEventIterator<Proposition> itr = null;
        try {
            itr = newDataIterator();
            while (!isInterrupted() && itr.hasNext()) {
                queue.put(itr.next());
            }
            itr.close();
            queue.put(poisonPill);
            itrClosed = true;
        } catch (DataSourceReadException | Error | RuntimeException ex) {
            exceptions.add(new QueryException(query.getName(), ex));
            try {
                queue.put(poisonPill);
            } catch (InterruptedException ignore) {
                log(Level.SEVERE, "Failed to send stop message to the do process thread; the query may be hung", ignore);
            }
        } catch (InterruptedException ex) {
            // by DoProcessThread
            log(Level.FINER, "Retrieve data thread interrupted", ex);
        } finally {
            if (!itrClosed) {
                try {
                    itr.close();
                } catch (DataSourceReadException ignore) {
                }
            }
        }
        log(Level.FINER, "End retrieve data thread");
    }

    private DataStreamingEventIterator<Proposition> newDataIterator() throws DataSourceReadException {
        log(Level.INFO, "Retrieving data");
        Query query = getQuery();
        Set<String> inDataSourcePropIds = new HashSet<>();
        for (PropositionDefinition pd : this.propositionDefinitionCache) {
            if (pd.getInDataSource()) {
                inDataSourcePropIds.add(pd.getId());
            }
        }
        if (isLoggable(Level.FINER)) {
            log(Level.FINER, "Asking data source for {0}", StringUtils.join(inDataSourcePropIds, ", "));
        }
        return this.dataSource.readPropositions(
                Arrays.asSet(query.getKeyIds()), inDataSourcePropIds, 
                this.filters, this.queryResultsHandler);
    }

}
