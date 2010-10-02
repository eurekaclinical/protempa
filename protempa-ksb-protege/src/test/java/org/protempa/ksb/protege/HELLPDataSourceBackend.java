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
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.arp.javautil.sql.SQLExecutor.StatementPreparer;
import org.protempa.DataSourceBackendFailedValidationException;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.DataSourceReadException;
import org.protempa.DatabaseDataSourceType;
import org.protempa.KnowledgeSource;
import org.protempa.QuerySession;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsDataSourceBackend;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.RelativeHourGranularity;
import org.protempa.proposition.value.RelativeHourGranularityFactory;
import org.protempa.proposition.value.RelativeHourUnitFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.ValueFormat;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
@BackendInfo(displayName = "HELLP Data")
public class HELLPDataSourceBackend extends AbstractCommonsDataSourceBackend {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private String dbUrl;
    private String username;
    private String password;
    private String driverClass;
    private final RelativeHourGranularityFactory granularityFactory;
    private final RelativeHourUnitFactory unitFactory;
    private ConnectionSpec connectionSpecInstance;

    /**
     *
     */
    public HELLPDataSourceBackend() {
        this.granularityFactory = new RelativeHourGranularityFactory();
        this.unitFactory = new RelativeHourUnitFactory();
    }

    @BackendProperty
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUrl() {
        return this.dbUrl;
    }

    @BackendProperty
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @BackendProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return this.driverClass;
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        return this.granularityFactory;
    }

    @Override
    public UnitFactory getUnitFactory() {
        return this.unitFactory;
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws DataSourceBackendInitializationException {
        super.initialize(config);
        try {
            connectionSpecInstance =
                    DatabaseAPI.DRIVERMANAGER.newConnectionSpecInstance(
                    this.dbUrl, this.username, this.password);
        } catch (InvalidConnectionSpecArguments ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend", ex);
        }
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints, QuerySession qs)
            throws DataSourceReadException {
        boolean where = false;
        final Set<String> keyIdsL = keyIds;
        final Set<String> paramIdsL = paramIds;
        final Long minValidL = null;
        final Long maxValidL = null;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,paramId,value,valueType,hrsOffset FROM data WHERE paramId<>'ICD9' and paramId <>'ICD9DC'");
        if (keyIds != null && !keyIds.isEmpty()) {
            primParamAscStmt.append(" AND keyId IN (");
            where = true;
            primParamAscStmt.append(StringUtils.join(Collections.nCopies(keyIds.size(), '?'),
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
            primParamAscStmt.append(StringUtils.join(
                    Collections.nCopies(paramIds.size(),
                    '?'), '?'));
            primParamAscStmt.append(")");
        }
        if (minValidL != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValidL != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(" ORDER BY hrsOffset ASC");

        StatementPreparer stmtPreparer = new StatementPreparer() {

            @Override
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

            @Override
            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        PrimitiveParameter p = new PrimitiveParameter(
                                resultSet.getString(2));
                        p.setValue(ValueFormat.parse(
                                resultSet.getString(3), 
                                ValueType.valueOf(resultSet.getString(4))));
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
                                resultSet.getString(3), 
                                ValueType.valueOf(resultSet.getString(4))));
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
            SQLExecutor.executeSQL(this.connectionSpecInstance,
                    primParamAscStmt.toString(), stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
    }

    @Override
    public Map<String, List<Constant>> getConstantPropositions(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        return new HashMap<String, List<Constant>>(0);
    }

    @Override
    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> paramIds, Filter filters, QuerySession qs)
            throws DataSourceReadException {
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
        final Long minValidL = null;
        final Long maxValidL = null;
        StringBuilder primParamAscStmt = new StringBuilder(
                "SELECT keyId,value,hrsOffset FROM data ");
        if (keyIds != null && !keyIds.isEmpty()) {
            primParamAscStmt.append(" WHERE paramId = 'ICD9' AND keyId IN (");
            where = true;
            primParamAscStmt.append(StringUtils.join(Collections.nCopies(keyIds.size(), '?'),
                    ","));
            primParamAscStmt.append(')');
        }
        if (paramIds != null && !paramIds.isEmpty()) {
            if (where) {
                primParamAscStmt.append(" AND value in (");
            } else {
                primParamAscStmt.append(" WHERE paramId = 'ICD9' AND value IN (");
                where = true;
            }
            primParamAscStmt.append(StringUtils.join(
                    Collections.nCopies(paramIds.size(),
                    '?'), ','));
            primParamAscStmt.append(")");
        }
        if (minValidL != null) {
            primParamAscStmt.append(" AND hrsOffset >= ?");
        }
        if (maxValidL != null) {
            primParamAscStmt.append(" AND hrsOffset <= ?");
        }
        primParamAscStmt.append(" ORDER BY hrsOffset ASC");

        StatementPreparer stmtPreparer = new StatementPreparer() {

            @Override
            public void prepare(PreparedStatement stmt)
                    throws SQLException {
                int pos = 1;
                if (keyIdsL != null) {
                    for (Iterator<String> itr = keyIdsL.iterator(); itr.hasNext();) {
                        stmt.setString(pos++, itr.next());
                    }
                }
                if (paramIdsL != null) {
                    for (Iterator<String> itr = paramIdsL.iterator(); itr.hasNext();) {
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

            @Override
            public void process(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String keyId = resultSet.getString(1);
                    if (result.containsKey(keyId)) {
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        p.setDataSourceType(new DatabaseDataSourceType(getDataSourceBackendId()));
                        long l = RelativeHourGranularity.HOUR.lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(intervalFactory.getInstance(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        List<Event> lll = result.get(keyId);
                        lll.add(p);
                    } else {
                        List<Event> lll = new ArrayList<Event>();
                        Event p = new Event("ICD-9_"
                                + resultSet.getString(2));
                        p.setDataSourceType(new DatabaseDataSourceType(getDataSourceBackendId()));
                        long l = RelativeHourGranularity.HOUR.lengthInBaseUnit(resultSet.getInt(3));
                        p.setInterval(intervalFactory.getInstance(l,
                                RelativeHourGranularity.HOUR, l,
                                RelativeHourGranularity.HOUR));
                        lll.add(p);
                        result.put(keyId, lll);
                    }

                }
            }
        };
        try {
            SQLExecutor.executeSQL(this.connectionSpecInstance, primParamAscStmt.toString(),
                    stmtPreparer, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
        return result;
    }

    public boolean hasAttribute(String propId, String attributeId) {
        return attributeId.equals("ATTR_VALUE");
    }

    @Override
    public String getKeyType() {
        return "CASE";
    }

    @Override
    public String getKeyTypeDisplayName() {
        return "case";
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        return "cases";
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException {
    }
}
