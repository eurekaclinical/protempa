package org.protempa.backend.dsb;

import org.protempa.backend.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.AbstractBackend;
import org.protempa.DataSource;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.DataSourceReadException;
import org.protempa.QuerySession;
import org.protempa.proposition.Proposition;


/**
 * Convenience class for implementing a data source backend.
 * 
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
		AbstractBackend<DataSourceBackendUpdatedEvent, DataSource> implements
		DataSourceBackend {

    @Override
    public Map<String, List<Proposition>> readPropositions(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return null;
    }

    @Override
    public void close() {
    }

    /**
     * Notifies registered listeners that the backend has been updated.
     *
     * @see DataSourceBackendUpdatedEvent
     * @see DataSourceBackendListener
     */
    private void fireDataSourceBackendUpdated() {
        fireBackendUpdated(new DataSourceBackendUpdatedEvent(this));
    }

    @Override
    public String getKeyTypeDisplayName() {
        return getKeyType();
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        return getKeyTypeDisplayName() + "s";
    }
}
