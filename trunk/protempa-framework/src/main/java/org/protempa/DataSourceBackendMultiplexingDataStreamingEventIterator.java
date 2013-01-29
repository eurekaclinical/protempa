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
import org.arp.javautil.arrays.Arrays;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
public class DataSourceBackendMultiplexingDataStreamingEventIterator
        extends MultiplexingDataStreamingEventIterator {

    private final List<? extends DataStreamingEventIterator<UniqueIdPair>> refs;
    private final List<? extends DataStreamingEventIterator<Proposition>> itrs;

    public DataSourceBackendMultiplexingDataStreamingEventIterator(
            List<? extends DataStreamingEventIterator<Proposition>> itrs,
            List<? extends DataStreamingEventIterator<UniqueIdPair>> refs) {
        super(itrs, new ReferenceLinkerPropositionDataStreamerProcessor(refs));
        this.refs = refs;
        this.itrs = itrs;
    }

    /**
     * Closes resources associated with the iterators specified in the
     * constructor. Subclasses that override this method must call
     * <code>super.close()</code> to ensure that those resources are cleaned up
     * properly.
     *
     * @throws DataSourceReadException if an error occurred closing resources.
     */
    @Override
    public void close() throws DataSourceReadException {
        List<DataSourceReadException> exceptions =
                new ArrayList<DataSourceReadException>();
        for (DataStreamingEventIterator<UniqueIdPair> it : this.refs) {
            try {
                it.close();
            } catch (DataSourceReadException ex) {
                exceptions.add(ex);
            }
        }
        for (DataStreamingEventIterator<Proposition> it : this.itrs) {
            try {
                it.close();
            } catch (DataSourceReadException ex) {
                exceptions.add(ex);
            }
        }
        if (!exceptions.isEmpty()) {
            DataSourceReadException ex = new DataSourceReadException(
                    "Error occurred reading from data source");
            List<StackTraceElement> elts = new ArrayList<StackTraceElement>();
            for (DataSourceReadException subex : exceptions) {
                Arrays.addAll(elts, subex.getStackTrace());
            }
            ex.setStackTrace(elts.toArray(new StackTraceElement[elts.size()]));
            throw ex;
        }
    }
}
