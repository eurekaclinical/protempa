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
package org.protempa.backend.dsb.relationaldb;

import java.io.Serializable;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Specifies mappings from propositions to relational database tables. Instances
 * of this class tell a {@link RelationalDatabaseDataSourceBackend} how to query
 * a relational database for propositions.
 * 
 * @author Andrew Post
 */
public final class RelationalDatabaseSpec implements Serializable {

    private static final long serialVersionUID = -7404642542962229266L;
    private static final EntitySpec[] EMPTY_ES_ARR = new EntitySpec[0];
    private static final StagingSpec[] EMPTY_SS_ARR = new StagingSpec[0];
    private EntitySpec[] primitiveParameterSpecs = EMPTY_ES_ARR;
    private EntitySpec[] eventSpecs = EMPTY_ES_ARR;
    private EntitySpec[] constantSpecs = EMPTY_ES_ARR;
    private StagingSpec[] stagedSpecs = EMPTY_SS_ARR;
    private final UnitFactory units;
    private final GranularityFactory granularities;

    /**
     * Instantiates this class with the specified mappings from primitive
     * parameters, events and constant parameters to relational database tables,
     * and unit and granularity types.
     * 
     * You can specify alternative versions of the same entity by creating
     * sequential entity specs with the same name, different column specs for
     * one or more properties or references, and mutually exclusive constraint
     * specs. One example of where this is useful is inpatient versus clinic
     * hospital encounters, for which dates might be stored differently.
     * 
     * @param primitiveParameterSpecs
     *            an {@link EntitySpec} array containing mappings from primitive
     *            parameters to relational database tables.
     * @param eventSpecs
     *            an {@link EntitySpec} array containing mappings from events to
     *            relational database tables.
     * @param constantSpecs
     *            an {@link EntitySpec} array containing mappings from constants to
     *            relational database tables.
     * @param stagedSpecs
     *            a {@link TableSpec} array containing the names of database tables
     *            to be staged before querying
     * @param units
     *            what {@link UnitFactory} from which to get units.
     * @param granularities
     *            what {@link GranularityFactory} from which to get
     *            granularities.
     */
    RelationalDatabaseSpec(EntitySpec[] primitiveParameterSpecs,
            EntitySpec[] eventSpecs, EntitySpec[] constantSpecs,
            StagingSpec[] stagedSpecs, UnitFactory units,
            GranularityFactory granularities) {
        setPrimitiveParameterSpecs(primitiveParameterSpecs);
        setConstantSpecs(constantSpecs);
        setEventSpecs(eventSpecs);
        setStagedSpecs(stagedSpecs);
        this.units = units;
        this.granularities = granularities;
    }

    /**
     * Gets the granularity factory from which the
     * {@link RelationalDatabaseDataSourceBackend} should get granularities.
     * 
     * @return a {@link GranularityFactory}.
     */
    GranularityFactory getGranularities() {
        return granularities;
    }

    UnitFactory getUnits() {
        return units;
    }

    EntitySpec[] getConstantSpecs() {
        return constantSpecs.clone();
    }

    /**
     * You can specify alternative versions of the same entity by creating
     * sequential entity specs with the same name, different column specs for
     * one or more properties or references, and mutually exclusive constraint
     * specs. One example of where this is useful is inpatient versus clinic
     * hospital encounters, for which dates might be stored differently.
     * 
     * @param constantParameterSpecs
     */
    private void setConstantSpecs(EntitySpec[] constantParameterSpecs) {
        if (constantParameterSpecs == null) {
            this.constantSpecs = EMPTY_ES_ARR;
        } else {
            this.constantSpecs = constantParameterSpecs.clone();
        }
    }

    EntitySpec[] getEventSpecs() {
        return eventSpecs.clone();
    }

    /**
     * You can specify alternative versions of the same entity by creating
     * sequential entity specs with the same name, different column specs for
     * one or more properties or references, and mutually exclusive constraint
     * specs. One example of where this is useful is inpatient versus clinic
     * hospital encounters, for which dates might be stored differently.
     * 
     * @param eventSpecs
     */
    private void setEventSpecs(EntitySpec[] eventSpecs) {
        if (eventSpecs == null) {
            this.eventSpecs = EMPTY_ES_ARR;
        } else {
            this.eventSpecs = eventSpecs.clone();
        }
    }

    EntitySpec[] getPrimitiveParameterSpecs() {
        return primitiveParameterSpecs.clone();
    }

    /**
     * You can specify alternative versions of the same entity by creating
     * sequential entity specs with the same name, different column specs for
     * one or more properties or references, and mutually exclusive constraint
     * specs. One example of where this is useful is inpatient versus clinic
     * hospital encounters, for which dates might be stored differently.
     * 
     * @param primitiveParameterSpecs
     */
    private void setPrimitiveParameterSpecs(EntitySpec[] primitiveParameterSpecs) {
        if (primitiveParameterSpecs == null) {
            this.primitiveParameterSpecs = EMPTY_ES_ARR;
        } else {
            this.primitiveParameterSpecs = primitiveParameterSpecs.clone();
        }
    }
    
    StagingSpec[] getStagedSpecs() {
        return stagedSpecs.clone();
    }
    
    private void setStagedSpecs(StagingSpec[] stagedSpecs) {
        if (stagedSpecs == null) {
            this.stagedSpecs = EMPTY_SS_ARR;
        } else {
            this.stagedSpecs = stagedSpecs.clone();
        }
    }
}
