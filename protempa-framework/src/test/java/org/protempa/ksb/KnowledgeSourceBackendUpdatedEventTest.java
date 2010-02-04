package org.protempa.ksb;

import org.protempa.*;
import org.protempa.backend.test.MockKnowledgeSourceBackend;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class KnowledgeSourceBackendUpdatedEventTest extends TestCase {
	private KnowledgeSource knowledgeSource;

	private MockKnowledgeSourceBackend knowledgeSourceBackend;
    
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.knowledgeSourceBackend = new MockKnowledgeSourceBackend();
		this.knowledgeSource = new KnowledgeSource(
                new KnowledgeSourceBackend[] {this.knowledgeSourceBackend});
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

		public void sourceUpdated(KnowledgeSourceUpdatedEvent event) {
			this.knowledgeSourceUpdatedEventFired = true;
		}

		boolean wasKnowledgeSourceUpdatedEventFired() {
			return this.knowledgeSourceUpdatedEventFired;
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
