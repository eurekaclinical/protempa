package org.protempa;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import java.util.ArrayList;
import java.util.Collections;
import org.protempa.dsb.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;


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
		SchemaAdaptorListener, TerminologyAdaptorListener, DataSourceBackend {

    private static final String RETRIEVAL_EXCEPTION_MSG =
        "Could not retrieve data";

    private final TerminologyAdaptor terminologyAdaptor;

    private final SchemaAdaptor schemaAdaptor;

    /**
	 * Creates new instance with a schema adaptor and no terminology adaptor.
	 *
	 * @param schemaAdaptor
	 *            a {@link SchemaAdaptor}. If <code>null</code>, all
	 *            of the <code>get*</code> methods will return no data.
	 */
    protected AbstractDataSourceBackend(SchemaAdaptor schemaAdaptor) {
        this(schemaAdaptor, null);
    }

	/**
	 * Creates new instance with a schema adaptor and terminology adaptor.
	 * 
	 * @param schemaAdaptor
	 *            a {@link SchemaAdaptor}. Cannot be <code>null</code>.
	 * @param terminologyAdaptor
	 *            a {@link TerminologyAdaptor}. May be <code>null</code>
	 *            if PROTEMPA's data model and the data source's schema are
	 *            identical.
	 */
    protected AbstractDataSourceBackend(SchemaAdaptor schemaAdaptor,
			TerminologyAdaptor terminologyAdaptor) {
        if (schemaAdaptor == null)
            throw new IllegalArgumentException("schemaAdaptor cannot be null");
        
        this.schemaAdaptor = schemaAdaptor;
        this.schemaAdaptor.addSchemaAdaptorListener(this);
        this.terminologyAdaptor = terminologyAdaptor;
        if (terminologyAdaptor != null)
            this.terminologyAdaptor.addTerminologyAdaptorUpdatedListener(this);
    }
	
    public final GranularityFactory getGranularityFactory() {
        return schemaAdaptor.getGranularityFactory();
    }

    public final UnitFactory getUnitFactory() {
        return schemaAdaptor.getUnitFactory();
    }

    public final Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAsc(Set<String> paramIds, 
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<String, List<PrimitiveParameter>> result;
        if (terminologyAdaptor != null) {
            result
                    = new HashMap<String, List<PrimitiveParameter>>();
            for (String paramId : paramIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(paramId);
                Map<String, List<PrimitiveParameter>> localResult =
                        schemaAdaptor.getPrimitiveParametersAsc(
                        localIds, dataSourceConstraints);
                // TODO another visitor delegates to the copying class.
                //localResult = terminologyAdaptor.localToStandardUnits(localResult);
                //copy results except with new id.
                result.putAll(localResult);
            }
        } else {
            result = schemaAdaptor.getPrimitiveParametersAsc(
                    paramIds, dataSourceConstraints);
        }

        return result;
    }

    public final Map<String, List<PrimitiveParameter>>
            getPrimitiveParametersAsc(Set<String> keyIds, Set<String> paramIds,
			DataSourceConstraint dataSourceConstraints)
			throws DataSourceReadException {
		Map<String, List<PrimitiveParameter>> result;
        if (terminologyAdaptor != null) {
            result
                    = new HashMap<String, List<PrimitiveParameter>>();
            for (String paramId : paramIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(paramId);
                Map<String, List<PrimitiveParameter>> localResult =
                        schemaAdaptor.getPrimitiveParametersAsc(keyIds,
                        localIds, dataSourceConstraints);
                //localResult = terminologyAdaptor.localToStandardUnits(localResult);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getPrimitiveParametersAsc(keyIds,
                    paramIds, dataSourceConstraints);
        }

        return result;
    }

    public final List<PrimitiveParameter> getPrimitiveParametersAsc(
            String keyId,Set<String> paramIds, 
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<PrimitiveParameter> result;
        if (terminologyAdaptor != null) {
            result = new ArrayList<PrimitiveParameter>();
            for (String paramId : paramIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(paramId);
                List<PrimitiveParameter> localResult =
                        schemaAdaptor.getPrimitiveParametersAsc(keyId,
                        localIds, dataSourceConstraints);
                //localResult = terminologyAdaptor.localToStandardUnits(localResult);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getPrimitiveParametersAsc(keyId,
                    paramIds, dataSourceConstraints);
        }

        return result;
    }

    public final List<PrimitiveParameter> getPrimitiveParametersDesc(
            String keyId, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
            List<PrimitiveParameter> result;
        if (terminologyAdaptor != null) {
            result = new ArrayList<PrimitiveParameter>();
            for (String paramId : paramIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(paramId);
                List<PrimitiveParameter> localResult =
                        schemaAdaptor.getPrimitiveParametersDesc(keyId,
                        localIds, dataSourceConstraints);
                //localResult = terminologyAdaptor.localToStandardUnits(localResult);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getPrimitiveParametersDesc(keyId,
                    paramIds, dataSourceConstraints);
        }

        return result;
    }

    public final List<String> getAllKeyIds(int start, int count,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
            return schemaAdaptor.getAllKeyIds(start, count,
                    dataSourceConstraints);
    }

    public final List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds) throws DataSourceReadException {
            List<ConstantParameter> result;
        return getConstantParameters(Collections.singleton(keyId), paramIds)
                .get(keyId);
    }

    public final Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds,
            Set<String> paramIds) throws DataSourceReadException {
        Map<String, List<ConstantParameter>> result;
        if (terminologyAdaptor != null) {
            result = new HashMap<String, List<ConstantParameter>>();
            for (String paramId : paramIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(paramId);
                Map<String, List<ConstantParameter>> localResult =
                        schemaAdaptor.getConstantParameters(keyIds,
                        localIds);
                //localResult = terminologyAdaptor.localToStandardUnits(localResult);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getConstantParameters(keyIds,
                    paramIds);
        }

        return result;
    }

    public final Map<String, List<Event>> getEventsAsc(Set<String> eventIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<String, List<Event>> result;
        if (terminologyAdaptor != null) {
            result = new HashMap<String, List<Event>>();
            for (String eventId : eventIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(eventId);
                Map<String, List<Event>> localResult =
                        schemaAdaptor.getEventsAsc(localIds,
                        dataSourceConstraints);
                //copy results except with new id.
                result.putAll(localResult);
            }
        } else {
            result = schemaAdaptor.getEventsAsc(eventIds,
                    dataSourceConstraints);
        }

        return result;
    }

    public final Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<String, List<Event>> result;
        if (terminologyAdaptor != null) {
            result
                    = new HashMap<String, List<Event>>();
            for (String eventId : eventIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(eventId);
                Map<String, List<Event>> localResult =
                        schemaAdaptor.getEventsAsc(keyIds, localIds,
                        dataSourceConstraints);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getEventsAsc(keyIds, eventIds,
                    dataSourceConstraints);
        }

        return result;
    }

    public final List<Event> getEventsAsc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<Event> result;
        if (terminologyAdaptor != null) {
            result = new ArrayList<Event>();
            for (String eventId : eventIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(eventId);
                List<Event> localResult =
                        schemaAdaptor.getEventsAsc(keyId,
                        localIds, dataSourceConstraints);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getEventsAsc(keyId,
                    eventIds, dataSourceConstraints);
        }

        return result;
    }

    public final List<Event> getEventsDesc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<Event> result;
        if (terminologyAdaptor != null) {
            result = new ArrayList<Event>();
            for (String eventId : eventIds) {
                Set<String> localIds =
                        terminologyAdaptor.standardToLocalTerms(eventId);
                List<Event> localResult =
                        schemaAdaptor.getEventsDesc(keyId,
                        localIds, dataSourceConstraints);
                //copy results except with new id.
                //merge with existing results
            }
        } else {
            result = schemaAdaptor.getEventsDesc(keyId,
                    eventIds, dataSourceConstraints);
        }

        return result;
    }

    public final void close() {
        this.schemaAdaptor.removeSchemaAdaptorListener(this);
        this.schemaAdaptor.close();
        if (this.terminologyAdaptor != null) {
            this.terminologyAdaptor
                            .removeTerminologyAdaptorUpdatedListener(this);
            this.terminologyAdaptor.close();
        }
    }
    
    public final void initialize(BackendInstanceSpec config)
        throws DataSourceBackendInitializationException {
		this.schemaAdaptor.initialize(config);
        if (this.terminologyAdaptor != null)
            this.terminologyAdaptor.initialize(config);
    }

    public final void schemaAdaptorUpdated(SchemaAdaptorUpdatedEvent event) {
        fireDataSourceBackendUpdated();
    }

    public final void terminologyAdaptorUpdated(
            TerminologyAdaptorUpdatedEvent event) {
        fireDataSourceBackendUpdated();
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

    public final String getKeyType() {
        return this.schemaAdaptor.getKeyType();
    }

    public final String getKeyTypeDisplayName() {
        return this.schemaAdaptor.getKeyTypeDisplayName();
    }

    public final String getKeyTypePluralDisplayName() {
        return this.schemaAdaptor.getKeyTypePluralDisplayName();
    }
}
