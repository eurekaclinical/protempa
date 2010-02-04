package org.protempa.dsb;

import junit.framework.TestCase;
import org.protempa.DataSource;
import org.protempa.DataSourceBackend;
import org.protempa.DataSourceUpdatedEvent;
import org.protempa.SourceListener;

/**
 * @author Andrew Post
 */
public class TerminologyAdaptorUpdatedEventTest extends TestCase {
	private DataSource dataSource;

	private MockTerminologyAdaptor terminologyAdaptor;


	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.terminologyAdaptor = new MockTerminologyAdaptor();
		MockSchemaAdaptor schemaAdaptor = new MockSchemaAdaptor();
		this.dataSource = new DataSource(
                new DataSourceBackend[] {
            new MockDataSourceBackend(schemaAdaptor,
                this.terminologyAdaptor)});
		this.dataSource.getAllKeyIds(1, 1000); // Force it to initialize.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.terminologyAdaptor = null;
		this.dataSource.close();
		this.dataSource = null;
	}

	private static final class MockDataSourceListener implements
			SourceListener<DataSourceUpdatedEvent> {

		private boolean dataSourceUpdatedEventFired;

		public void sourceUpdated(DataSourceUpdatedEvent event) {
			this.dataSourceUpdatedEventFired = true;
		}

		boolean wasDataSourceUpdatedEventFired() {
			return this.dataSourceUpdatedEventFired;
		}

	}

	public void testDataSourceUpdatedEvent() {
		MockDataSourceListener listener = new MockDataSourceListener();
		this.dataSource.addSourceListener(listener);
		this.terminologyAdaptor.fireTerminologyAdaptorUpdated();
		this.dataSource.removeSourceListener(listener);
		assertTrue(listener.wasDataSourceUpdatedEventFired());
	}

}
