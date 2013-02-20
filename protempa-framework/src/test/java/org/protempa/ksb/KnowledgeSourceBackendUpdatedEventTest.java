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
package org.protempa.ksb;

import org.protempa.backend.ksb.SimpleKnowledgeSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.*;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class KnowledgeSourceBackendUpdatedEventTest extends TestCase {

    private KnowledgeSource knowledgeSource;
    private SimpleKnowledgeSourceBackend knowledgeSourceBackend;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.knowledgeSourceBackend = new SimpleKnowledgeSourceBackend();
        this.knowledgeSource = new KnowledgeSourceImpl(
                new KnowledgeSourceBackend[]{this.knowledgeSourceBackend});
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.knowledgeSourceBackend = null;
        this.knowledgeSource.close();
        this.knowledgeSource = null;
    }

    private static final class MockKnowledgeSourceListener implements
            SourceListener<KnowledgeSourceUpdatedEvent> {

        private boolean knowledgeSourceUpdatedEventFired;

        @Override
        public void sourceUpdated(KnowledgeSourceUpdatedEvent event) {
            this.knowledgeSourceUpdatedEventFired = true;
        }

        boolean wasKnowledgeSourceUpdatedEventFired() {
            return this.knowledgeSourceUpdatedEventFired;
        }

        @Override
        public void closedUnexpectedly(SourceClosedUnexpectedlyEvent e) {
        }
    }

    public void testDataSourceUpdatedEvent() {
        MockKnowledgeSourceListener listener =
                new MockKnowledgeSourceListener();
        this.knowledgeSource.addSourceListener(listener);
        this.knowledgeSourceBackend.fireKnowledgeSourceBackendUpdated();
        this.knowledgeSource.removeSourceListener(listener);
        assertTrue(listener.wasKnowledgeSourceUpdatedEventFired());
    }
}
