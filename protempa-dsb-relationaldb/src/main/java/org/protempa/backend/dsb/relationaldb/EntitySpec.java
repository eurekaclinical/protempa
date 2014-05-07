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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.ValueType;

/**
 * Defines a mapping from propositions to entities in a relational database.
 *
 * @author Andrew Post
 */
public final class EntitySpec implements Serializable {

    private static final long serialVersionUID = -1935588032831088001L;
    private static final PropertySpec[] EMPTY_PROPERTY_SPEC_ARRAY
            = new PropertySpec[0];
    private static final ReferenceSpec[] EMPTY_REFERENCE_SPEC_ARRAY
            = new ReferenceSpec[0];
    private static final ColumnSpec[] EMPTY_COLUMN_SPEC_ARRAY
            = new ColumnSpec[0];
    private final String name;
    private final String description;
    private final String[] propositionIds;
    private final boolean unique;
    private final ColumnSpec baseSpec;
    private final ColumnSpec[] uniqueIdSpecs;
    private final ColumnSpec startTimeOrTimestampSpec;
    private final ColumnSpec finishTimeSpec;
    private final PropertySpec[] propertySpecs;
    private final ReferenceSpec[] referenceSpecs;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ColumnSpec[] constraintSpecs;
    private final ValueType valueType;
    private final ColumnSpec valueSpec;
    private final Granularity granularity;
    private final JDBCPositionFormat positionParser;
    private final Unit partitionBy;

    /**
     * Creates an entity spec instance.
     *
     * @param name a {@link String}. Cannot be <code>null</code>.
     * @param description a {@link String}. The constructor replaces a
     * <code>null</code> argument with a {@link String} of length zero.
     * @param propositionIds the proposition id {@link String[]}s to which this
     * entity spec applies. Cannot contain <code>null</code> values. These
     * propositions must all have the same set of properties.
     * @param unique <code>true</code> if every row in the database table
     * specified by the <code>baseSpec</code> argument contains a unique
     * instance of this entity, <code>false</code> otherwise.
     * @param baseSpec a {@link ColumnSpec} representing the path through the
     * database from the key's main table to this entity's main table.
     * @param uniqueIdSpec a {@link ColumnSpec[]} representing the paths through
     * the database from this entity's main table to the tables and columns that
     * together form an unique identifier for this entity. The columns
     * comprising the unique identifier cannot have null values with one
     * exception: if the column is also used for the PROTEMPA keyId, then it can
     * have a null value (because records with a null keyId are discarded with
     * just a logged warning).
     * @param startTimeOrTimestampSpec a {@link ColumnSpec} representing the
     * path through the database from this entity's main table to the table and
     * column where the entity's start time (or timestamp, if no finish time is
     * defined) is located, or <code>null</code> if this entity has no start
     * time or timestamp.
     * @param finishTimeSpec a {@link ColumnSpec} representing the path through
     * the database from this entity's main table to the table and column where
     * the entity's finish time is located, or <code>null</code> if this entity
     * has no finish time.
     * @param propertySpecs a {@link PropertySpec[]} defining the entity's
     * properties. These properties should be the same as the corresponding
     * propositions' properties. Cannot contain <code>null</code> values.
     * @param codeToPropIdMap a one-to-one {@link Map<String,String>} from code
     * to proposition id. If <code>null</code> or a mapping for a code is not
     * defined, it is assumed that the code in the database is the same as the
     * proposition id.
     * @param codeSpec a {@link ColumnSpec} representing the path through the
     * database from this entity's main table to the table and column where the
     * entity's code is located, or <code>null</code> if this entity has no
     * code.
     * @param constraintSpecs zero or more {@link ColumnSpec[]} paths from this
     * instance's main table to another table and column whose value will
     * constrain which rows in the database are members of this entity. Cannot
     * contain <code>null</code> values.
     * @param valueType if this entity has a value, its {@link ValueType}.
     * @param granularity the granularity for interpreting this entity' start
     * and finish times.
     * @param positionParser a parser for dates/times/timestamps for this
     * entity's start and finish times. Cannot be <code>null</code>.
     * @param partitionBy a hint to the relational data source backend to
     * partition queries for this entity spec by the given {@link Unit}. For
     * example, if a time unit of MONTH is specified, the backend may only query
     * data one month at a time. In order for this to work, at least one
     * {@link org.protempa.dsb.filter.PositionFilter} must be specified that
     * defines both upper and lower bounds on the same side of a proposition's
     * intervals. If multiple position filters are specified, then one of these
     * will be used to partition queries (which one is undefined!). If
     * <code>null</code>, no partitioning will occur.
     */
    public EntitySpec(String name,
            String description,
            String[] propositionIds,
            boolean unique,
            ColumnSpec baseSpec,
            ColumnSpec[] uniqueIdSpecs,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            PropertySpec[] propertySpecs,
            ReferenceSpec[] referenceSpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ColumnSpec valueSpec,
            ValueType valueType,
            Granularity granularity,
            JDBCPositionFormat positionParser,
            Unit partitionBy) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;

