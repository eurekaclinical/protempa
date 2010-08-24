package org.protempa.bp.commons.dsb;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.sqlgen.SQLOrderBy;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * An API for ServiceLoader-style services that generate database- and driver-
 * specific SQL for the {@link RelationalDatabaseSchemaAdaptor}.
 *
 * The following database-specific SQL generators are provided:
 * <ul>
 * <li>{@link ConnectorJ5MySQL415Generator}
 * <li>{@link Ojdbc14Oracle10gSQLGenerator}
 * </ul>
 *
 * The user may specify additional SQL generators by implementing this interface
 * and registering the implementation using the Java ServiceLoader method. Note
 * that if generators have overlapping compatibility, the generator that
 * {@link RelationalDatabaseSchemaAdaptor} will use is not deterministic.
 * 
 * @author Andrew Post
 */
public interface SQLGenerator {

    Map<String, List<ConstantParameter>> readConstantParameters(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints)
            throws DataSourceReadException;

    Map<String, List<PrimitiveParameter>> readPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints, SQLOrderBy order)
            throws DataSourceReadException;

    Map<String, List<Event>> readEvents(Set<String> keyIds,
            Set<String> eventIds, Filter dataSourceConstraints,
            SQLOrderBy order) throws DataSourceReadException;

    public GranularityFactory getGranularities();

    public UnitFactory getUnits();
}
