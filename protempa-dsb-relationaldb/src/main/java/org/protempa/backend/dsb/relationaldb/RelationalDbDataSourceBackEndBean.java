/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 *
 */
package org.protempa.backend.dsb.relationaldb;

import java.io.IOException;

import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.proposition.value.AbsoluteTimeGranularityFactory;
import org.protempa.proposition.value.AbsoluteTimeUnitFactory;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * SubClass of
 *
 * @author mgrand {@link RelationalDbDataSourceBackend} whose behavior is
 * extended by setting bean properties rather than by inheritance.
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
    private StagingSpec[] myStagedSpecs = new StagingSpec[0];

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

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend#stagedSpecs()
     */
    @Override
    protected StagingSpec[] stagedSpecs() {
        return myStagedSpecs;
    }

    public void setStagedSpecs(StagingSpec[] myStagedSpecs) {
        this.myStagedSpecs = myStagedSpecs;
    }
}
