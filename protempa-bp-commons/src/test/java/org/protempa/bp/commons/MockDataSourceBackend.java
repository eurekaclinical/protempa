package org.protempa.bp.commons;

import org.protempa.DataSourceBackendFailedValidationException;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.KnowledgeSource;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.*;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

@BackendInfo(displayName = "Mock Data Source Backend")
public class MockDataSourceBackend extends AbstractCommonsDataSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws DataSourceBackendInitializationException {
        super.initialize(config);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UnitFactory getUnitFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getKeyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException {
    }

}