        if (positionParser == null
                && (startTimeOrTimestampSpec != null || finishTimeSpec != null)) {
            throw new IllegalArgumentException(
                    "positionParser cannot be null for entities with a start time and/or finish time");
        }

        if (propositionIds != null) {
            this.propositionIds = propositionIds.clone();
            ProtempaUtil.internAll(this.propositionIds);
            ProtempaUtil.checkArrayForNullElement(this.propositionIds,
                    "propositionIds");
            if (this.propositionIds.length == 0) {
                throw new IllegalArgumentException("propositionIds must have at least one element");
            }
            if (this.propositionIds.length > 1 && codeSpec == null) {
                throw new IllegalArgumentException("if propositionIds has multiple proposition ids, there must be a codeSpec to differentiate between them");
            }
        } else {
            throw new IllegalArgumentException("propositionIds cannot be null");
        }

        if (baseSpec == null) {
            throw new IllegalArgumentException("baseSpec cannot be null");
        }
        this.baseSpec = baseSpec;

        if (description == null) {
            description = "";
        }
        this.description = description;

        this.unique = unique;

        ProtempaUtil.checkArray(uniqueIdSpecs, "uniqueIdSpecs");
        this.uniqueIdSpecs = uniqueIdSpecs;

        this.startTimeOrTimestampSpec = startTimeOrTimestampSpec;
        this.finishTimeSpec = finishTimeSpec;

        if (propertySpecs != null) {
            this.propertySpecs = propertySpecs.clone();
            ProtempaUtil.checkArrayForNullElement(this.propertySpecs,
                    "propertySpecs");
        } else {
            this.propertySpecs = EMPTY_PROPERTY_SPEC_ARRAY;
        }

        if (referenceSpecs != null) {
            this.referenceSpecs = referenceSpecs.clone();
            ProtempaUtil.checkArrayForNullElement(this.referenceSpecs,
                    "referenceSpecs");
            for (ReferenceSpec rs : this.referenceSpecs) {
                rs.setReferringEntitySpec(this);
            }
        } else {
            this.referenceSpecs = EMPTY_REFERENCE_SPEC_ARRAY;
        }

        if (codeToPropIdMap != null) {
            this.codeToPropIdMap = new HashMap<>(codeToPropIdMap);
        } else {
            this.codeToPropIdMap = Collections.emptyMap();
        }

        this.codeSpec = codeSpec;

        if (constraintSpecs != null) {
            this.constraintSpecs = new ColumnSpec[constraintSpecs.length];
            System.arraycopy(constraintSpecs, 0, this.constraintSpecs, 0,
                    constraintSpecs.length);
            ProtempaUtil.checkArrayForNullElement(this.constraintSpecs,
                    "constraintSpecs");
        } else {
            this.constraintSpecs = EMPTY_COLUMN_SPEC_ARRAY;
        }

