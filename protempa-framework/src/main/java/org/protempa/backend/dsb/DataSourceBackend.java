package org.protempa.backend.dsb;

import org.protempa.backend.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.Backend;
import org.protempa.DataSource;
import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.DataSourceReadException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.QuerySession;
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
