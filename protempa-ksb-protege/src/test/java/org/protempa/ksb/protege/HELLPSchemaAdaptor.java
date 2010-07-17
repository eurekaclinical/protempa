package org.protempa.ksb.protege;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.Event;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.RelativeHourGranularity;
import org.protempa.proposition.value.RelativeHourGranularityFactory;
import org.protempa.proposition.value.RelativeHourUnitFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.ValueFormat;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.arp.javautil.sql.SQLExecutor.StatementPreparer;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.SchemaAdaptorProperty;
import org.protempa.bp.commons.dsb.DriverManagerAbstractSchemaAdaptor;

/**
 * Schema adaptor for the HELLP database.
 * 
 * @author Andrew Post
 */
public class HELLPSchemaAdaptor extends DriverManagerAbstractSchemaAdaptor {
    
    private String dbUrl;

    private String username;
    
    private String password;

    private String driverClass;

    private final RelativeHourGranularityFactory granularityFactory;
	
    private final RelativeHourUnitFactory unitFactory;

    @SchemaAdaptorProperty
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public String getDbUrl() {
        return this.dbUrl;
    }

    @SchemaAdaptorProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @SchemaAdaptorProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @SchemaAdaptorProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public String getDriverClass() {
        return this.driverClass;
    }

	/**
	 * 
	 */
	public HELLPSchemaAdaptor() {
		this.granularityFactory = new RelativeHourGranularityFactory();
		this.unitFactory = new RelativeHourUnitFactory();
	}

	public GranularityFactory getGranularityFactory() {
		return this.granularityFactory;
	}
	