        if (valueType != null && valueSpec == null) {
            throw new IllegalArgumentException(
                    "valueType must have a corresponding valueSpec");
        }
        if (valueType == null && valueSpec != null) {
            throw new IllegalArgumentException(
                    "valueSpec must have a corresponding valueType");
        }
        this.valueType = valueType;
        this.valueSpec = valueSpec;
        this.granularity = granularity;
        this.positionParser = positionParser;
        this.partitionBy = partitionBy;
    }

    /**
     * Gets this entity spec's name.
     *
     * @return a {@link String}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a textual description of this entity spec.
     *
     * @return a {@link String}. Cannot be <code>null</code>.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the proposition ids to which this entity spec applies.
     *
     * @return a {@link String[]} of proposition ids.
     */
    public String[] getPropositionIds() {
        return this.propositionIds.clone();
    }

    /**
     * Returns whether each row corresponds to its own instance of this entity.
     *
     * @return <code>true</code> if rows each correspond to their own instance
     * of this entity, <code>false</code> otherwise.
     */
    public boolean isUnique() {
        return this.unique;
    }

    /**
     * Gets the path through the database from the key's main table to this
     * entity's main table.
     *
     * @return a {@link ColumnSpec} representing this path.
     */
    public ColumnSpec getBaseSpec() {
        return this.baseSpec;
    }

    /**
     * Gets the paths through the database from this entity's main table to the
     * tables and columns that together form an unique identifier for this
     * entity.
     *
     * @return a {@link ColumnSpec[]} representing these paths.
     */
    public ColumnSpec[] getUniqueIdSpecs() {
        return this.uniqueIdSpecs.clone();
    }

    /**
     * Gets the path through the database from this entity's main table to the
     * table and column where the entity's start time (or timestamp, if no
     * finish time is defined) is located, or <code>null</code> if this entity
     * has no start time or timestamp.
     *
     * @return a {@link ColumnSpec} representing this path, or <code>null</code>
     * if this entity has no start time or timestamp.
     */
    public ColumnSpec getStartTimeSpec() {
        return this.startTimeOrTimestampSpec;
    }

    /**
     * Gets the path through the database from this entity's main table to the
     * table and column where the entity's finish time (if defined) is located.
     *
     * @return a {@link ColumnSpec} representing this path, or <code>null</code>
     * if this entity has no finish time.
     */
    public ColumnSpec getFinishTimeSpec() {
        return this.finishTimeSpec;
    }

    /**
     * The entity's properties.
     *
     * @return a {@link PropertySpec[]} of the entity's properties. Guaranteed
     * not <code>null</code>.
     */
    public PropertySpec[] getPropertySpecs() {
        return this.propertySpecs.clone();
    }

    /**
     * The entity's references to other entities.
     *
     * @return a {@link ReferenceSpec[]} of the entity's references to other
     * entities. Guaranteed not <code>null</code>.
     */
    public ReferenceSpec[] getReferenceSpecs() {
        return this.referenceSpecs.clone();
    }

    /**
     * Returns whether this entity spec has a reference to another entity spec.
     *
     * @param entitySpec another entity spec.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean hasReferenceTo(EntitySpec entitySpec) {
        if (entitySpec == null) {
            throw new IllegalArgumentException("entitySpec cannot be null");
        }

        boolean found = false;
        String entitySpecName = entitySpec.name;
        for (ReferenceSpec refSpec : this.referenceSpecs) {
            if (refSpec.getEntityName().equals(entitySpecName)) {
                found = true;
                continue;
            }
        }
        return found;
    }

    /**
     * Returns the reference specs that point to the given entity spec.
     *
     * @param entitySpec another entity spec.
     * @return a {@link ReferenceSpec[]}.
     */
    public ReferenceSpec[] referencesTo(EntitySpec entitySpec) {
        if (entitySpec == null) {
            throw new IllegalArgumentException("entitySpec cannot be null");
        }

        List<ReferenceSpec> result = new ArrayList<>();
        String entitySpecName = entitySpec.name;
        for (ReferenceSpec refSpec : this.referenceSpecs) {
            if (refSpec.getEntityName().equals(entitySpecName)) {
                result.add(refSpec);
            }
        }
        return result.toArray(new ReferenceSpec[result.size()]);
    }

    /**
     * Returns a one-to-one mapping from code to proposition id. If
     * <code>null</code> or a mapping for a code is not defined, it is assumed
     * that the code in the database is the same as the proposition id.
     *
     * @return a {@link Map<String,String>}. Guaranteed not <code>null</code>.
     */
    public Map<String, String> getCodeToPropIdMap() {
        Map<String, String> result
                = new HashMap<>(this.codeToPropIdMap);
        return result;
    }

    /**
     * Gets the path through the database from this entity's main table to the
     * table and column where the a code representing this entity is located.
     *
     * @return a {@link ColumnSpec}.
     */
    public ColumnSpec getCodeSpec() {
        return this.codeSpec;
    }

    /**
     * Returns zero or more {@link ColumnSpec[]} paths from this instance's main
     * table to another table and column whose value will constrain which rows
     * in the database are members of this entity. Cannot contain
     * <code>null</code> values.
     *
     * @return a {@link ColumnSpec[]}.
     */
    public ColumnSpec[] getConstraintSpecs() {
        return this.constraintSpecs.clone();
    }

    /**
     * Returns this entity's value.
     *
     * @return a {@link ValueType}, or <code>null</code> if this entity does not
     * have a value.
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * Gets the path through the database from this entity's main table to the
     * table and column where this entity's value is located.
     *
     * @return a {@link ColumnSpec}.
     */
    public ColumnSpec getValueSpec() {
        return this.valueSpec;
    }

    /**
     * Returns the granularity for interpreting this entity' start and finish
     * times.
     *
     * @return a {@link Granularity}.
     */
    public Granularity getGranularity() {
        return this.granularity;
    }

    /**
     * Returns a parser for dates/times/timestamps for this entity's start and
     * finish times in the database.
     *
     * @return a {@link PositionParser}. Cannot be <code>null</code>.
     */
    public JDBCPositionFormat getPositionParser() {
        return this.positionParser;
    }

    /**
     * Returns a hint to the relational data source backend to partition queries
     * for this entity spec by the given units. For example, if a time unit of
     * MONTH is specified, the backend may only query data one month at a time.
     * In order for this to work, at least one
     * {@link org.protempa.dsb.filter.PositionFilter} must be specified that
     * defines both upper and lower bounds on the same side of a proposition's
     * intervals. If multiple position filters are specified, then one of these
     * will be used to partition queries (which one is undefined!).
     *
     * @return a {@link Unit} to partition by. If <code>null</code>, no
     * partitioning will occur.
     */
    public Unit getPartitionBy() {
        return this.partitionBy;
    }

    /**
     * Returns the distinct tables specified in this entity spec, not including
     * references to other entity specs.
     *
     * @return an array of {@link TableSpec}s. Guaranteed not 
     * <code>null</code>.
     */
    public TableSpec[] getTableSpecs() {
        Set<TableSpec> results = new HashSet<>();
        addTo(results, this.baseSpec);
        addTo(results, this.codeSpec);
        addTo(results, this.constraintSpecs);
        addTo(results, this.finishTimeSpec);
        addTo(results, this.startTimeOrTimestampSpec);
        addTo(results, this.uniqueIdSpecs);
        addTo(results, this.valueSpec);
        for (PropertySpec propertySpec : this.propertySpecs) {
            addTo(results, propertySpec.getSpec());
        }
        return results.toArray(new TableSpec[results.size()]);
    }

    private void addTo(Set<TableSpec> tableSpecs, ColumnSpec... colSpecs) {
        for (ColumnSpec colSpec : colSpecs) {
            if (colSpec != null) {
                for (ColumnSpec cs : colSpec.asList()) {
                    tableSpecs.add(TableSpec.fromColumnSpec(cs));
                }
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.name).append("description", this.description).append("propositionIds", this.propositionIds).append("unique", this.unique).append("baseSpec", this.baseSpec).append("uniqueIdSpecs", this.uniqueIdSpecs).append("startTimeOrTimestampSpec", this.startTimeOrTimestampSpec).append("finishTimeSpec", this.finishTimeSpec).append("propertySpecs", this.propertySpecs).append("referenceSpecs", this.referenceSpecs).append("codeToPropIdMap", this.codeToPropIdMap).append("codeSpec", this.codeSpec).append("constraintSpecs", this.constraintSpecs).append("valueType", this.valueType).append("valueSpec", this.valueSpec).append("granularity", this.granularity).append("positionParser", this.positionParser).append("partitionBy", this.partitionBy).toString();
    }
}
