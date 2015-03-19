package org.protempa.backend.dsb.relationaldb;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import java.io.Serializable;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 *
 * @author Andrew Post
 */
class RelationalDatabaseSpecBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final EntitySpec[] EMPTY_ES_ARR = new EntitySpec[0];
    private static final StagingSpec[] EMPTY_SS_ARR = new StagingSpec[0];
    private EntitySpec[] primitiveParameterSpecs = EMPTY_ES_ARR;
    private EntitySpec[] eventSpecs = EMPTY_ES_ARR;
    private EntitySpec[] constantSpecs = EMPTY_ES_ARR;
    private StagingSpec[] stagedSpecs = EMPTY_SS_ARR;
    private UnitFactory units;
    private GranularityFactory granularities;

    /**
     * Gets the granularity factory from which the
     * {@link RelationalDatabaseDataSourceBackend} should get granularities.
     *
     * @return a {@link GranularityFactory}.
     */
    GranularityFactory getGranularities() {
        return granularities;
    }

    /**
     * Sets the granularity factory from which the
     * {@link RelationalDatabaseDataSourceBackend} should get granularities.
     *
     * @param granularities a {@link GranularityFactory}.
     */
    void setGranularities(GranularityFactory granularities) {
        this.granularities = granularities;
    }

    UnitFactory getUnits() {
        return units;
    }

    void setUnits(UnitFactory units) {
        this.units = units;
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
    void setConstantSpecs(EntitySpec[] constantParameterSpecs) {
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
    void setEventSpecs(EntitySpec[] eventSpecs) {
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
    void setPrimitiveParameterSpecs(EntitySpec[] primitiveParameterSpecs) {
        if (primitiveParameterSpecs == null) {
            this.primitiveParameterSpecs = EMPTY_ES_ARR;
        } else {
            this.primitiveParameterSpecs = primitiveParameterSpecs.clone();
        }
    }

    StagingSpec[] getStagedSpecs() {
        return stagedSpecs.clone();
    }

    void setStagedSpecs(StagingSpec[] stagedSpecs) {
        if (stagedSpecs == null) {
            this.stagedSpecs = EMPTY_SS_ARR;
        } else {
            this.stagedSpecs = stagedSpecs.clone();
        }
    }

    RelationalDatabaseSpec build() {
        return new RelationalDatabaseSpec(
                getPrimitiveParameterSpecs(),
                getEventSpecs(),
                getConstantSpecs(),
                getStagedSpecs(),
                getUnits(),
                getGranularities()
        );
    }
}
