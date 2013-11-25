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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.ProtempaUtil;

/**
 * A 1:N relationship with instances of another entity.
 * 
 * @author Andrew Post
 */
public class ReferenceSpec implements Serializable {
    private static final long serialVersionUID = -2223863541098753792L;

    public static enum Type {
        ONE,
        MANY
    }

    private final String referenceName;
    private final String entityName;
    private EntitySpec referringEntitySpec;
    private final ColumnSpec[] uniqueIdSpecs;
    private final Type type;

    /**
     * Instantiates a reference instance with the reference's name, the 
     * right-hand-side entity name and the paths to the tables and columns
     * that form the right-hand-side entity's unique identifier.
     *
     * @param referenceName the name {@link String of ther reference.
     * @param entityName the name {@link String} of the entity being
     * referenced.
     * @param uniqueIdSpecs the {@link ColumnSpec[]} paths through the database
     * from an entity's main table to the tables and columns that together form
     * an unique identifier of the entities being referenced.
     */
    public ReferenceSpec(String referenceName, String entityName,
            ColumnSpec[] uniqueIdSpecs, Type type) {
        if (referenceName == null)
            throw new IllegalArgumentException("referenceName cannot be null");
        if (entityName == null)
            throw new IllegalArgumentException("entityName cannot be null");
        if (uniqueIdSpecs == null)
            throw new IllegalArgumentException("uniqueIdSpecs cannot be null");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        this.uniqueIdSpecs = uniqueIdSpecs.clone();
        ProtempaUtil.checkArray(this.uniqueIdSpecs, "uniqueIdSpecs");
        this.referenceName = referenceName.intern();
        this.entityName = entityName;
        this.type = type;
    }

    EntitySpec getReferringEntitySpec() {
        return this.referringEntitySpec;
    }

    void setReferringEntitySpec(EntitySpec referringEntitySpec) {
        this.referringEntitySpec = referringEntitySpec;
    }

    /**
     * Gets the paths through the database from an entity's main table to
     * the tables and columns that together form an unique identifier of the
     * entities being referenced.
     *
     * @return a {@link ColumnSpec[]} representing those paths.
     */
    public ColumnSpec[] getUniqueIdSpecs() {
        return this.uniqueIdSpecs.clone();
    }

    /**
     * Returns the reference's name.
     *
     * @return a reference name {@link String}.
     */
    public String getReferenceName() {
        return this.referenceName;
    }

    /**
     * Returns the right-hand-side entity name.
     *
     * @return an entity name {@link String}.
     */
    public String getEntityName() {
        return this.entityName;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("referenceName", this.referenceName)
                .append("entityName", this.entityName)
                .append("uniqueIdSpecs", this.uniqueIdSpecs)
                .append("type", this.type)
                .toString();
    }
}
