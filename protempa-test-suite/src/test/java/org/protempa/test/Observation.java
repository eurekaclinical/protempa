/*
 * #%L
 * Protempa Test Suite
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
package org.protempa.test;

import java.util.Date;

/**
 * Abstract class used as the super-type for various types of observations
 * related to an encounter.
 *
 * @author hrathod
 *
 */
public abstract class Observation extends Record {

    /**
     * The unique identifier for the observation.
     */
    private String id;
    /**
     * The unique identifier for the encounter to which this observation is
     * associated.
     */
    private Long encounterId;
    /**
     * The date of the observation.
     */
    private Date timestamp;
    /**
     * The unique identifier for the entity associated with this observation.
     */
    private String entityId;

    /**
     * Get the unique identifier for the observation.
     *
     * @return The unique identifier for the observation.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the unique identifier for the observation.
     *
     * @param inId The unique identifier for the observation.
     */
    public void setId(String inId) {
        this.id = inId;
    }

    /**
     * Get the unique identifier for the encounter with which the observation is
     * associated.
     *
     * @return The unique identifier for the encounter.
     */
    public Long getEncounterId() {
        return this.encounterId;
    }

    /**
     * Set the unique identifier for the encounter with which the observation is
     * associated.
     *
     * @param inEncounterId The unique identifier for the encounter.
     */
    public void setEncounterId(Long inEncounterId) {
        this.encounterId = inEncounterId;
    }

    /**
     * Get the date of the observation.
     *
     * @return The date of the observation.
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * Set the date of the observation.
     *
     * @param inTimestamp The date of the observation.
     */
    public void setTimestamp(Date inTimestamp) {
        this.timestamp = inTimestamp;
    }

    /**
     * Get the unique identifier for the entity associated with the observation.
     *
     * @return The unique identifier for the entity.
     */
    public String getEntityId() {
        return this.entityId;
    }

    /**
     * Set the unique identifier for the entity associated with the observation.
     *
     * @param inEntityId The unique identifier for the entity.
     */
    public void setEntityId(String inEntityId) {
        this.entityId = inEntityId;
    }
}
