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
package org.protempa.asb;

import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.*;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class AlgorithmSourceBackendUpdatedEventTest extends TestCase {

    private AlgorithmSource algorithmSource;
    private MockAlgorithmSourceBackend algorithmSourceBackend;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.algorithmSourceBackend = new MockAlgorithmSourceBackend();
        this.algorithmSource = new AlgorithmSourceImpl(
                new AlgorithmSourceBackend[]{this.algorithmSourceBackend});
        this.algorithmSource.readAlgorithms(); // Force it to initialize.
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.algorithmSourceBackend = null;
        this.algorithmSource.close();
        this.algorithmSource = null;
    }

    private static final class MockAlgorithmSourceListener implements
            SourceListener<AlgorithmSourceUpdatedEvent> {

        private boolean algorithmSourceUpdatedEventFired;

        @Override
        public void sourceUpdated(AlgorithmSourceUpdatedEvent event) {
            this.algorithmSourceUpdatedEventFired = true;
        }

        boolean wasAlgorithmSourceUpdatedEventFired() {
            return this.algorithmSourceUpdatedEventFired;
        }

        @Override
        public void closedUnexpectedly(SourceClosedUnexpectedlyEvent e) {
            
        }
    }

    public void testDataSourceUpdatedEvent() {
        MockAlgorithmSourceListener listener =
                new MockAlgorithmSourceListener();
        this.algorithmSource.addSourceListener(listener);
        this.algorithmSourceBackend.fireAlgorithmSourceBackendUpdated();
        this.algorithmSource.removeSourceListener(listener);
        assertTrue(listener.wasAlgorithmSourceUpdatedEventFired());
    }
}
