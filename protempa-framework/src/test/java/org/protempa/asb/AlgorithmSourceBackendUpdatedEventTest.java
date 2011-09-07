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
        this.algorithmSource = new AlgorithmSource(
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
