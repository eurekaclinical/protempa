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

import java.util.*;

/**
 * An implementation of event-driven data streaming. Data is read from a data
 * source and grouped by keyId with the help of a 
 * {@link DataStreamerProcessor}. When the data for each key id has been
 * completely assembled, the supplied {@link DataStreamerProcessor}'s
 * {@link DataStreamerProcessor#fireKeyCompleted} method is called with the
 * assembled data.
 *
 * @author Andrew Post
 */
final class DataStreamer<E> implements AutoCloseable {

    private DataStreamerIterator itr;

    /**
     * Constructs a data streamer.
     */
    DataStreamer() {
    }

    /**
     * Groups the data in a list of {@link DataStreamingEventIterator}s by
     * key id.
     * 
     * @param processor a {@link DataStreamerProcessor} that helps with the
     * grouping.
     * @param itrs the data to be grouped.
     * @throws DataSourceReadException if an error occurred when iterating over
     * the {@link DataStreamingEventIterator}s.
     */
    void doStream(DataStreamerProcessor<E> processor,
            List<DataStreamingEventIterator<E>> itrs) throws
            DataSourceReadException {
        assert processor != null : "processor cannot be null";
        assert itrs != null : "itrs cannot be null";
        if (this.itr == null) {
            this.itr = new DataStreamerIterator(itrs);
        }
        boolean stopOnNext = false;
        while (this.itr.hasNext() && !stopOnNext) {
            String keyId = processor.getKeyId();
            String nextKeyId = this.itr.getNextKeyId();
            if (keyId != null && !keyId.equals(nextKeyId)) {
                stopOnNext = true;
            }
            processor.execute(this.itr.next());
        }

        if (!stopOnNext) {
            processor.finish();
        }
    }

    @Override
    public void close() {
        if (this.itr != null) {
            this.itr.close();
        }
    }
}
