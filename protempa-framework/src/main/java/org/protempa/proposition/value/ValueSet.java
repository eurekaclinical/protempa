package org.protempa.proposition.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.ProtempaUtil;

/**
 * For specifying value sets.
 * 
 * @author Andrew Post
 */
public final class ValueSet {

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
            }
            if (abbrevDisplayName == null) {
                abbrevDisplayName = "";
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

    public ValueSet(String id, OrderedValue lowerBound,
            OrderedValue upperBound) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.valueSetElements = new ValueSetElement[0];
        this.values = new HashMap<Value, ValueSetElement>();
        this.valuesKeySet = this.values.keySet();
    }

    /**
     * Instantiates a new value set with the specified values (value set
     * elements).
     *
     * @param valueSetElements a {@link ValueSetElement[]}. No duplicate
     * {@link ValueSetElement}s are allowed.
     */
    public ValueSet(String id, ValueSetElement[] valueSetElements) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        ProtempaUtil.checkArray(valueSetElements, "valueSetElements");

        this.id = id;
        this.valueSetElements = valueSetElements.clone();

        this.values = new HashMap<Value, ValueSetElement>();
        for (ValueSetElement vse : this.valueSetElements) {
            if (this.values.containsKey(vse.value)) {
                throw new IllegalArgumentException(
                        "No duplicate values allowed");
            } else {
                this.values.put(vse.value, vse);
            }
        }
        this.valuesKeySet = this.values.keySet();
        this.lowerBound = null;
        this.upperBound = null;
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
            if (this.lowerBound != null && 
                    !ValueComparator.GREATER_THAN_OR_EQUAL_TO.contains(
                    value.compare(this.lowerBound))) {
                result = false;
            }
            if (this.upperBound != null
                    && !ValueComparator.LESS_THAN_OR_EQUAL_TO.contains(
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
