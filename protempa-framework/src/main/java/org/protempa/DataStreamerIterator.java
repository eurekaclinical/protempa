/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
import org.protempa.proposition.Proposition;

/**
 * Provides the same functionality as {@link DataStreamer} except as an
 * iterator. While {@link DataStreamer} implements pushing data from a data
 * source to the caller, this iterator supports pulling data from the data 
 * source.
 * @author Andrew Post
 */
final class DataStreamerIterator<E extends Proposition> {

    private final List<DataStreamingEventIterator<E>> itrs;
    private final List<DataStreamingEvent<E>> currentElt;
    private boolean hasNext;
    private DataStreamingEvent<E> result;
    private boolean hasNextComputed;
    private int i = 0;
    private DataStreamingEvent<E> currentMin;
    private String nextKeyId;

    /**
     * Constructs an iterator with a list of iterators containing data from the
     * data source.
     *
     * @param itrs the iterators.
     * @throws SQLException if an error occurred querying the underlying
     * database during iteration.
     */
    DataStreamerIterator(List<DataStreamingEventIterator<E>> itrs) 
            throws DataSourceReadException {
        assert itrs != null : "itrs cannot be null";
        this.itrs = itrs;
        this.currentElt = new ArrayList<DataStreamingEvent<E>>(itrs.size());
        int itrsSize = itrs.size();
        for (int j = 0; j < itrsSize; j++) {
            this.currentElt.add(null);
        }
        /*
         * Stores the most recent element retrieved from each iterator.
         */
        /*
         * Stores the most recent element retrieved from each iterator.
         */
        for (int j = 0; j < itrsSize; j++) {
            advance(this.itrs, j, this.currentElt);
        }
        /*
         * If the iterators have no elements, cut to the chase and arrange for
         * {@link #hasNext()} to return immediately.
         */
        /*
         * If the iterators have no elements, cut to the chase and arrange for
         * {@link #hasNext()} to return immediately.
         */
        for (DataStreamingEvent<E> event : this.currentElt) {
            if (event != null) {
                this.hasNext = true;
                break;
            }
        }
    }

    String getNextKeyId() {
        return this.nextKeyId;
    }

    /**
     * We loop through the iterators passed into the constructor and return
     * whether the the next item in at least one iterator is for the right
     * keyId.
     *
     * @return
     * <code>true</code> or
     * <code>false</code>.
     */
    boolean hasNext() throws DataSourceReadException {
        if (!hasNextComputed) {
            boolean stayInLoop;
            do {
                stayInLoop = false;
                /*
                 * Prior to reading the first iterator, we loop through all of
                 * the iterators to determine the minimum keyId. The minimum
                 * keyId, stored in currentMin, is used for data retrieval. The
                 * value of currentMin will be null if there is no data to
                 * retrieve.
                 */
                if (i == 0) {
                    this.currentMin = null;
                    for (DataStreamingEvent<E> elt : this.currentElt) {
                        currentMin = min(currentMin, elt);
                    }
                }
                /*
                 * If there is no data to retrieve, we'll return false. If there
                 * is data to retrieve, we'll loop through the iterators and
                 * pull data for the current keyId, which is the keyId field of
                 * currentMin. If there's no data for the current keyId, this
                 * should loop through all of the iterators until their ends,
                 * and set hasNext to false.
                 */
                if (currentMin == null) {
                    this.hasNext = false;
                } else {
                    int n = this.currentElt.size();
                    for (; i < n; i++) {
                        DataStreamingEvent<E> elt = this.currentElt.get(i);
                        if (elt != null && elt.getKeyId().equals(
                                this.currentMin != null 
                                ? this.currentMin.getKeyId() : null)) {
                            this.result = elt;
                            this.nextKeyId = elt.getKeyId();
                            advance(this.itrs, i, this.currentElt);
                            i++;
                            break;
                        }
                    }
                    if (this.i == n && this.result == null) {
                        this.i = 0;
                        stayInLoop = true;
                    }
                }
            } while (this.hasNext && stayInLoop);
            this.hasNextComputed = true;
        }
        return hasNext;
    }

    DataStreamingEvent<E> next() {
        assert this.result != null : "result cannot be null";
        this.hasNextComputed = false;
        DataStreamingEvent<E> r = this.result;
        this.result = null;
        this.nextKeyId = null;
        return r;
    }

    private void advance(List<DataStreamingEventIterator<E>> itrs, int j, 
            List<DataStreamingEvent<E>> currentElt) 
            throws DataSourceReadException {
        DataStreamingEventIterator<E> itr2 = itrs.get(j);
        if (itr2.hasNext()) {
            currentElt.set(j, itr2.next());
        } else {
            currentElt.set(j, null);
        }
    }

    private DataStreamingEvent min(DataStreamingEvent elt1, 
            DataStreamingEvent elt2) {
        if (elt1 == null) {
            return elt2;
        } else if (elt2 == null) {
            return elt1;
        } else {
            String keyId = elt1.getKeyId();
            return keyId.compareTo(elt2.getKeyId()) < 0 ? elt1 : elt2;
        }
    }
}
