package org.protempa;

import java.util.logging.Level;
import org.protempa.dsb.filter.Filter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
     * Returns an object for accessing the length units of returned data from
     * the schema adaptor for this data source.
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

    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return PRIMPARAM_ASC_QUERY.execute(this, keyIds, paramIds,
                filters, qs);
    }

    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return PRIMPARAM_DESC_QUERY.execute(this, keyIds, paramIds,
                filters, qs);
    }

    private static void assertOnNullReturnVal(Backend backend,
            String methodName) {
        String msg = "The " + backend.getClass().getName() + "'s "
                + methodName + " method returned null -- this should not happen";
        throw new AssertionError(msg);
    }

    public Map<String, List<Constant>> getConstantPropositions(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return CONST_QUERY.execute(this, keyIds, paramIds, filters, qs);
    }

    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return EVENTS_ASC_QUERY.execute(this, keyIds, eventIds, filters, qs);
    }

    public Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return EVENTS_DESC_QUERY.execute(this, keyIds, eventIds,
                filters, qs);
    }

    private static abstract class ProcessQuery<P> {

        private Comparator comparator;

        ProcessQuery(Comparator comparator) {
            this.comparator = comparator;
        }

        Map<String, List<P>> execute(DataSource dataSource,
                Set<String> keyIds, Set<String> propIds,
                Filter filters,
                QuerySession qs)
                throws DataSourceReadException {
            Set<String> notNullKeyIds = handleKeyIdSetArgument(keyIds);
            Set<String> notNullPropIds = handlePropIdSetArgument(propIds);
            Map<String, List<P>> result = new HashMap<String, List<P>>();

            dataSource.initializeIfNeeded();
            List<DataSourceBackend> backends =
                    dataSource.backendManager.getBackends();
            for (DataSourceBackend backend : backends) {
                Map<String, List<P>> events = executeBackend(backend,
                        notNullKeyIds,
                        notNullPropIds, filters, qs);
                if (events == null) {
                    events = new HashMap<String, List<P>>();
                }
                for (Map.Entry<String, List<P>> entry : events.entrySet()) {
                    if (result.containsKey(entry.getKey())) {
                        result.get(entry.getKey()).addAll(entry.getValue());
                    } else {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            if (this.comparator != null) {
                if (ALWAYS_RESORT || backends.size() > 1) {
                    for (Map.Entry<String, List<P>> entry : result.entrySet()) {
                        Collections.sort(entry.getValue(), this.comparator);
                    }
                }
            }

            return result;
        }

        protected abstract Map<String, List<P>> executeBackend(
                DataSourceBackend backend,
                Set<String> keyIds, Set<String> propIds,
                Filter filters,
                QuerySession qs)
                throws DataSourceReadException;
    }
    private static ProcessQuery<Event> EVENTS_ASC_QUERY =
            new ProcessQuery<Event>(ProtempaUtil.TEMP_PROP_COMP) {

                @Override
                protected Map<String, List<Event>> executeBackend(
                        DataSourceBackend backend, Set<String> keyIds,
                        Set<String> propIds, Filter filters, QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getEventsAsc(keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<Event> EVENTS_DESC_QUERY =
            new ProcessQuery<Event>(ProtempaUtil.REVERSE_TEMP_PROP_COMP) {

                @Override
                protected Map<String, List<Event>> executeBackend(
                        DataSourceBackend backend, Set<String> keyIds,
                        Set<String> propIds, Filter filters, QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getEventsDesc(keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<PrimitiveParameter> PRIMPARAM_ASC_QUERY =
            new ProcessQuery<PrimitiveParameter>(ProtempaUtil.TEMP_PROP_COMP) {

                @Override
                protected Map<String, List<PrimitiveParameter>> executeBackend(
                        DataSourceBackend backend,
                        Set<String> keyIds, Set<String> propIds, Filter filters,
                        QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getPrimitiveParametersAsc(
                            keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<PrimitiveParameter> PRIMPARAM_DESC_QUERY =
            new ProcessQuery<PrimitiveParameter>(
            ProtempaUtil.REVERSE_TEMP_PROP_COMP) {

                @Override
                protected Map<String, List<PrimitiveParameter>> executeBackend(
                        DataSourceBackend backend,
                        Set<String> keyIds, Set<String> propIds,
                        Filter filters,
                        QuerySession qs)
                        throws DataSourceReadException {
                    return backend.getPrimitiveParametersDesc(
                            keyIds, propIds, filters, qs);
                }
            };
    private static ProcessQuery<Constant> CONST_QUERY =
            new ProcessQuery<Constant>(null) {

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
