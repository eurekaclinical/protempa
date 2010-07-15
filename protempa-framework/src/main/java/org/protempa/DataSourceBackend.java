package org.protempa;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 *
 * @author Andrew Post
 */
public interface DataSourceBackend extends
		Backend<DataSourceBackendUpdatedEvent, DataSource>{


    /**
     * Returns a list of all key ids in this data set optionally that meet
     * a set of specified constraints.
     *
     * @param start the first key id to retrieve, must be >= 0.
     * @param count the number of key ids to retrieve,
     * must be > 1.
     * @param dataSourceConstraints a {@link DataSourceConstraint} with
     * position and value constraints that reduce the number of key ids
     * returned. If <code>null</code>, no constraints will be applied.
     * @return a newly-created {@link List} of {@link String}s.
     * @throws DataSourceReadException if there was an error reading from
     * the database.
     */
    List<String> getAllKeyIds(int start, int count,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds)
            throws DataSourceReadException;

    Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds,
            Set<String> paramIds) throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    List<Event> getEventsAsc(String keyId, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    List<Event> getEventsDesc(String keyId, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId, 
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId, 
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException;

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

}
