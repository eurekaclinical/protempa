package org.protempa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.protempa.dsb.filter.Filter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Read-only access to a database. Data source backends are specified in the
 * constructor that implement the actual database connection(s).
 *
 * If you have multiple data source backends, parameters are resorted before
 * being returned using {@link Collections.sort}. With only one backend, we
 * just use the data as sorted and returned by the backend to take advantage
 * of a potentially optimized sorting algorithm. This may result in a
 * different sort order when the backends are used separately than if they
 * are used together if the backend's sorting algorithm is different from
 * Java's. If you want the sort order to be consistent always with Java, set
 * the system property <code>protempa.datasource.alwaysresort</code> to
 * <code>true</code>.
 * 
 * @author Andrew Post
 * @see DataSourceBackend
 */
public final class DataSource extends AbstractSource<DataSourceUpdatedEvent, DataSourceBackendUpdatedEvent> {

    private static final String ALWAYS_RESORT_SYSTEM_PROPERTY =
            "protempa.datasource.alwaysresort";
    private static final boolean ALWAYS_RESORT =
            Boolean.getBoolean(ALWAYS_RESORT_SYSTEM_PROPERTY);

    static {
        ProtempaUtil.logger().log(Level.FINE,
                "Will the data source always resort? {0}", ALWAYS_RESORT);
    }
    private final BackendManager<DataSourceBackendUpdatedEvent, DataSource, DataSourceBackend> backendManager;

