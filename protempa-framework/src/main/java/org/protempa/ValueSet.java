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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.OrderedValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * For specifying value sets.
 * 
 * @author Andrew Post
 */
public final class ValueSet {

    private static final ValueSetElement[] EMPTY_VALUE_SET_ELT_ARRAY =
            new ValueSetElement[0];

    /**
     * For specifying the values of a value set.
     */
    public static class ValueSetElement {

        private final Value value;
        private final String displayName;
        private final String abbrevDisplayName;

        /**
         * Instantiates a value of a value set.
         *
         * @param value the {@link Value}. Cannot be <code>null</code>.
         * @param displayName the value's display name {@link String}. If
         * <code>null</code> is specified, {@link #getDisplayName()} will
         * return the empty string.
         * @param abbrevDisplayName the value's abbreviated display name
         * {@link String}. If <code>null</code> is specified,
         * {@link #getAbbrevDisplayName()} will return the empty string.
         */
        public ValueSetElement(Value value, String displayName,
                String abbrevDisplayName) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            if (displayName == null) {
                displayName = "";
            } else {
                displayName = displayName.intern();
            }
            if (abbrevDisplayName == null) {
                abbrevDisplayName = "";
            } else {
                abbrevDisplayName = abbrevDisplayName.intern();
            }
            this.value = value;
            this.displayName = displayName;
            this.abbrevDisplayName = abbrevDisplayName;
        }

        /**
         * Returns the value's abbreviated display name. Guaranteed not
         * <code>null</code>.
         *
         * @return a {@link String}.
         */
        public String getAbbrevDisplayName() {
            return abbrevDisplayName;
        }

        /**
         * Returns the value's display name. Guaranteed not <code>null</code>.
         *
         * @return a {@link String}.
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the value. Guaranteed not <code>null</code>.
         * 
         * @return a {@link Value}.
         */
        public Value getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
    private final String id;
    private final ValueSetElement[] valueSetElements;
    private final Map<Value, ValueSetElement> values;
    private final Set<Value> valuesKeySet;
    private final OrderedValue lowerBound;
    private final OrderedValue upperBound;
    private final SourceId sourceId;

    public ValueSet(String id, OrderedValue lowerBound,
            OrderedValue upperBound, SourceId sourceId) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id.intern();
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.valueSetElements = EMPTY_VALUE_SET_ELT_ARRAY;
        this.values = new HashMap<Value, ValueSetElement>();
        this.valuesKeySet = this.values.keySet();
        if (sourceId == null) {
            this.sourceId = NotRecordedSourceId.getInstance();
        } else {
            this.sourceId = sourceId;
        }
    }

    /**
     * Instantiates a new value set with the specified values (value set
     * elements).
     *
     * @param valueSetElements a {@link ValueSetElement[]}. No duplicate
     * {@link ValueSetElement}s are allowed.
     */
    public ValueSet(String id,
            ValueSetElement[] valueSetElements, SourceId sourceId) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Logger logger = ProtempaUtil.logger();
        ProtempaUtil.checkArray(valueSetElements, "valueSetElements");

        this.id = id.intern();
        this.valueSetElements = valueSetElements.clone();

        this.values = new HashMap<Value, ValueSetElement>();
        for (ValueSetElement vse : this.valueSetElements) {
            if (this.values.containsKey(vse.value)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Ignoring duplicate value {0} for id {1}",
                            new Object[]{vse.value.getFormatted(), id});
                }
//                throw new IllegalArgumentException(
//                        "No duplicate values allowed: " + vse.value.getFormatted());
            } else {
                this.values.put(vse.value, vse);
            }
        }
        this.valuesKeySet = this.values.keySet();
        this.lowerBound = null;
        this.upperBound = null;
        if (sourceId == null) {
            this.sourceId = NotRecordedSourceId.getInstance();
        } else {
            this.sourceId = sourceId;
        }
    }

    /**
     * Returns the value set's unique identifier.
     *
     * @return a unique identifer {@link String}.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Returns the source of this value set.
     * 
     * @return a {@link SourceId}, guaranteed not <code>null</code>.
     */
    public SourceId getSourceId() {
        return this.sourceId;
    }

    /**
     * Gets the elements of this value set, if specified.
     *
     * @return a {@link ValueSetElement[]}. Guaranteed not null.
     */
    public ValueSetElement[] getValueSetElements() {
        return this.valueSetElements;
    }

    /**
     * Gets the lower bound of this value set, if specified.
     * 
     * @return a lower bound {@link OrderedValue}.
     */
    public OrderedValue getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Gets the upper bound of this value set, if specified.
     *
     * @return an upper bound {@link OrderedValue}.
     */
    public OrderedValue getUpperBound() {
        return this.upperBound;
    }

    /**
     * Returns whether the specified value is in the value set.
     *
     * @param value a {@link Value}. Cannot be <code>null</code>.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isInValueSet(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        boolean result = true;
        if (!this.valuesKeySet.isEmpty()) {
            result = this.valuesKeySet.contains(value);
        } else if (this.lowerBound != null || this.upperBound != null) {
            if (this.lowerBound != null
                    && !ValueComparator.GREATER_THAN_OR_EQUAL_TO.test(
                    value.compare(this.lowerBound))) {
                result = false;
            }
            if (this.upperBound != null
                    && !ValueComparator.LESS_THAN_OR_EQUAL_TO.test(
                    value.compare(this.upperBound))) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Returns a display name for the specified value, if one is defined.
     *
     * @param value a {@link Value}. Cannot be <code>null</code>.
     * @return a {@link String}.
     */
    public String displayName(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        ValueSetElement vse = this.values.get(value);
        if (vse != null) {
            return vse.displayName;
        } else {
            return "";
        }
    }

    /**
     * Returns an abbreviated display name for the specified value, if one is
     * defined.
     *
     * @param value a {@link Value}. Cannot be <code>null</code>.
     * @return a {@link String}.
     */
    public String abbrevDisplayName(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        ValueSetElement vse = this.values.get(value);
        if (vse != null) {
            return vse.abbrevDisplayName;
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
