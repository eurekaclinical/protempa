package org.protempa.dsb;

import org.protempa.AbstractDataSourceBackend;


/**
 *
 * @author Andrew Post
 */
class MockDataSourceBackend extends AbstractDataSourceBackend {
    MockDataSourceBackend(SchemaAdaptor schemaAdaptor) {
        super(schemaAdaptor);
    }

    MockDataSourceBackend(SchemaAdaptor schemaAdaptor,
			TerminologyAdaptor terminologyAdaptor) {
        super(schemaAdaptor, terminologyAdaptor);
    }
}
