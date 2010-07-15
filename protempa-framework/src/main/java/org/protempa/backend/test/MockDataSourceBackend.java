package org.protempa.backend.test;

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

    public List<String> getAllKeyIds(int start, int finish,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds) throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds,
            Set<String> paramIds) throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<Event>> getEventsAsc(
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Event> getEventsAsc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Event> getEventsDesc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAsc(Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId, 
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId, 
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UnitFactory getUnitFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getKeyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getKeyTypeDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getKeyTypePluralDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addBackendListener(
            BackendListener<DataSourceBackendUpdatedEvent> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeBackendListener(
            BackendListener<DataSourceBackendUpdatedEvent> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
