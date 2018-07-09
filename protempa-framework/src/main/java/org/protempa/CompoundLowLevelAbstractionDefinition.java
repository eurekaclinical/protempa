/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueType;

/**
 * An abstraction for representing compound low-level abstractions. These are
 * essentially low-level abstractions composed of multiple data types and whose
 * values are computed based on combinations the values of the underlying
 * low-level abstractions in overlapping intervals.
 *
 * Note that this is different than a {@link LowLevelAbstractionDefinition}
 * abstracted from multiple primitive parameters. In that case, the low-level
 * abstraction takes on a value based on the computation of all of the primitive
 * parameter values by a single algorithm. In compound low-level abstractions,
 * the values of the underlying low-level abstractions are considered
 * individually, and a higher-level value is assigned to an interval based on
 * rules.
 *
 * A compound low-level abstraction will generate a superset of the intervals in
 * the abstracted-from low-level abstractions.
 *
 * Just as for low-level abstractions, value definitions must be created for
 * compound low-level abstractions. These tell the abstraction how to process
 * the values of the underlying low-level abstraction. There are two ways to
 * consider the values of the low-level abstractions in a particular interval,
 * provided by the enum {@link ValueDefinitionMatchOperator}: ANY or ALL.
 * Specifying ANY will make the compound abstraction apply the value under
 * consideration if any of the low-level abstraction values in that interval
 * match the rule for that value. ALL requires all of the low-level abstraction
 * rules to match.
 *
 * The rules look something like:
 *
 * <code>COMPOUND_LLA_VALUE1 = {LLA1.value=LLA1_VALUE1,LLA2.value=LLA2_VALUE1,...,LLAN.value=LLAN_VALUE1}</code>
 * <code>COMPOUND_LLA_VALUE2 = {LLA1.value=LLA1_VALUE2,LLA2.value=LLA2_VALUE2,...,LLAN.value=LLAN_VALUE2}</code>
 *
 * The ANY or ALL operator applies to all of the rules. It is not applied on a
 * per-rule basis.
 *
 * Order matters when specifying the compound abstractions possible values.
 * Values will be considered in the order they were defined. The last value will
 * be considered as a catch-all if none of the others match. It should typically
 * be the "normal" interpretation of the underlying low-level abstractions.
 *
 * See {@link org.protempa.test.ProtempaTest} in protempa-test-suite for
 * examples of compound low-level abstractions.
 */