	public UnitFactory getUnitFactory() {
		return this.unitFactory;
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return getPrimitiveParameters(paramIds, minValid,
				maxValid, " ORDER BY hrsOffset ASC");
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return getPrimitiveParameters(paramIds, minValid,
				maxValid, " ORDER BY hrsOffset DESC");
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> keyIds, Set<String> paramIds,
			Long minValid, Long maxValid)
                        throws DataSourceReadException {
        boolean where = false;
        final Set<String> keyIdsL = keyIds;
        final Set<String> paramIdsL = paramIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,paramId,value,valueType,hrsOffset FROM data WHERE paramId<>'ICD9' and paramId <>'ICD9DC'");
        if (keyIds != null && !keyIds.isEmpty()) {
            primParamAscStmt.append(" AND keyId IN (");
            where = true;
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(Collections.nCopies(keyIds.size(), "?"),
                                    ","));
            primParamAscStmt.append(')');
        }
        if (paramIds != null && !paramIds.isEmpty()) {
            if (where) {
                primParamAscStmt.append(" AND paramId in (");
            } else {
                primParamAscStmt.append(" AND paramId IN (");
                where = true;
            }
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(
                                    Collections.nCopies(paramIds.size(),
                                            "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(" ORDER BY hrsOffset ASC");

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt)
                    throws SQLException {
                int pos = 1;
                if (keyIdsL != null) {
                    for (String keyId : keyIdsL) {
                        stmt.setString(pos++, keyId);
                    }
                }
                if (paramIdsL != null) {
                    for (String paramId : paramIdsL) {
                        stmt.setString(pos++, paramId);
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final Map<String, List<PrimitiveParameter>> result =
                new HashMap<String, List<PrimitiveParameter>>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        PrimitiveParameter p = new PrimitiveParameter(
                                resultSet.getString(2));
                        p.setValue(ValueFormat.parse(
                                resultSet.getString(3), resultSet
                                        .getString(4)));
                        p.setTimestamp(RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(5)));
                        p.setGranularity(RelativeHourGranularity.HOUR);
                        List<PrimitiveParameter> l = result.get(keyId);
                        l.add(p);
                    } else {
                        List<PrimitiveParameter> l =
                                new ArrayList<PrimitiveParameter>();
                        PrimitiveParameter p = new PrimitiveParameter(
                                resultSet.getString(2));
                        p.setValue(ValueFormat.parse(
                                resultSet.getString(3), resultSet
                                        .getString(4)));
                        p.setTimestamp(RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(5)));
                        p.setGranularity(RelativeHourGranularity.HOUR);
                        l.add(p);
                        result.put(keyId, l);
                    }

                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), 
                    primParamAscStmt.toString(), stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	private Map<String, List<PrimitiveParameter>> getPrimitiveParameters(
			Set<String> paramIds, Long minValid,
			Long maxValid, String orderBy)
                        throws DataSourceReadException {
        final Set<String> paramIdsL = paramIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,paramId,value,valueType,hrsOffset FROM data WHERE paramId<>'ICD9' and paramId <>'ICD9DC'");
        if (paramIds != null && !paramIds.isEmpty()) {

            primParamAscStmt.append(" AND paramId IN (");
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(
                                    Collections.nCopies(paramIds.size(),
                                            "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(orderBy);

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt) throws SQLException {
                int pos = 1;
                if (paramIdsL != null) {
                    for (String paramId : paramIdsL) {
                        stmt.setString(pos++, paramId);
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final Map<String, List<PrimitiveParameter>> result =
                new HashMap<String, List<PrimitiveParameter>>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        PrimitiveParameter p = new PrimitiveParameter(
                                resultSet.getString(2));
                        p.setValue(ValueFormat.parse(
                                resultSet.getString(3), resultSet
                                        .getString(4)));
                        p.setTimestamp(RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(5)));
                        p.setGranularity(RelativeHourGranularity.HOUR);
                        List<PrimitiveParameter> l = result.get(keyId);
                        l.add(p);
                    } else {
                        List<PrimitiveParameter> l =
                                new ArrayList<PrimitiveParameter>();
                        PrimitiveParameter p = new PrimitiveParameter(
                                resultSet.getString(2));
                        p.setValue(ValueFormat.parse(
                                resultSet.getString(3), resultSet
                                        .getString(4)));
                        p.setTimestamp(RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(5)));
                        p.setGranularity(RelativeHourGranularity.HOUR);
                        l.add(p);
                        result.put(keyId, l);
                    }

                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), 
                    primParamAscStmt.toString(), stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
        return getPrimitiveParameters(keyId, paramIds,
                minValid, maxValid, " ORDER BY hrsOffset ASC");
	}

	public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
        return getPrimitiveParameters(keyId, paramIds,
                minValid, maxValid, " ORDER BY hrsOffset DESC");
	}

	private List<PrimitiveParameter> getPrimitiveParameters(String keyId,
			Set<String> paramIds, Long minValid,
			Long maxValid, String orderBy)
                        throws DataSourceReadException {
        final String keyIdL = keyId;
        final Set<String> paramIdsL = paramIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT paramId,value,valueType,hrsOffset FROM data WHERE keyId = ? AND paramId<>'ICD9' and paramId <>'ICD9DC'");
        if (paramIds != null && !paramIds.isEmpty()) {

            primParamAscStmt.append(" AND paramId IN (");
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(
                                    Collections.nCopies(paramIds.size(),
                                            "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(orderBy);

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt) throws SQLException {
                int pos = 1;
                stmt.setString(pos++, keyIdL);
                if (paramIdsL != null) {
                    for (String paramId : paramIdsL) {
                        stmt.setString(pos++, paramId);
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final List<PrimitiveParameter> result =
                new ArrayList<PrimitiveParameter>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    PrimitiveParameter p = new PrimitiveParameter(resultSet
                            .getString(1));
                    p.setValue(ValueFormat.parse(resultSet.getString(2),
                            resultSet.getString(3)));
                    p.setTimestamp(RelativeHourGranularity.HOUR
                            .lengthInBaseUnit(resultSet.getInt(4)));
                    p.setGranularity(RelativeHourGranularity.HOUR);
                    result.add(p);
                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(),
                    primParamAscStmt.toString(), stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	private static final String allKeyIdsStmt =
            "SELECT DISTINCT keyId FROM data limit ";

	public List<String> getAllKeyIds(int start, int finish,
                DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
		final List<String> result = new ArrayList<String>();
		ResultProcessor resultProcessor = new ResultProcessor() {

			public void process(ResultSet resultSet)
                                throws SQLException {
				while (resultSet.next()) {
					result.add(resultSet.getString(1));
				}
			}

		};
        String stmt = allKeyIdsStmt + start + "," + finish;
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), stmt, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
		return result;
	}

	public List getConstantParameters(String keyId, Set<String> paramIds) {
		return new ArrayList(0);
	}

	public Map<String, List<Event>> getEventsAsc(
			Set<String> eventIds, Long minValid, Long maxValid) 
            throws DataSourceReadException {
        return getEvents(eventIds, minValid, maxValid,
                " ORDER BY hrsOffset ASC");

	}

	public List<Event> getEventsAsc(String keyId,
			Set<String> eventIds, Long minValid, Long maxValid) 
            throws DataSourceReadException {
        return getEvents(keyId, eventIds, minValid, maxValid,
                " ORDER BY hrsOffset ASC");
	}

	public List<Event> getEventsDesc(String keyId,
			Set<String> eventIds, Long minValid, Long maxValid) 
            throws DataSourceReadException {
        return getEvents(keyId, eventIds, minValid, maxValid,
                " ORDER BY hrsOffset DESC");
	}

	private List<Event> getEvents(String keyId,
			Set<String> paramIds, Long minValid, Long maxValid, String orderBy)
            throws DataSourceReadException {
        final String keyIdL = keyId;
        List<String> strippedParamIds = null;
        if (paramIds != null) {
            strippedParamIds = new ArrayList<String>(paramIds.size());
            for (String paramId : paramIds) {
                strippedParamIds.add(paramId.substring(6));
            }
        }
        final List<String> paramIdsL = strippedParamIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT value,hrsOffset FROM data WHERE keyId = ? AND paramId = 'ICD9'");
        if (strippedParamIds != null && !strippedParamIds.isEmpty()) {

            primParamAscStmt.append(" AND value IN (");
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(Collections.nCopies(strippedParamIds
                                    .size(), "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(orderBy);

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt)
                    throws SQLException {
                int pos = 1;
                stmt.setString(pos++, keyIdL);
                if (paramIdsL != null) {
                    for (Iterator<String> itr = paramIdsL.iterator(); itr
                            .hasNext();) {
                        stmt.setString(pos++, itr.next());
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final List<Event> result = new ArrayList<Event>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    Event p = new Event("ICD-9_" + resultSet.getString(1));
                    long l = RelativeHourGranularity.HOUR
                            .lengthInBaseUnit(resultSet.getInt(2));
                    p.setInterval(new PointInterval(l,
                            RelativeHourGranularity.HOUR, l,
                            RelativeHourGranularity.HOUR));
                    result.add(p);
                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), primParamAscStmt.toString(),
                    stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
        boolean where = false;
        final Set<String> keyIdsL = keyIds;
        List<String> strippedParamIds = null;
        if (paramIds != null) {
            strippedParamIds = new ArrayList<String>(paramIds.size());
            for (Iterator<String> itr = paramIds.iterator();
            itr.hasNext();) {
                strippedParamIds.add(itr.next().substring(6));
            }
        }
        final List<String> paramIdsL = strippedParamIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,value,hrsOffset FROM data ");
        if (keyIds != null && !keyIds.isEmpty()) {
            primParamAscStmt
                    .append(" WHERE paramId = 'ICD9' AND keyId IN (");
            where = true;
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(Collections.nCopies(keyIds.size(), "?"),
                                    ","));
            primParamAscStmt.append(')');
        }
        if (paramIds != null && !paramIds.isEmpty()) {
            if (where) {
                primParamAscStmt.append(" AND value in (");
            } else {
                primParamAscStmt
                        .append(" WHERE paramId = 'ICD9' AND value IN (");
                where = true;
            }
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(
                                    Collections.nCopies(paramIds.size(),
                                            "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(" ORDER BY hrsOffset ASC");

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt)
                    throws SQLException {
                int pos = 1;
                if (keyIdsL != null) {
                    for (Iterator<String> itr = keyIdsL.iterator(); itr
                            .hasNext();) {
                        stmt.setString(pos++, itr.next());
                    }
                }
                if (paramIdsL != null) {
                    for (Iterator<String> itr = paramIdsL.iterator(); itr
                            .hasNext();) {
                        stmt.setString(pos++, itr.next());
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final Map<String, List<Event>> result =
                new HashMap<String, List<Event>>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        long l = RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(new PointInterval(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        List<Event> lll = result.get(keyId);
                        lll.add(p);
                    } else {
                        List<Event> lll = new ArrayList<Event>();
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        long l = RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(new PointInterval(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        lll.add(p);
                        result.put(keyId, lll);
                    }

                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), primParamAscStmt.toString(),
                    stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	private Map<String, List<Event>> getEvents(
			Set<String> paramIds, Long minValid, Long maxValid, String orderBy)
            throws DataSourceReadException {
        List<String> strippedParamIds = null;
        if (paramIds != null) {
            strippedParamIds = new ArrayList<String>(paramIds.size());
            for (String paramId : paramIds) {
                strippedParamIds.add(paramId.substring(6));
            }
        }
        final List<String> paramIdsL = strippedParamIds;
        final Long minValidL = minValid;
        final Long maxValidL = maxValid;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,value,hrsOffset FROM data WHERE paramId='ICD9'");
        if (paramIds != null && !paramIds.isEmpty()) {
            primParamAscStmt.append(" AND value IN (");
            primParamAscStmt
                    .append(org.arp.javautil.collections.Collections
                            .join(Collections.nCopies(strippedParamIds
                                    .size(), "?"), ","));
            primParamAscStmt.append(")");
        }
        if (minValid != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValid != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(orderBy);

        StatementPreparer stmtPreparer = new StatementPreparer() {

            public void prepare(PreparedStatement stmt) throws SQLException {
                int pos = 1;
                if (paramIdsL != null) {
                    for (Iterator<String> itr = paramIdsL.iterator(); itr
                            .hasNext();) {
                        stmt.setString(pos++, itr.next());
                    }
                }
                if (minValidL != null) {
                    stmt.setLong(pos++, minValidL.longValue());
                }
                if (maxValidL != null) {
                    stmt.setLong(pos++, maxValidL.longValue());
                }
            }

        };

        final Map<String, List<Event>> result = new HashMap<String, List<Event>>();
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        long l = RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(new PointInterval(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        List<Event> lll = result.get(keyId);
                        lll.add(p);
                    } else {
                        List<Event> lll = new ArrayList<Event>();
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        long l = RelativeHourGranularity.HOUR
                                .lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(new PointInterval(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        lll.add(p);
                        result.put(keyId, lll);
                    }

                }
            }

        };
        try {
            SQLExecutor.executeSQL(getConnectionSpec(), primParamAscStmt.toString(),
                    stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
	}

	public boolean hasAttribute(String propId, String attributeId) {
		return attributeId.equals("ATTR_VALUE");
	}

	public String getKeyType() {
		return "CASE";
	}

	public String getKeyTypeDisplayName() {
		return "case";
	}

	public String getKeyTypePluralDisplayName() {
		return "cases";
	}


}
