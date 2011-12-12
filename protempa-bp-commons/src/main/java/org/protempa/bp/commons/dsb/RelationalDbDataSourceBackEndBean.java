/**
 * 
 */
package org.protempa.bp.commons.dsb;

import java.io.IOException;

import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.bp.commons.dsb.relationaldb.EntitySpec;
import org.protempa.bp.commons.dsb.relationaldb.RelationalDatabaseSpec;
import org.protempa.proposition.value.AbsoluteTimeGranularityFactory;
import org.protempa.proposition.value.AbsoluteTimeUnitFactory;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * SubClass of
 * 
 * @author mgrand {@link RelationalDbDataSourceBackend} whose behavior is
 *         extended by setting bean properties rather than by inheritance.
 */
public class RelationalDbDataSourceBackEndBean extends RelationalDbDataSourceBackend {
	private GranularityFactory granularityFactory = new AbsoluteTimeGranularityFactory();
	private UnitFactory unitFactory = new AbsoluteTimeUnitFactory();
	private String keyType;
	private String schemaName;
	private String keyIdTable;
	private String keyIdColumn;
	private String keyIdJoinKey;
	
	private EntitySpec[] myConstantSpecs = new EntitySpec[0];
	private EntitySpec[] myEventSpecs = new EntitySpec[0];
	private EntitySpec[] myPrimitiveParameterSpecs = new EntitySpec[0];

	/**
	 * Constructor.
	 */
	public RelationalDbDataSourceBackEndBean() {
		super();
	}

	/**
	 * @param relationalDatabaseSpec
	 */
	public RelationalDbDataSourceBackEndBean(RelationalDatabaseSpec relationalDatabaseSpec) {
		super(relationalDatabaseSpec);
	}

	/**
	 * @param relationalDatabaseSpec
	 * @param databaseAPI
	 */
	public RelationalDbDataSourceBackEndBean(RelationalDatabaseSpec relationalDatabaseSpec, DatabaseAPI databaseAPI) {
		super(relationalDatabaseSpec, databaseAPI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.backend.dsb.DataSourceBackend#getGranularityFactory()
	 */
	@Override
	public GranularityFactory getGranularityFactory() {
		return granularityFactory;
	}

	public void setGranularityFactory(GranularityFactory granularityFactory) {
		this.granularityFactory = granularityFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.backend.dsb.DataSourceBackend#getUnitFactory()
	 */
	@Override
	public UnitFactory getUnitFactory() {
		return unitFactory;
	}

	public void setUnitFactory(UnitFactory unitFactory) {
		this.unitFactory = unitFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.backend.dsb.DataSourceBackend#getKeyType()
	 */
	@Override
	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#getSchemaName()
	 */
	@Override
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#getKeyIdTable()
	 */
	@Override
	public String getKeyIdTable() {
		return keyIdTable;
	}

	public void setKeyIdTable(String keyIdTable) {
		this.keyIdTable = keyIdTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#getKeyIdColumn
	 * ()
	 */
	@Override
	public String getKeyIdColumn() {
		return keyIdColumn;
	}

	public void setKeyIdColumn(String keyIdColumn) {
		this.keyIdColumn = keyIdColumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#getKeyIdJoinKey
	 * ()
	 */
	@Override
	public String getKeyIdJoinKey() {
		return keyIdJoinKey;
	}

	public void setKeyIdJoinKey(String keyIdJoinKey) {
		this.keyIdJoinKey = keyIdJoinKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#constantSpecs()
	 */
	@Override
	protected EntitySpec[] constantSpecs() throws IOException {
		return myConstantSpecs;
	}

	public void setConstantSpecs(EntitySpec[] myConstantSpecs) {
		this.myConstantSpecs = myConstantSpecs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#eventSpecs()
	 */
	@Override
	protected EntitySpec[] eventSpecs() throws IOException {
		return myEventSpecs;
	}

	public void setEventSpecs(EntitySpec[] myEventSpecs) {
		this.myEventSpecs = myEventSpecs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#
	 * primitiveParameterSpecs()
	 */
	@Override
	protected EntitySpec[] primitiveParameterSpecs() throws IOException {
		return myPrimitiveParameterSpecs;
	}

	public void setPrimitiveParameterSpecs(EntitySpec[] myPrimitiveParameterSpecs) {
		this.myPrimitiveParameterSpecs = myPrimitiveParameterSpecs;
	}

}
