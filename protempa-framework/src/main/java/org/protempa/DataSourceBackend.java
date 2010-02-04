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
     *
     * @param start the first key id to retrieve, must be > 0.
     * @param count the number of key ids to retrieve, must be > 0.
     * @return
     * @throws org.protempa.DataSourceReadException
     */
    List<String> getAllKeyIds(int start, int count)
            throws DataSourceReadException;

    List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(
            Set<String> eventIds, Long minValidDate, Long maxValidDate)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, Long minValidDate,
            Long maxValidDate) throws DataSourceReadException;

    List<Event> getEventsAsc(String keyId, 
            Set<String> eventIds, Long minValidDate, Long maxValidDate)
            throws DataSourceReadException;

    List<Event> getEventsDesc(String keyId, 
            Set<String> eventIds, Long minValidDate, Long maxValidDate)
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> paramIds, Long minValidDate,
            Long maxValidDate) throws DataSourceReadException;

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            Long minValidDate, Long maxValidDate)
            throws DataSourceReadException;

    List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId, 
            Set<String> paramIds, Long minValidDate,
            Long maxValidDate) throws DataSourceReadException;

    List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId, 
            Set<String> paramIds, Long minValidDate,
            Long maxValidDate) throws DataSourceReadException;

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

}
