package org.protempa.backend.test;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;



/**
 *
 * @author Andrew Post
 */
public final class MockDataSourceBackend implements DataSourceBackend {

    @Override
    public String getDisplayName() {
        return "Mock Data Source Backend";
    }

    @Override
    public Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
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
    public String getKeyTypeDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addBackendListener(
            BackendListener<DataSourceBackendUpdatedEvent> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeBackendListener(
            BackendListener<DataSourceBackendUpdatedEvent> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
