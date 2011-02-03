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
 * Interface for data source backends, which provide access to data sources
 * for PROTEMPA.
 * 
 * @author Andrew Post
 */
public interface DataSourceBackend extends
		Backend<DataSourceBackendUpdatedEvent, DataSource>{

    Map<String, List<Constant>> getConstantPropositions(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<Event>> getEvents(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    Map<String, List<PrimitiveParameter>> getPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException;

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException;

}
