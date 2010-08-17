package org.protempa;

import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.proposition.ConstantParameter;
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

    Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsAsc(Set<String> keyIds, 
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException;

    Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException;

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

}
