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

import java.util.List;
import org.protempa.proposition.Proposition;

/**
 * Used by {@link MultiplexingDataStreamingEventIterator} for grouping
 * propositions by key id. An instance of this class or a subclass must be 
 * passed into {@link MultiplexingDataStreamingEventIterator}. This class' 
 * methods are intended only to be called by 
 * {@link MultiplexingDataStreamingEventIterator}. Subclasses may override 
 * {@link #fireKeyCompleted} to supply different behavior when a key id's
 * proposition list has been completedly created. Such subclasses'
 * {@link fireKeyCompleted} method must call 
 * <code>super.fireKeyCompleted</code>.
 * 
 * @author Andrew Post
 */
class PropositionDataStreamerProcessor
        extends DataStreamerProcessor<Proposition> {

    private DataStreamingEventHandler handler;

    /**
     * Creates an instance.
     */
    PropositionDataStreamerProcessor() {
    }

    /**
     * Sets a class into which the processor puts a completed list of
     * propositions for a key id. This is used internally by 
     * {@link MultiplexingDataStreamingEventIterator}.
     * 
     * @param handler the {@link DataStreamingEventHandler} into which the 
     * processor puts a completed
     * list of propositions for a key id.
     */
    void setHandler(DataStreamingEventHandler handler) {
        this.handler = handler;
    }

    /**
     * Called by {@link MultiplexingDataStreamingEventIterator} when a key id's
     * propositions have been completely assembled.
     * 
     * @param keyId a key id {@link String}.
     * @param data
     * @throws DataSourceReadException 
     */
    @Override
    protected void fireKeyCompleted(String keyId, List<Proposition> data)
            throws DataSourceReadException {
        assert this.handler != null : "handler cannot be null";
        this.handler.handle(keyId, data);
    }
    
    public void close() {
        
    }
    
}
