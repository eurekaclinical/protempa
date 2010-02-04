package org.protempa.dsb;

import junit.framework.TestCase;
import org.protempa.AbstractDataSourceBackend;
import org.protempa.DataSource;
import org.protempa.DataSourceBackend;
import org.protempa.DataSourceUpdatedEvent;
import org.protempa.SourceListener;

/**
 * @author Andrew Post
 */
public class SchemaAdaptorUpdatedEventTest extends TestCase {
	private DataSource dataSource;

	private MockSchemaAdaptor schemaAdaptor;

    private MockDataSourceBackend dataSourceBackend;
    
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.schemaAdaptor = new MockSchemaAdaptor();
        this.dataSourceBackend = new MockDataSourceBackend(this.schemaAdaptor);
		this.dataSource = new DataSource(
                new DataSourceBackend[] {this.dataSourceBackend});
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
		this.schemaAdaptor = null;
		this.dataSource.close();
		this.dataSource = null;
	}

	private static final class MockDataSourceListener implements
			SourceListener<DataSourceUpdatedEvent> {

		private boolean dataSourceUpdatedEventFired;

		boolean wasDataSourceUpdatedEventFired() {
			return this.dataSourceUpdatedEventFired;
		}

		public void sourceUpdated(DataSourceUpdatedEvent event) {
			this.dataSourceUpdatedEventFired = true;	
		}
	}

    private static final class MockDataSourceBackend
            extends AbstractDataSourceBackend {
        public MockDataSourceBackend(SchemaAdaptor schemaAdaptor) {
            super(schemaAdaptor);
        }
    }

	public void testDataSourceUpdatedEvent() {
		MockDataSourceListener listener = new MockDataSourceListener();
		this.dataSource.addSourceListener(listener);
		this.schemaAdaptor.fireSchemaAdaptorUpdated();
		this.dataSource.removeSourceListener(listener);
		assertTrue(listener.wasDataSourceUpdatedEventFired());
	}

}
