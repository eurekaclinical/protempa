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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.proposition.Proposition;

/**
 * Provides the same functionality as {@link DataStreamer} except as an
 * iterator. While {@link DataStreamer} implements pushing data from a data
 * source to the caller, this iterator supports pulling data from the data
 * source.
 *
 * @author Andrew Post
 */
final class DataStreamerIterator<E extends Proposition> implements AutoCloseable {

    private final List<DataStreamingEventIterator<E>> itrs;
    private final List<DataStreamingEvent<E>> currentElt;
    private boolean hasNext;
    private DataStreamingEvent<E> result;
    private boolean hasNextComputed;
    private int i = 0;
    private DataStreamingEvent<E> currentMin;
    private String nextKeyId;
    private final BlockingQueue<Integer> queue = new SynchronousQueue<>();
    private final ConsumerThread[] getterThreads;

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
        this.currentElt = new ArrayList<>(itrs.size());
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
            advance(j);
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
        this.getterThreads = new DataStreamerIterator.ConsumerThread[4];
        for (int k = 0; k < this.getterThreads.length; k++) {
            this.getterThreads[k] = new ConsumerThread(k);
            this.getterThreads[k].start();
        }
    }

    String getNextKeyId() {
        return this.nextKeyId;
    }

    @Override
    public void close() {
        for (int k = 0; k < this.getterThreads.length; k++) {
            this.getterThreads[k].requestDone();
            try {
                this.getterThreads[k].join();
            } catch (InterruptedException ex) {
                ProtempaUtil.logger().log(Level.FINER, "Interrupted thread {0}", this.getterThreads[k].getName());
            }
        }
    }

    private final class ConsumerThread extends Thread {

        private volatile boolean done;
        private final List<DataSourceReadException> exceptions;

        ConsumerThread(int k) {
            super("DataStreamerIterator GetterThread " + k);
            this.done = false;
            this.exceptions = new ArrayList<>();
        }

        @Override
        public void run() {
            try {
                synchronized (this) {
                    while (!this.done) {
                        Integer index;
                        do {
                            while ((index = queue.poll(500, TimeUnit.MILLISECONDS)) != null && index > -1) {
                                advance(index);
                            }
                        } while (!done && index == null);
                        wait();
                    }
                }
            } catch (InterruptedException ex) {
                ProtempaUtil.logger().log(Level.FINER, "Interrupted thread {0}", getName());
            } catch (DataSourceReadException ex) {
                this.exceptions.add(ex);
            }
        }

        void requestDone() {
            this.done = true;
        }

        void joinPatient() {
            synchronized (this) {
                notify();
            }
        }

        List<DataSourceReadException> getExceptions() {
            return exceptions;
        }
        
    }

    /**
     * We loop through the iterators passed into the constructor and return
     * whether the the next item in at least one iterator is for the right
     * keyId.
     *
     * @return <code>true</code> or <code>false</code>.
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
                        currentMin = min(elt);
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
                    try {
                        for (int k = 0; k < this.getterThreads.length; k++) {
                            getterThreads[k].requestDone();
                        }
                        for (int k = 0; k < this.getterThreads.length; k++) {
                            this.getterThreads[k].joinPatient();
                        }
                        for (int k = 0; k < this.getterThreads.length; k++) {
                            getterThreads[k].join();
                        }
                    } catch (InterruptedException ex) {
                        ProtempaUtil.logger().log(Level.FINER, "Interrupted thread");
                    }
                } else {
                    int n = this.currentElt.size();
                    for (; i < n; i++) {
                        DataStreamingEvent<E> elt = this.currentElt.get(i);
                        if (elt != null && elt.getKeyId().equals(
                                this.currentMin != null
                                        ? this.currentMin.getKeyId() : null)) {
                            this.result = elt;
                            this.nextKeyId = elt.getKeyId();
                            try {
                                queue.put(i);
                            } catch (InterruptedException ex) {
                                ProtempaUtil.logger().log(Level.FINER, "Interrupted putting on queue");
                            }
                            i++;
                            break;
                        }
                    }
                    try {
                        for (int k = 0; k < this.getterThreads.length; k++) {
                            queue.put(-1);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataStreamerIterator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int k = 0; k < getterThreads.length; k++) {
                        getterThreads[k].joinPatient();
                    }
                    if (this.i == n && this.result == null) {
                        this.i = 0;
                        stayInLoop = true;
                    }
                }
            } while (this.hasNext && stayInLoop);
            this.hasNextComputed = true;
        }
        
        for (int k = 0; k < this.getterThreads.length; k++) {
            List<DataSourceReadException> exceptions = this.getterThreads[k].getExceptions();
            if (!exceptions.isEmpty()) {
                throw exceptions.get(0);
            }
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

    private void advance(int j)
            throws DataSourceReadException {
        DataStreamingEventIterator<E> itr = this.itrs.get(j);
        if (itr.hasNext()) {
            DataStreamingEvent<E> next = itr.next();
            this.currentElt.set(j, next);
        } else {
            this.currentElt.set(j, null);
        }
    }

    private DataStreamingEvent min(DataStreamingEvent elt2) {
        DataStreamingEvent elt1 = this.currentMin;
        if (elt1 == null) {
            return elt2;
        } else if (elt2 == null) {
            return elt1;
        } else {
            return elt1.getKeyId().compareTo(elt2.getKeyId()) < 0 ? elt1 : elt2;
        }
    }
}
