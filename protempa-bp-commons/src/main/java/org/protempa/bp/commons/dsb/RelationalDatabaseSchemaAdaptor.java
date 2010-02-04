/*
 * $Id: SchemaAdaptor.java,v 1.10 2008/05/30 16:28:47 arp4m Exp $
 */
package org.protempa.bp.commons.dsb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.protempa.proposition.Event;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.ValueFactory;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.arp.javautil.sql.SQLExecutor.StatementPreparer;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.SchemaAdaptorProperty;
import org.protempa.proposition.ConstantParameter;

/**
 * Schema adaptor for the contrast reaction project.
 * 
 * @author Andrew Post
 */
public final class RelationalDatabaseSchemaAdaptor
        extends DriverManagerAbstractSchemaAdaptor {

    private String dbUrl;

    private String username;
    
    private String password;

    private GranularityFactory granularityFactory;

    private UnitFactory unitFactory;

    @SchemaAdaptorProperty
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @SchemaAdaptorProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @SchemaAdaptorProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getDbUrl() {
        return this.dbUrl;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    private final List<TableSpec> tables = new ArrayList<TableSpec>();
    private TableSpec keysTable;
    private final Map<String, PropositionSpec> primitiveParameters;
    private final Map<String, PropositionSpec> events;
    private final Map<String, PropositionSpec> constants;

    public RelationalDatabaseSchemaAdaptor(
            RelationalDatabaseSpec relationalDatabaseSpec) {
        this();
        if (relationalDatabaseSpec != null) {
            for (TableSpec table : relationalDatabaseSpec.getTableSpecs())
                this.tables.add(table);
            this.keysTable = relationalDatabaseSpec.getKeysTable();
            if (relationalDatabaseSpec.getPrimitiveParameterSpecs() != null)
                for (PropositionSpec primParamSpec :
                    relationalDatabaseSpec.getPrimitiveParameterSpecs())
                    this.primitiveParameters.put(primParamSpec.getCode(),
                            primParamSpec);
            if (relationalDatabaseSpec.getEventSpecs() != null)
                for (PropositionSpec eventSpec :
                    relationalDatabaseSpec.getEventSpecs())
                    this.events.put(eventSpec.getCode(), eventSpec);
            if (relationalDatabaseSpec.getConstantParameterSpecs() != null)
                for (PropositionSpec constantParamSpec :
                    relationalDatabaseSpec.getConstantParameterSpecs())
                    this.constants.put(constantParamSpec.getCode(),
                            constantParamSpec);
        }
        this.granularityFactory = relationalDatabaseSpec.getGranularities();
		this.unitFactory = relationalDatabaseSpec.getUnits();
    }

	public RelationalDatabaseSchemaAdaptor() {
        this.primitiveParameters = new HashMap<String, PropositionSpec>();
        this.events = new HashMap<String, PropositionSpec>();
        this.constants = new HashMap<String, PropositionSpec>();
	}

	public GranularityFactory getGranularityFactory() {
		return this.granularityFactory;
	}
	
	public UnitFactory getUnitFactory() {
		return this.unitFactory;
	}

	public List<String> getAllKeyIds(int start, int finish)
            throws DataSourceReadException {
		TableSpec t = this.keysTable;
		String allKeyIdsStmt = "SELECT DISTINCT " + t.getKey() + " FROM "
                + t.getName() + " LIMIT " + start + "," + finish;
		final List<String> result = new ArrayList<String>();
		ResultProcessor resultProcessor = new ResultProcessor() {

			public void process(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					result.add(resultSet.getString(1));
				}
			}

		};
        try {
            SQLExecutor.executeSQL(creator, allKeyIdsStmt, resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }
		return result;
	}

	private static class ConstantParameterResultProcessor implements
			ResultProcessor {

		private PropositionSpec constant;

		private List<ConstantParameter> results;

		/**
		 * @return the constant
		 */
		public PropositionSpec getConstant() {
			return constant;
		}

		/**
		 * @param constant
		 *            the constant to set
		 */
		public void setConstant(PropositionSpec constant) {
			this.constant = constant;
		}

		List<ConstantParameter> getResults() {
			return results;
		}

		void setResults(List<ConstantParameter> results) {
			this.results = results;
		}

		public void process(ResultSet resultSet) throws SQLException {
			if (resultSet.next()) {
				ValueFactory vf = this.constant.getTable().getValType();
                ConstantParameter cp = new ConstantParameter(constant.getCode());
                cp.setValue(vf.getInstance(resultSet.getString(1)));
				results.add(cp);
			}
		}

	}

	public List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds) throws DataSourceReadException {
		List<ConstantParameter> results = new ArrayList<ConstantParameter>();
		final String fKeyId = keyId;
		StatementPreparer constStmtPreparer = new StatementPreparer() {

			public void prepare(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, fKeyId);
			}

		};

		ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
		resultProcessor.setResults(results);

		for (String paramId : paramIds) {
			PropositionSpec c = this.constants.get(paramId);
			TableSpec t = c.getTable();
			String stmt = "SELECT " + t.getVal() + " FROM " + t.getName()
					+ " WHERE " + t.getKey() + "= ?";

			resultProcessor.setConstant(c);
            try {
                SQLExecutor.executeSQL(creator, stmt, constStmtPreparer,
                        resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }
		}

		return results;
	}

	private static class PrimitiveParameterResultProcessorAllKeyIds implements
			ResultProcessor {

		private PropositionSpec primitiveParameterSpec;

		private Map<String, List<PrimitiveParameter>> results;

		PropositionSpec getPrimitiveParameterSpec() {
			return primitiveParameterSpec;
		}

		void setPrimitiveParameterSpec(PropositionSpec paramId) {
			this.primitiveParameterSpec = paramId;
		}

		Map<String, List<PrimitiveParameter>> getResults() {
			return results;
		}

		void setResults(Map<String, List<PrimitiveParameter>> results) {
			this.results = results;
		}

		public void process(ResultSet resultSet) throws SQLException {
			while (resultSet.next()) {
				String keyId = resultSet.getString(1);
				ValueFactory vf = this.primitiveParameterSpec.getTable()
						.getValType();
				PrimitiveParameter p = new PrimitiveParameter(
						primitiveParameterSpec.getCode());
				try {
					p.setTimestamp(this.primitiveParameterSpec.getTable()
                            .getPositionParser().toLong(resultSet, 2));
				} catch (SQLException e) {
					DSBUtil.logger().log(Level.WARNING,
							"Could not parse timestamp. Ignoring data value.",
							e);
					continue;
				}
				p.setGranularity(primitiveParameterSpec.getTable().getGran());
				p.setValue(vf.getInstance(resultSet.getString(3)));
				if (results.containsKey(keyId)) {
					List<PrimitiveParameter> l = results.get(keyId);
					l.add(p);
				} else {
					List<PrimitiveParameter> l = new ArrayList<PrimitiveParameter>();
					l.add(p);
					results.put(keyId, l);
				}
			}
		}

	}

	private static class EventResultProcessorAllKeyIds implements
			ResultProcessor {

		private PropositionSpec eventSpec;

		private Map<String, List<Event>> results;

		PropositionSpec getEventSpec() {
			return eventSpec;
		}

		void setEventSpec(PropositionSpec paramId) {
			this.eventSpec = paramId;
		}

		Map<String, List<Event>> getResults() {
			return results;
		}

		void setResults(Map<String, List<Event>> results) {
			this.results = results;
		}

		public void process(ResultSet resultSet) throws SQLException {
			while (resultSet.next()) {
				String keyId = resultSet.getString(1);
				Event p = new Event(this.eventSpec.getCode());
				Granularity gran = this.eventSpec.getTable().getGran();
				try {
					long d = this.eventSpec.getTable()
                            .getPositionParser().toLong(resultSet, 2);
					p.setInterval(new PointInterval(d, gran, d, gran));
				} catch (SQLException e) {
					DSBUtil.logger().log(Level.WARNING,
							"Could not parse timestamp. Ignoring data value.",
							e);
					continue;
				}
				if (results.containsKey(keyId)) {
					List<Event> l = results.get(keyId);
					l.add(p);
				} else {
					List<Event> l = new ArrayList<Event>();
					l.add(p);
					results.put(keyId, l);
				}
			}
		}

	}

	private static class MyStatementPreparer implements StatementPreparer {
		private Set<String> keyIds;
		private Set<String> paramIds;
		private Long minValid;
		private Long maxValid;
		private int pos;
		private Map<String, PropositionSpec> primitiveParameters;

		MyStatementPreparer(Set<String> keyIds, Set<String> paramIds,
				Long minValid, Long maxValid, int pos,
				Map<String, PropositionSpec> primitiveParameters) {
			this.keyIds = keyIds;
			this.paramIds = paramIds;
			this.minValid = minValid;
			this.maxValid = maxValid;
			this.pos = pos;
			this.primitiveParameters = primitiveParameters;
		}

		public void prepare(PreparedStatement stmt) throws SQLException {
			if (this.keyIds != null) {
				for (String keyId : this.keyIds) {
					stmt.setString(this.pos++, keyId);
				}
			}
			if (this.paramIds != null) {
				for (String paramId : this.paramIds) {
					PropositionSpec spec = this.primitiveParameters
							.get(paramId);
					if (spec.getTable().getCode() != null) {
						stmt.setString(this.pos++, paramId);
					}
				}
			}
			if (this.minValid != null) {
				stmt.setLong(this.pos++, this.minValid);
			}

			if (this.maxValid != null) {
				stmt.setLong(this.pos, this.maxValid);
			}
		}

	}

	private Map<String, List<PrimitiveParameter>> readPrimitiveParameters(
			Set<String> keyIds, Set<String> paramIds, Long minValid,
			Long maxValid, String order) throws DataSourceReadException {
		Map<String, List<PrimitiveParameter>> results =
                new HashMap<String, List<PrimitiveParameter>>();

		PrimitiveParameterResultProcessorAllKeyIds resultProcessor =
                new PrimitiveParameterResultProcessorAllKeyIds();
		resultProcessor.setResults(results);

		for (Iterator<String> itr = paramIds.iterator(); itr.hasNext();) {
			String paramId = itr.next();
			StatementPreparer stmtPreparer = new MyStatementPreparer(keyIds,
					Collections.singleton(paramId), minValid, maxValid, 1,
					this.primitiveParameters);
			PropositionSpec spec = this.primitiveParameters.get(paramId);
			TableSpec t = spec.getTable();
			String stmt = "SELECT " + t.getKey() + "," + t.getPosition() + ","
					+ t.getVal() + " FROM " + t.getName();
			StringBuilder stmtBuf = new StringBuilder(stmt);
			addToStatement(keyIds, Collections.singleton(paramId), minValid,
					maxValid, spec, stmtBuf);
			stmtBuf.append(order);
			resultProcessor.setPrimitiveParameterSpec(spec);
            try {
                SQLExecutor.executeSQL(creator, stmtBuf.toString(),
                        stmtPreparer, resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }
		}
		return results;
	}

	private Map<String, List<Event>> readEvents(Set<String> keyIds,
			Set<String> paramIds, Long minValid, Long maxValid, String order)
            throws DataSourceReadException
			{
		Map<String, List<Event>> results = new HashMap<String, List<Event>>();

		EventResultProcessorAllKeyIds resultProcessor
                = new EventResultProcessorAllKeyIds();
		resultProcessor.setResults(results);

		Set<String> paramIdsS = new HashSet<String>(paramIds.size());
		for (String paramId : paramIds) {
			paramIdsS.add(paramId);
		}

		StatementPreparer stmtPreparer = new MyStatementPreparer(keyIds,
				paramIdsS, minValid, maxValid, 1, this.events);

		for (Iterator<String> itr = paramIdsS.iterator(); itr.hasNext();) {
			String paramId = itr.next();
			PropositionSpec spec = this.events.get(paramId);
			if (spec != null) {
				TableSpec t = spec.getTable();
				String stmt = "SELECT " + t.getKey() + "," + t.getPosition()
						+ " FROM " + t.getName();
				StringBuilder stmtBuf = new StringBuilder(stmt);
				addToStatement(keyIds, Collections.singleton(paramId),
						minValid, maxValid, spec, stmtBuf);
				stmtBuf.append(order);
				resultProcessor.setEventSpec(spec);
                try {
                    SQLExecutor.executeSQL(creator, stmtBuf.toString(),
                            stmtPreparer, resultProcessor);
                } catch (SQLException ex) {
                    throw new DataSourceReadException(ex);
                }
			}
		}
		return results;
	}

	private void addToStatement(Set<String> keyIds, Set<String> paramIds,
			Long minValid, Long maxValid, PropositionSpec primParamSpec,
			StringBuilder stmtBuf) {
		boolean where = false;
		if (keyIds != null && !keyIds.isEmpty()) {
			stmtBuf.append(" WHERE " + primParamSpec.getTable().getKey()
					+ " in (");
			where = true;
			stmtBuf.append(org.arp.javautil.collections.Collections.join(
					Collections.nCopies(keyIds.size(), '?'), ","));
			stmtBuf.append(')');
		}
		String code = primParamSpec.getTable().getCode();
		if (code != null && paramIds != null && !paramIds.isEmpty()) {
			if (where) {
				stmtBuf.append(" AND ");
			} else {
				stmtBuf.append("WHERE ");
				where = true;
			}
			stmtBuf.append(code + " in (");
			stmtBuf.append(org.arp.javautil.collections.Collections.join(
					Collections.nCopies(paramIds.size(), '?'), ","));
			stmtBuf.append(")");
		}
		if (minValid != null) {
			if (where) {
				stmtBuf.append(" AND ");
			} else {
				stmtBuf.append("WHERE ");
				where = true;
			}
			stmtBuf.append(primParamSpec.getTable().getPosition());
			stmtBuf.append(" >= ?");
		}
		if (maxValid != null) {
			if (!where) {
				stmtBuf.append("WHERE ");
				where = true;
			} else {
				stmtBuf.append(" AND ");
			}
			stmtBuf.append(primParamSpec.getTable().getPosition());
			stmtBuf.append(" <= ?");
		}
		stmtBuf.append(" ORDER BY ");
		stmtBuf.append(primParamSpec.getTable().getPosition());
	}

	public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return readPrimitiveParameters(Collections.singleton(keyId), paramIds,
				minValid, maxValid, " ASC").get(keyId);
	}

	public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return readPrimitiveParameters(Collections.singleton(keyId), paramIds,
				minValid, maxValid, " DESC").get(keyId);
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> paramIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return readPrimitiveParameters(null, paramIds, minValid, maxValid,
				" ASC");
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> keyIds, Set<String> paramIds,
			Long minValid, Long maxValid) throws DataSourceReadException {
		return readPrimitiveParameters(keyIds, paramIds, minValid, maxValid,
				" ASC");
	}

	public Map<String, List<Event>> getEventsAsc(
			Set<String> eventIds, Long minValid, Long maxValid)
			throws DataSourceReadException {
		return readEvents(null, eventIds, minValid, maxValid, " ASC");
	}

	public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
			Set<String> eventIds, Long minValid,
			Long maxValid) throws DataSourceReadException {
		return readEvents(keyIds, eventIds, minValid, maxValid, " ASC");
	}

	public List<Event> getEventsAsc(String keyId,
			Set<String> eventIds, Long minValid, Long maxValid)
			throws DataSourceReadException {
		return readEvents(Collections.singleton(keyId), eventIds, minValid,
				maxValid, " ASC").get(keyId);
	}

	public List<Event> getEventsDesc(String keyId,
			Set<String> eventIds, Long minValid, Long maxValid) 
            throws DataSourceReadException {
		return readEvents(Collections.singleton(keyId), eventIds, minValid,
				maxValid, " DESC").get(keyId);
	}

	public String getKeyType() {
		return "PATIENT";
	}

	public String getKeyTypeDisplayName() {
		return "patient";
	}

	public String getKeyTypePluralDisplayName() {
		return "patients";
	}

}
