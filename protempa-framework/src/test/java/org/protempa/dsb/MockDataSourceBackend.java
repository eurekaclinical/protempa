package org.protempa.dsb;

import org.protempa.AbstractDataSourceBackend;
import org.protempa.BackendInitializationException;
import org.protempa.DataSourceBackendFailedValidationException;
import org.protempa.KnowledgeSource;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;


/**
 *
 * @author Andrew Post
 */
class MockDataSourceBackend extends AbstractDataSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
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
    public String getDisplayName() {
        return "Mock Data Source Backend";
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException {
        
    }
    
}
