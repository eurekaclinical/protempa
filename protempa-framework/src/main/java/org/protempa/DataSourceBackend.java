package org.protempa;

import org.protempa.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.proposition.Constant;
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

    Map<String, List<Constant>> getConstantPropositions(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException;

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException;

}
