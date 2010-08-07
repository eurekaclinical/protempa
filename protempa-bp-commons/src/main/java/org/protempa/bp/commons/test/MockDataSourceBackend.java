package org.protempa.bp.commons.test;

import org.protempa.AbstractDataSourceBackend;
import org.protempa.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.*;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

@BackendInfo(displayName = "Mock Data Source Backend")
public class MockDataSourceBackend extends AbstractDataSourceBackend {

    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UnitFactory getUnitFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getKeyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
