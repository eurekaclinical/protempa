package org.protempa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.PropositionUtil;
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
public final class DataSource extends
		AbstractSource<DataSourceUpdatedEvent,
        DataSourceBackendUpdatedEvent>{
    
    private static final String ALWAYS_RESORT_SYSTEM_PROPERTY =
            "protempa.datasource.alwaysresort";

    private static final boolean ALWAYS_RESORT =
                Boolean.getBoolean(ALWAYS_RESORT_SYSTEM_PROPERTY);

    static {
        ProtempaUtil.logger().fine("Will the data source always resort? " +
                ALWAYS_RESORT);
    }
    
    private final BackendManager<DataSourceBackendUpdatedEvent,
            DataSource, DataSourceBackend> backendManager;

    public DataSource(DataSourceBackend[] backends) {
        super(backends);
		this.backendManager = new BackendManager<DataSourceBackendUpdatedEvent,
                DataSource, DataSourceBackend>(
				this, backends);

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
        for (DataSourceBackend backend: this.backendManager.getBackends()) {
            result = backend.getKeyTypeDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type display name found in " +
                b.getClass() + "!";
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
        for (DataSourceBackend backend: this.backendManager.getBackends()) {
            result = backend.getKeyTypePluralDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type plural display name found in " +
                b.getClass() + "!";
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
        assert result != null : "no granularity factory returned from " +
                b.getClass() + "!";
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
        assert result != null : "no unit factory returned from " +
                b.getClass() + "!";
        return result;
	}

	/**
	 * Returns a list of all key ids in this data set with no data
	 * prefetching.
     *
	 * @param start the first key id to retrieve, must be >= 0.
     * @param count the number of key ids to retrieve,
     * must be > 9.
	 * @return a newly-created {@link Iterator} of {@link String}s.
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
    public List<String> getAllKeyIds(int start, int count)
			throws DataSourceReadException {
        if (start < 0)
            throw new IllegalArgumentException("start must be >= 0");
        if (count < 1)
            throw new IllegalArgumentException("count must be >= 1");
        initializeIfNeeded();
        Set<String> result = new HashSet<String>();
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            List<String> keyIds = backend.getAllKeyIds(start, count);
            if (keyIds == null)
                assertOnNullReturnVal(backend, "getAllKeyIds");
            result.addAll(keyIds);
            if (result.size() == count)
                break;
            else
                count -= result.size();
        }
	return new ArrayList(result);
    }

	/**
	 * Returns all elements of the timeseries identified by the given key and
	 * parameter id in ascending order.
	 *
	 * @param keyId
	 *            a timeseries key.
	 * @param paramId
	 *            a parameter id <code>String</code>.
	 * @return a newly-created <code>List</code> of timeseries elements in
	 *         ascending order. An newly-created empty <code>List</code> is
	 *         returned if the given <code>keyId</code> is invalid.
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			String paramId) throws DataSourceReadException {
		return getPrimitiveParametersAsc(keyId, paramId, null, null);
	}

	/**
	 * Returns all elements of the timeseries identified by the given key and
	 * parameter ids in ascending order.
	 *
	 * @param keyId
	 *            a timeseries key.
	 * @param paramIds
	 *            a <code>Set</code> of parameter id <code>String</code>s.
	 * @return a newly-created <code>List</code> of timeseries elements in
	 *         ascending order. An newly-created empty <code>List</code> is
	 *         returned if the given <code>keyId</code> is invalid.
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			Set<String> paramIds) throws DataSourceReadException {
		return getPrimitiveParametersAsc(keyId, paramIds, null, null);
	}

	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			String paramId, Long minValidDate, Long maxValidDate)
			throws DataSourceReadException {
		return getPrimitiveParametersAsc(keyId, Collections.singleton(paramId),
                minValidDate, maxValidDate);
	}

    public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
            Set<String> paramIds) throws DataSourceReadException {
        return getPrimitiveParametersDesc(keyId, paramIds, null, null);
    }

	public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
			String paramId) throws DataSourceReadException {
		return getPrimitiveParametersDesc(keyId, paramId, null, null);
	}

	public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
			String propId, Long minValidDate, Long maxValidDate)
			throws DataSourceReadException {
		Set<String> ids = handlePropIdArgument(propId);
		return getPrimitiveParametersDesc(keyId, ids, minValidDate,
				maxValidDate);
	}

	/**
	 * Returns all elements of the timeseries identified by the given key and
	 * parameter id in ascending order.
	 *
	 * @param keyId
	 *            a timeseries key.
	 * @param eventId
	 *            a parameter id <code>String</code>.
	 * @return a newly-created <code>List</code> of timeseries elements in
	 *         ascending order. An newly-created empty <code>List</code> is
	 *         returned if the given <code>keyId</code> is invalid.
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<Event> getEventsAsc(String keyId, String eventId)
			throws DataSourceReadException {
		return getEventsAsc(keyId, eventId, null, null);
	}

	/**
	 * Returns all elements of the timeseries identified by the given key and
	 * parameter ids in ascending order.
	 *
	 * @param keyId
	 *            a timeseries key.
	 * @param eventIds
	 *            a <code>Set</code> of parameter id <code>String</code>s.
	 * @return a newly-created <code>List</code> of timeseries elements in
	 *         ascending order. An newly-created empty <code>List</code> is
	 *         returned if the given <code>keyId</code> is invalid.
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<Event> getEventsAsc(String keyId, Set<String> eventIds)
			throws DataSourceReadException {
		return getEventsAsc(keyId, eventIds, null, null);
	}

	public List<Event> getEventsDesc(String keyId, String propId,
			Long minValidDate, Long maxValidDate)
			throws DataSourceReadException {
		Set<String> eventIds = handlePropIdArgument(propId);
		return getEventsDesc(keyId, eventIds, minValidDate, maxValidDate);
	}

	public List<Event> getEventsDesc(String keyId, Set<String> eventIds)
			throws DataSourceReadException {
		return getEventsDesc(keyId, eventIds, null, null);
	}

	public List<Event> getEventsDesc(String keyId, String eventId)
			throws DataSourceReadException {
        Set<String> eventIds = handlePropIdArgument(eventId);
		return getEventsDesc(keyId, eventIds, null, null);
	}

	/**
	 * @param keyId
	 * @param paramIds
	 * @param minValidDate
	 * @param maxValidDate
	 * @return
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			Set<String> paramIds, Long minValidDate,
			Long maxValidDate) throws DataSourceReadException {
            handleKeyIdArgument(keyId);
            Set<String> needIds = handlePropIdSetArgument(paramIds);
            List<PrimitiveParameter> result =
                    new ArrayList<PrimitiveParameter>();
            initializeIfNeeded();
            List<DataSourceBackend> backends =
                    this.backendManager.getBackends();
            for (DataSourceBackend backend : backends) {
                List<PrimitiveParameter> ss = backend.getPrimitiveParametersAsc(
                                keyId, needIds, minValidDate, maxValidDate);
                if (ss == null)
                    assertOnNullReturnVal(backend, "getPrimitiveParametersAsc");
                result.addAll(ss);
            }
            if (ALWAYS_RESORT || backends.size() > 1) {
                Collections.sort(result,
                            PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
            }
            return result;
	}

    private static void assertOnNullReturnVal(Backend backend,
            String methodName) {
        String msg = "The " + backend.getClass().getName() + "'s " +
                methodName + " method returned null -- this should not happen";
        throw new AssertionError(msg);
    }

    public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
			Set<String> paramIds, Long minValidDate,
			Long maxValidDate) throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(paramIds);
        List<PrimitiveParameter> result = new ArrayList<PrimitiveParameter>();

        initializeIfNeeded();
        List<DataSourceBackend> backends = this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<PrimitiveParameter> ss = backend.getPrimitiveParametersDesc(
                            keyId, needIds, minValidDate, maxValidDate);
            if (ss == null)
                assertOnNullReturnVal(backend, "getPrimitiveParametersDesc");
            result.addAll(ss);
        }

        if (ALWAYS_RESORT || backends.size() > 1) {
            Collections.sort(result,
                    PropositionUtil.REVERSE_TEMPORAL_PROPOSITION_COMPARATOR);
        }

        return result;
    }

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersDescByParamId(
			String keyId, Set<String> paramIds, Long minValidDate,
			Long maxValidDate) throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(paramIds);
		Map<String, List<PrimitiveParameter>> result =
                new HashMap<String, List<PrimitiveParameter>>();

        initializeIfNeeded();

        List<DataSourceBackend> backends = this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<PrimitiveParameter> ss = backend.getPrimitiveParametersDesc(
                keyId, needIds, minValidDate, maxValidDate);
            if (ss == null)
                assertOnNullReturnVal(backend, "getPrimitiveParametersDesc");
            Map<String, List<PrimitiveParameter>> primParamMap =
                    PropositionUtil.createPropositionMap(ss);
            for (Map.Entry<String,List<PrimitiveParameter>> me :
                primParamMap.entrySet()) {
                String meKey = me.getKey();
                if (result.containsKey(meKey)) {
                    result.get(meKey).addAll(me.getValue());
                } else {
                    result.put(meKey, me.getValue());
                }
            }
        }

        if (ALWAYS_RESORT || backends.size() > 1) {
            for (List<PrimitiveParameter> params : result.values()) {
                Collections.sort(params,
                    PropositionUtil.REVERSE_TEMPORAL_PROPOSITION_COMPARATOR);
            }
        }

		return result;
	}

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAscByParamId(
			String keyId, Set<String> paramIds, Long minValidDate,
			Long maxValidDate) throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(paramIds);
		Map<String, List<PrimitiveParameter>> result =
                new HashMap<String, List<PrimitiveParameter>>();

        initializeIfNeeded();

        List<DataSourceBackend> backends = this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<PrimitiveParameter> ss = backend.getPrimitiveParametersAsc(
                keyId, needIds, minValidDate, maxValidDate);
            if (ss == null)
                assertOnNullReturnVal(backend, "getPrimitiveParametersAsc");
            Map<String, List<PrimitiveParameter>> primParamMap = PropositionUtil
                    .createPropositionMap(ss);
            for (Map.Entry<String,List<PrimitiveParameter>> me :
                primParamMap.entrySet()) {
                String meKey = me.getKey();
                if (result.containsKey(meKey)) {
                    result.get(meKey).addAll(me.getValue());
                } else {
                    result.put(meKey, me.getValue());
                }
            }
        }

        if (ALWAYS_RESORT || backends.size() > 1) {
            for (List<PrimitiveParameter> params : result.values()) {
                Collections.sort(params,
                        PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
            }
        }

		return result;
	}

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAscByParamId(
			String keyId, String propId)
			throws DataSourceReadException {
		Set<String> paramIds = handlePropIdArgument(propId);
		return getPrimitiveParametersAscByParamId(keyId, paramIds);

	}

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAscByParamId(
			String keyId, Set<String> paramIds)
			throws DataSourceReadException {
		return getPrimitiveParametersAscByParamId(keyId, paramIds, null, null);
	}

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersDescByParamId(
			String keyId, String propId)
			throws DataSourceReadException {
		Set<String> paramIds = handlePropIdArgument(propId);
		return getPrimitiveParametersDescByParamId(keyId, paramIds);

	}

	public Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersDescByParamId(
			String keyId, Set<String> paramIds)
			throws DataSourceReadException {
		return getPrimitiveParametersDescByParamId(keyId, paramIds, null, null);
	}

	public List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds)
			throws DataSourceReadException {
            handleKeyIdArgument(keyId);
            paramIds = handlePropIdSetArgument(paramIds);
            initializeIfNeeded();
            List<ConstantParameter> result = new ArrayList<ConstantParameter>();
            for (DataSourceBackend backend : this.backendManager.getBackends()) {
                List<ConstantParameter> l = 
                        backend.getConstantParameters(keyId, paramIds);
                if (l == null)
                    assertOnNullReturnVal(backend, "getConstantParameters");
                result.addAll(l);
            }
            return result;
        }

        public List getConstantParameters(String keyId, String propId)
                            throws DataSourceReadException {
            Set<String> paramIds = handlePropIdArgument(propId);
            return getConstantParameters(keyId, paramIds);
        }

        public List<Event> getEventsAsc(String keyId, String propId,
                            Long minValidDate, Long maxValidDate)
                            throws DataSourceReadException {
            handleKeyIdArgument(keyId);
            Set<String> propIds = handlePropIdArgument(propId);
            return getEventsAsc(keyId, propIds,
                        minValidDate, maxValidDate);
	}

	/**
	 * @param keyId
	 * @param eventIds
	 * @param minValidDate
	 * @param maxValidDate
	 * @return
	 * @throws TerminologyAdaptorInitializationException
	 *             if the terminology adaptor could not be initialized.
	 * @throws SchemaAdaptorInitializationException
	 *             if the schema adaptor could not be initialized.
	 * @throws DataSourceReadException
	 */
	public List<Event> getEventsAsc(String keyId, Set<String> eventIds,
			Long minValidDate, Long maxValidDate)
			throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(eventIds);
		List<Event> result = new ArrayList<Event>();

        initializeIfNeeded();
        List<DataSourceBackend> backends =
                this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<Event> e = backend.getEventsAsc(keyId, needIds,
                minValidDate, maxValidDate);
            if (e == null)
                assertOnNullReturnVal(backend, "getEventsAsc");
            result.addAll(e);
        }
        if (ALWAYS_RESORT || backends.size() > 1)
            Collections.sort(result,
                    PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);

		return result;
	}

    public List<Event> getEventsDesc(String keyId, Set<String> eventIds,
			Long minValidDate, Long maxValidDate)
			throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(eventIds);
		List<Event> result = new ArrayList<Event>();

        initializeIfNeeded();
        List<DataSourceBackend> backends =
                this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<Event> e = backend.getEventsDesc(keyId, needIds,
                minValidDate, maxValidDate);
            if (e == null)
                assertOnNullReturnVal(backend, "getEventsDesc");
            result.addAll(e);
        }
        if (ALWAYS_RESORT || backends.size() > 1)
            Collections.sort(result,
                    PropositionUtil.REVERSE_TEMPORAL_PROPOSITION_COMPARATOR);

		return result;
    }

    public Map<String, List<Event>> getEventsDescByEventId(String keyId,
		Set<String> propIds, Long minValidDate, Long maxValidDate)
		throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(propIds);
        Map<String, List<Event>> result = new HashMap<String, List<Event>>();

        initializeIfNeeded();
        List<Event> ss = new ArrayList<Event>();
        List<DataSourceBackend> backends = this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<Event> l = backend.getEventsDesc(keyId, needIds,
                    minValidDate, maxValidDate);
            if (l == null)
                assertOnNullReturnVal(backend, "getEventDesc");
            ss.addAll(l);
        }
        if (ALWAYS_RESORT || backends.size() > 1)
            Collections.sort(ss,
                PropositionUtil.REVERSE_TEMPORAL_PROPOSITION_COMPARATOR);
        Map<String, List<Event>> primParamMap = PropositionUtil
                .createPropositionMap(ss);
        result.putAll(primParamMap);

        return result;
    }

    public Map<String, List<Event>> getEventsAscByEventId(String keyId,
            Set<String> propIds, Long minValidDate, Long maxValidDate)
            throws DataSourceReadException {
        handleKeyIdArgument(keyId);
        Set<String> needIds = handlePropIdSetArgument(propIds);

	Map<String, List<Event>> result = new HashMap<String, List<Event>>();

        initializeIfNeeded();
        List<Event> ss = new ArrayList<Event>();
        List<DataSourceBackend> backends =
                this.backendManager.getBackends();
        for (DataSourceBackend backend : backends) {
            List<Event> l = backend.getEventsAsc(keyId, needIds,
                minValidDate, maxValidDate);
            if (l == null)
                assertOnNullReturnVal(backend, "getEventsAsc");
            ss.addAll(l);
        }
        if (ALWAYS_RESORT || backends.size() > 1)
            Collections.sort(ss,
                PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
        Map<String, List<Event>> primParamMap = PropositionUtil
                .createPropositionMap(ss);
        result.putAll(primParamMap);

	return result;
    }

	public Map<String, List<Event>> getEventsAscByEventId(String keyId,
			String propId) throws DataSourceReadException {
		Set<String> eventIds = handlePropIdArgument(propId);
		return getEventsAscByEventId(keyId, eventIds);

	}

	public Map<String, List<Event>> getEventsAscByEventId(String keyId,
			Set<String> propIds) throws DataSourceReadException {
		return getEventsAscByEventId(keyId, propIds, null, null);
	}

	public Map<String, List<Event>> getEventsDescByEventId(String keyId,
			String propId) throws DataSourceReadException {
		Set<String> eventIds = handlePropIdArgument(propId);
		return getEventsDescByEventId(keyId, eventIds);

	}

	public Map<String, List<Event>> getEventsDescByEventId(String keyId,
			Set<String> propIds) throws DataSourceReadException {
		return getEventsDescByEventId(keyId, propIds, null, null);
	}

    public void backendUpdated(DataSourceBackendUpdatedEvent evt) {
        clear();
		fireDataSourceUpdated();
    }

    @Override
    public void close() {
		clear();
		this.backendManager.close();
        super.close();
	}

	public void clear() {

	}

	private void initializeIfNeeded() throws DataSourceReadException {
        if (isClosed())
            throw new IllegalStateException("Data source already closed!");
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

    private static Set<String> handlePropIdSetArgument(Set<String> propIds) {
        if (propIds != null)
            return new HashSet<String>(propIds);
        else
            throw new IllegalArgumentException("propIds cannot be null");
    }

    private static Set<String> handlePropIdArgument(String propId) {
        if (propId != null)
            return Collections.singleton(propId);
        else
            throw new IllegalArgumentException("propId cannot be null");
    }

    private static void handleKeyIdArgument(String keyId) {
        if (keyId == null)
            throw new IllegalArgumentException("keyId cannot be null");
    }

}
