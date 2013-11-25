/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.arp.javautil.arrays.Arrays;
import org.protempa.proposition.Proposition;

/**
 * Iterator-like access to {@link DataStreamingEvent}s. This implementation
 * groups (multiplexes) propositions retrieved from multiple 
 * {@link DataStreamingEventIterators} by key id. It assembles all of the
 * propositions for a given key id with the help of a
 * {@link PropositionDataStreamerProcessor}. After this assembly has been 
 * completed for each key id, the iterator calls the 
 * {@link PropositionDataStreamerProcessor}'s
 * {@link PropositionDataStreamerProcessor#fireKeyCompleted} method with
 * the key id and list of propositions. The iterator gets each assembled key id 
 * and proposition list from the {@link PropositionDataStreamerProcessor} to 
 * return to the caller. Subclasses of {@link PropositionDataStreamerProcessor} 
 * may specify additional processing to be performed on the key id and 
 * proposition list prior to returning them to the caller.
 * 
 * @author Andrew Post
 */
public class MultiplexingDataStreamingEventIterator 
        implements DataStreamingEventIterator<Proposition> {

    private static class MultiplexingDataStreamingEventHandler
            implements DataStreamingEventHandler {

        private DataStreamingEvent<Proposition> event;

        @Override
        public void handle(String keyId, List<Proposition> propositions) {
            this.event =
                    new DataStreamingEvent<>(keyId, propositions);
        }
    }
    
    private final MultiplexingDataStreamingEventHandler handler;
    private DataStreamerIterator itr;
    private final PropositionDataStreamerProcessor processor;
    private final List<? extends DataStreamingEventIterator<Proposition>> itrs;
    private DataStreamingEvent<Proposition> next;

    /**
     * Constructs the iterator with a list of the iterators to multiplex and
     * a {@link PropositionDataStreamerProcessor} to help with 
     * multiplexing.
     * 
     * @param itrs the {@link List<DataStreamingEventIterator<Proposition>>} to
     * multiplex. Cannot be <code>null</code>.
     * @param processor the {@link PropositionDataStreamerProcessor}. Cannot be
     * <code>null</code>.
     */
    MultiplexingDataStreamingEventIterator(
            List<? extends DataStreamingEventIterator<Proposition>> itrs,
            PropositionDataStreamerProcessor processor) {
        if (itrs == null) {
            throw new IllegalArgumentException("itrs cannot be null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null");
        }
        this.itrs = itrs;
        this.processor = processor;
        this.handler = new MultiplexingDataStreamingEventHandler();
        this.processor.setHandler(this.handler);
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        if (this.next == null) {
            if (this.itr == null) {
                this.itr = new DataStreamerIterator(itrs);
            }
            boolean stopOnNext = false;
            while (this.itr.hasNext() && !stopOnNext) {
                if (processor.getKeyId() != null
                        && !processor.getKeyId().equals(
                        this.itr.getNextKeyId())) {
                    stopOnNext = true;
                }
                processor.execute(this.itr.next());
                if (this.handler.event != null) {
                    this.next = this.handler.event;
                    this.handler.event = null;
                    return true;
                }
            }

            if (!stopOnNext) {
                processor.finish();
                if (this.handler.event != null) {
                    this.next = this.handler.event;
                    this.handler.event = null;
                    return true;
                }
            }
        }
        return this.next != null;
    }

    @Override
    public DataStreamingEvent<Proposition> next() 
            throws DataSourceReadException {
        if (hasNext()) {
            DataStreamingEvent<Proposition> result = this.next;
            this.next = null;
            return result;
        } else {
            throw new NoSuchElementException("Past end of iterator");
        }
    }
    
    /**
     * Calls the underlying {@link DataStreamingEventIterator}s 
     * {@link DataStreamingEventIterator#close() } method.
     * 
     * @throws DataSourceReadException 
     */
    @Override
    public void close() throws DataSourceReadException {
        List<DataSourceReadException> exceptions =
                new ArrayList<>();
        for (DataStreamingEventIterator<Proposition> it : this.itrs) {
            try {
                it.close();
            } catch (DataSourceReadException fe) {
                exceptions.add(fe);
            }
        }
        if (!exceptions.isEmpty()) {
            List<StackTraceElement> stes = new ArrayList<>();
            for (Exception ex : exceptions) {
                Arrays.addAll(stes, ex.getStackTrace());
            }
            
            DataSourceReadException ex = new DataSourceReadException(
                    exceptions.size() + " data source backend(s) failed");
            ex.setStackTrace(stes.toArray(new StackTraceElement[stes.size()]));
            throw ex;
        }
    }
}
