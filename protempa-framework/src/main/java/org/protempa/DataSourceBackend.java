package org.protempa;

import org.protempa.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.proposition.Proposition;
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

    Map<String, List<Proposition>> readPropositions(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs) throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException;

}