public final class CompoundLowLevelAbstractionDefinition 
        extends AbstractAbstractionDefinition implements ParameterDefinition {

    private static final long serialVersionUID = -1285908778762502403L;
    private final Set<String> lowLevelIds;
    private GapFunction gapBtwValues;
    private ContextDefinition interpretationContext;
    private String contextId;
    private int skip;
    private transient LinkedHashMap<String, List<ClassificationMatrixValue>> classificationMatrix;
    private transient List<ValueClassification> valueClassifications;

    /*
     * Number of consecutive intervals with the same value required for this
     * abstraction to be asserted. Default is 1.
     */
    private int minimumNumberOfValues;
    
    /**
     * The different ways to consider the low-level abstraction value rules in a
     * given interval
     */
    public static enum ValueDefinitionMatchOperator {

        /**
         * Assert the value if any of the low-level abstractions have the
         * appropriate value
         */
        ANY,
        /**
         * Assert the value if all of the low-level abstractions have the
         * appropriate value
         */
        ALL
    }
    private ValueDefinitionMatchOperator valueDefinitionMatchOperator;
    private boolean concatenable;

    /**
     * Constructor for this class. Sets the proposition ID. Sets the minimum
     * number of values to match to 1.
     *
     * @param id the proposition ID, a {@link String}
     */
    public CompoundLowLevelAbstractionDefinition(String id) {
        super(id);
        this.lowLevelIds = new HashSet<>();
        this.minimumNumberOfValues = 1;
        this.skip = -1;
        this.valueDefinitionMatchOperator = ValueDefinitionMatchOperator.ANY;
        this.gapBtwValues = GapFunction.DEFAULT;
        this.concatenable = true;
        initInstance();
    }

    private void initInstance() {
        this.classificationMatrix = new LinkedHashMap<>();
        this.valueClassifications = new ArrayList<>();
    }

    /**
     * Gets the IDs of the low-level abstraction that this abstraction is
     * abstracted from. Identical to {@link #getAbstractedFrom()}.
     *
     * @return a {@link Set} of {@link String}s that are the proposition IDs of
     * the underlying low-level abstractions
     */
    public Set<String> getLowLevelAbstractionIds() {
        return Collections.unmodifiableSet(lowLevelIds);
    }

    public String getContextId() {
        return this.contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public void setGapFunctionBetweenValues(GapFunction gf) {
        if (gf == null) {
            this.gapBtwValues = GapFunction.DEFAULT;
        } else {
            this.gapBtwValues = gf;
        }
    }

    public GapFunction getGapFunctionBetweenValues() {
        return this.gapBtwValues;
    }

    /**
     * Gets the minimum number of consecutive intervals that must have the same
     * value before this abstraction is asserted.
     *
     * @return the minimum number of values, an <code>int</code>
     */
    public int getMinimumNumberOfValues() {
        return minimumNumberOfValues;
    }

    /**
     * Sets the minimum number of consecutive intervals that must have the same
     * value before this abstraction is asserted.
     *
     * @param minimumNumberOfValues the minimum number of values, an
     * <code>int</code> greater than 0.
     */
    public void setMinimumNumberOfValues(int minimumNumberOfValues) {
        if (minimumNumberOfValues < 1) {
            throw new IllegalArgumentException("minimumNumberOfValues must be > 0");
        }
        this.minimumNumberOfValues = minimumNumberOfValues;
    }

    /**
     * Gets the operator that is applied to each interval's low-level
     * abstraction values to determine the value to set for this abstraction.
     *
     * @return a {@link ValueDefinitionMatchOperator}. The default value is ANY.
     */
    public ValueDefinitionMatchOperator getValueDefinitionMatchOperator() {
        return valueDefinitionMatchOperator;
    }

    /**
     * Sets the operator that is applied to each interval's low-level
     * abstraction values to determine the value to set for this abstraction. If
     * <code>null</code>, the setting will revert to the default (ANY).
     *
     * @param valueDefinitionMatchOperator the match operator to set, a
     * {@link ValueDefinitionMatchOperator}
     */
    public void setValueDefinitionMatchOperator(
            ValueDefinitionMatchOperator valueDefinitionMatchOperator) {
        if (valueDefinitionMatchOperator == null) {
            this.valueDefinitionMatchOperator =
                    ValueDefinitionMatchOperator.ANY;
        } else {
            this.valueDefinitionMatchOperator = valueDefinitionMatchOperator;
        }
    }

    /**
     * Adds a value classification for the abstraction. If a classification of
     * the provided name doesn't exist, then it is created. The value definition
     * name must be one of the values applicable to the low-level abstraction
     * whose ID is also passed in.
     *
     * @param id the name of the value definition
     * @param lowLevelAbstractionId the name of the low-level abstraction to add
     * to the classification
     * @param lowLevelValueDefName the name of the low-level abstraction's value
     */
    public void addValueClassification(
            ValueClassification valueClassification) {
        if (valueClassification == null) {
            throw new IllegalArgumentException(
                    "valueClassification cannot be null");
        }
        if (!classificationMatrix.containsKey(valueClassification.value)) {
            classificationMatrix.put(valueClassification.value,
                    new ArrayList<ClassificationMatrixValue>());
        }
        lowLevelIds.add(valueClassification.lladId);
        classificationMatrix.get(valueClassification.value).add(
                new ClassificationMatrixValue(valueClassification.lladId,
                NominalValue.getInstance(valueClassification.lladValue)));
        this.valueClassifications.add(valueClassification);
        recalculateChildren();
    }

    LinkedHashMap<String, List<ClassificationMatrixValue>> getValueClassificationsInt() {
        return this.classificationMatrix;
    }

    public ValueClassification[] getValueClassifications() {
        return this.valueClassifications.toArray(
                new ValueClassification[this.valueClassifications.size()]);
    }

    @Override
    public Set<String> getAbstractedFrom() {
        return getLowLevelAbstractionIds();
    }

    /**
     * Sets whether this type of compound low-level abstraction is concatenable.
     *
     * @param concatenable <code>true</code> if concatenable, <code>false</code>
     * if not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }

    @Override
    public boolean isConcatenable() {
        return this.concatenable;
    }

    @Override
    public boolean isSolid() {
        return true;
    }
    
    public ContextDefinition getInterpretationContext() {
        return interpretationContext;
    }

    public void setInterpretationContext(ContextDefinition interpretationContext) {
        this.interpretationContext = interpretationContext;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.NOMINALVALUE;
    }

    @Override
    public void acceptChecked(
            PropositionDefinitionCheckedVisitor propositionVisitor)
            throws ProtempaException {
        if (propositionVisitor == null) {
            throw new IllegalArgumentException(
                    "propositionVisitor cannot be null");
        }
        propositionVisitor.visit(this);
    }

    @Override
    public void accept(PropositionDefinitionVisitor propositionVisitor) {
        if (propositionVisitor == null) {
            throw new IllegalArgumentException(
                    "propositionVisitor cannot be null");
        }
        propositionVisitor.visit(this);
    }

    @Override
    protected void recalculateChildren() {
        this.children = this.lowLevelIds.toArray(new String[this.lowLevelIds
                .size()]);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.valueClassifications.size());
        for (ValueClassification vc : this.valueClassifications) {
            s.writeObject(vc);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        if (this.minimumNumberOfValues < 1) {
            throw new InvalidObjectException(
                    "minimumNumberOfValues must be > 0");
        }

        initInstance();

        int numValueClassifications = s.readInt();
        for (int i = 0; i < numValueClassifications; i++) {
            ValueClassification vc = (ValueClassification) s.readObject();
            if (vc != null) {
                addValueClassification(vc);
            } else {
                throw new InvalidObjectException("null ValueClassification");
            }
        }
    }

    @Override
    public String toString() {
        ReflectionToStringBuilder builder =
                new ReflectionToStringBuilder(this);
        builder.setAppendTransients(true);
        return builder.toString();
    }
}