    public DataSource(DataSourceBackend[] backends) {
        super(backends);
        this.backendManager =
                new BackendManager<DataSourceBackendUpdatedEvent, DataSource, DataSourceBackend>(this, backends);

    }

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case).
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException
     *             if the terminology adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException
     *             if the schema adaptor could not be initialized.
     */
    public String getKeyType() throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyType();
            b = backend;
            break;
        }
        assert result != null : "no key type found in " + b.getClass() + "!";
        return result;
    }

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException
     *             if the terminology adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException
     *             if the schema adaptor could not be initialized.
     */
    public String getKeyTypeDisplayName()
            throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyTypeDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type display name found in "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns a plural string representing the type of keys in this data source
     * (e.g., patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException
     *             if the terminology adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException
     *             if the schema adaptor could not be initialized.
     * @throws IllegalStateException
     *             if the schema adaptor and/or terminology adaptor could not be
     *             initialized, or if the schema adaptor returned a
     *             <code>null</code> key type plural display name.
     */
    public String getKeyTypePluralDisplayName()
            throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyTypePluralDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type plural display name found in "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns an object for accessing the granularity of returned data from the
     * schema adaptor for this data source.
     *
     * @return a {@link GranularityFactory}, or <code>null</code> if the data
     *         source could not be initialized or the schema adaptor returned a
     *         null GranularityFactory.
     * @throws TerminologyAdaptorInitializationException
     *             if the terminology adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException
     *             if the schema adaptor could not be initialized.
     * @see SchemaAdaptor#getGranularityFactory()
     */
    public GranularityFactory getGranularityFactory()
            throws DataSourceReadException {
        initializeIfNeeded();
        GranularityFactory result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getGranularityFactory();
            b = backend;
            break;
        }
        assert result != null : "no granularity factory returned from "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns the length units of returned data from
     * this data source.
     *
     * @return a {@link UnitFactory}, or <code>null</code> if the data
     *         source could not be initialized or the schema adaptor returned a
     *         null UnitFactory.
     * @throws DataSourceReadException
     *             if the data source could not be initialized.
     */
    public UnitFactory getUnitFactory() throws DataSourceReadException {
        initializeIfNeeded();
        UnitFactory result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getUnitFactory();
            b = backend;
            break;
        }
        assert result != null : "no unit factory returned from "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Queries for primitive parameters.
     * 
     * @param keyIds the key id {@link String}s to query.
     * @param propIds the proposition id {@link String}s of the primitive
     * parameters of interest.
     * @param filters {@link Filter}s to constraint the query.
     * @param qs
     * @return an immutable {@link Map<String, List<PrimitiveParameter>>}.
     * @throws DataSourceReadException
     */
    public Map<String, List<PrimitiveParameter>> getPrimitiveParameters(
            Set<String> keyIds, Set<String> propIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return PRIMPARAM_QUERY.execute(this, keyIds, propIds, filters, qs);
    }

    /**
     * Queries for constants.
     *
     * @param keyIds the key id {@link String}s to query.
     * @param propIds the proposition id {@link String}s of the primitive
     * parameters of interest.
     * @param filters {@link Filter}s to constraint the query.
     * @param qs
     * @return an immutable {@link Map<String, List<Constant>>}.
     * 
     * @throws DataSourceReadException
     */
    public Map<String, List<Constant>> getConstantPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return CONST_QUERY.execute(this, keyIds, propIds, filters, qs);
    }

    /**
     * Queries for events.
     *
     * @param keyIds the key id {@link String}s to query.
     * @param propIds the proposition id {@link String}s of the primitive
     * parameters of interest.
     * @param filters {@link Filter}s to constraint the query.
     * @param qs
     * @return an immutable {@link Map<String, List<Event>>}.
     * @throws DataSourceReadException
     */
    public Map<String, List<Event>> getEvents(Set<String> keyIds,
            Set<String> propIds, Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return EVENTS_QUERY.execute(this, keyIds, propIds, filters, qs);
    }

    private static abstract class ProcessQuery<P> {

        ProcessQuery() {
        }

        Map<String, List<P>> execute(DataSource dataSource,
                Set<String> keyIds, Set<String> propIds,
                Filter filters,
                QuerySession qs)
                throws DataSourceReadException {
            Set<String> notNullKeyIds = handleKeyIdSetArgument(keyIds);
            Set<String> notNullPropIds = handlePropIdSetArgument(propIds);

            dataSource.initializeIfNeeded();
            List<DataSourceBackend> backends =
                    dataSource.backendManager.getBackends();
            List<Map<String, List<P>>> resultMaps = 
                    new ArrayList<Map<String, List<P>>>(backends.size());
            for (DataSourceBackend backend : backends) {
                resultMaps.add(executeBackend(backend,
                        notNullKeyIds,
                        notNullPropIds, filters, qs));

            }

            Map<String, List<P>> result =
                    new DataSourceResultMap<P>(resultMaps);

            return result;
        }

        protected abstract Map<String, List<P>> executeBackend(
                DataSourceBackend backend,
                Set<String> keyIds, Set<String> propIds,
                Filter filters,
                QuerySession qs)
                throws DataSourceReadException;
    }
    private static ProcessQuery<Event> EVENTS_QUERY =
            new ProcessQuery<Event>() {

                @Override
                protected Map<String, List<Event>> executeBackend(
                        DataSourceBackend backend, Set<String> keyIds,
                        Set<String> propIds, Filter filters, QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getEvents(keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<PrimitiveParameter> PRIMPARAM_QUERY =
            new ProcessQuery<PrimitiveParameter>() {

                @Override
                protected Map<String, List<PrimitiveParameter>> executeBackend(
                        DataSourceBackend backend,
                        Set<String> keyIds, Set<String> propIds, Filter filters,
                        QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getPrimitiveParameters(
                            keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<Constant> CONST_QUERY =
            new ProcessQuery<Constant>() {

                @Override
                protected Map<String, List<Constant>> executeBackend(
                        DataSourceBackend backend,
                        Set<String> keyIds, Set<String> propIds,
                        Filter filters,
                        QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getConstantPropositions(
                            keyIds, propIds, filters, qs);
                }
            };

    @Override
    public void backendUpdated(DataSourceBackendUpdatedEvent evt) {
        clear();
        fireDataSourceUpdated();
    }

    /**
     * Determines whether the data source's data element mappings are
     * consistent with the data elements' definitions.
     *
     * @param knowledgeSource a {@link KnowledgeSource}.
     * @throws DataSourceFailedValidationException if the data element
     * mappings are not consistent with the data elements' definitions.
     * @throws DataSourceValidationException if an error occurred during
     * validation.
     */
    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        try {
            initializeIfNeeded();
            for (DataSourceBackend backend : this.backendManager.getBackends()) {
                try {
                    backend.validate(knowledgeSource);
                } catch (DataSourceBackendFailedValidationException ex) {
                    throw new DataSourceFailedValidationException(
                            "Data source failed validation", ex);
                } catch (KnowledgeSourceReadException ex) {
                    throw new DataSourceValidationIncompleteException(
                            "An error occurred during validation", ex);
                }
            }
        } catch (DataSourceReadException ex) {
            throw new DataSourceValidationIncompleteException(
                    "An error occurred during validation", ex);
        }
    }

    @Override
    public void close() {
        clear();
        this.backendManager.close();
        super.close();
    }

    @Override
    public void clear() {
    }

    private void initializeIfNeeded() throws DataSourceReadException {
        if (isClosed()) {
            throw new IllegalStateException("Data source already closed!");
        }
        try {
            this.backendManager.initializeIfNeeded();
        } catch (BackendNewInstanceException iae) {
            throw new DataSourceReadException(iae);
        } catch (BackendInitializationException ex) {
            throw new DataSourceReadException(ex);
        }
    }

    /**
     * Notifies registered listeners that the data source has been updated.
     *
     * @see DataSourceUpdatedEvent
     * @see SourceListener
     */
    private void fireDataSourceUpdated() {
        fireSourceUpdated(new DataSourceUpdatedEvent(this));
    }

    private static Set<String> handleKeyIdSetArgument(Set<String> keyIds) {
        if (keyIds == null) {
            return new HashSet<String>();
        } else {
            return new HashSet<String>(keyIds);
        }
    }

    private static Set<String> handlePropIdSetArgument(Set<String> propIds) {
        if (propIds != null) {
            return new HashSet<String>(propIds);
        } else {
            throw new IllegalArgumentException("propIds cannot be null");
        }
    }
}
