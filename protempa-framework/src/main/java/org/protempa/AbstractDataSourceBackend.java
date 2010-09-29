package org.protempa;

import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.ConstantProposition;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;


/**
 * Implements connectivity to an external data source. Delegates to a
 * <code>SchemaAdaptor</code> which implements the actual connectivity to the
 * data source, and optionally to a <code>TerminologyAdaptor</code>, which
 * translates between PROTEMPA's data model and the schema of the data source. A
 * terminology adaptor is only needed if the data source uses different
 * proposition ids, units, etc. from PROTEMPA's data model.
 * 
 * FIXME The terminology adaptor functionality is incomplete. it should convert
 * from standard to local terms, ask the schema adaptor for the data, and copy
 * the results into new primitive parameters with standard terms and appropriate
 * units. The copy should only occur if the terminology adaptor actually changes
 * something.
 *
 * FIXME this should be barebones like the others, and the schema and
 * terminology adaptor stuff belongs in org.protempa.dsb.
 * 
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
		AbstractBackend<DataSourceBackendUpdatedEvent, DataSource> implements
		DataSourceBackend {

    @Override
    public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAsc(Set<String> keyIds, Set<String> paramIds,
			Filter filters, QuerySession qs)
			throws DataSourceReadException {
		return null;
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return null;
    }

    public List<String> getAllKeyIds(int start, int count, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return null;
    }

    @Override
    public Map<String, List<ConstantProposition>> getConstantParameters(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return null;
    }

    @Override
    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return null;
    }

    @Override
    public Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
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
        return null;
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        return null;
    }
}
