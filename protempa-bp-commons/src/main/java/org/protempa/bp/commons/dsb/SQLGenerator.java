package org.protempa.bp.commons.dsb;

import java.util.Set;

import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.sqlgen.ConnectorJ5MySQL415Generator;
import org.protempa.bp.commons.dsb.sqlgen.Ojdbc14Oracle10gSQLGenerator;
import org.protempa.bp.commons.dsb.sqlgen.ResultCache;
import org.protempa.bp.commons.dsb.sqlgen.SQLOrderBy;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Constant;
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

    ResultCache<Constant> readConstants(Set<String> keyIds,
            Set<String> paramIds, Filter dataSourceConstraints)
            throws DataSourceReadException;

    ResultCache<PrimitiveParameter> readPrimitiveParameters(Set<String> keyIds,
            Set<String> paramIds, Filter dataSourceConstraints, SQLOrderBy order)
            throws DataSourceReadException;

    ResultCache<Event> readEvents(Set<String> keyIds, Set<String> eventIds,
            Filter dataSourceConstraints, SQLOrderBy order)
            throws DataSourceReadException;

    public GranularityFactory getGranularities();

    public UnitFactory getUnits();
}
