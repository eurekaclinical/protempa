package org.protempa.dsb.filter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ListValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * For specifying constraints on the value of a property of a proposition.
 * 
 * @author Andrew Post
 */
public class PropertyValueFilter extends AbstractFilter {

    private String property;
    private ValueComparator valueComparator;
    private Value value;

    /**
     * Instantiate with the proposition ids to which this filter applies,
     * the property to which this filter applies, the operator and a value.
     *
     * @param propositionIds a proposition id {@link String[]}.
     * @param property a property name {@link String}.
     * Cannot be <code>null</code>.
     * @param valueComparator a {@link ValueComparator} operator. Cannot be
     * <code>null</code>.
     * @param value a {@link Value}. If a {@link ListValue}, it cannot contain
     * nested lists. Cannot be <code>null</code>.
     */
    public PropertyValueFilter(String[] propositionIds,
            String property, ValueComparator valueComparator,
            Value value) {
        super(propositionIds);
        if (property == null)
            throw new IllegalArgumentException("property cannot be null");
        if (valueComparator == null) {
            throw new IllegalArgumentException(
                    "valueComparator cannot be null");
        }
        if (valueComparator == ValueComparator.UNKNOWN) {
            throw new IllegalArgumentException(
                    "Cannot use UNKNOWN value comparator here");
        }
        if (value == null)
            throw new IllegalArgumentException("value cannot be null");
        if (value instanceof ListValue) {
            ListValue<Value> lv = (ListValue<Value>) value;
            for (Value v : lv) {
                if (v instanceof ListValue) {
                    throw new IllegalArgumentException(
                            "ListValue value arguments cannot contain nested "
                            + "lists");
                }
            }
        }
        this.property = property;
        this.valueComparator = valueComparator;
        this.value = value;
    }

    /**
     * Gets the name of the specified property.
     *
     * @return a property name [@link String}. Cannot be <code>null</code>.
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the specified value.
     *
     * @return a {@link Value}. Cannot be <code>null</code>.
     */
    public Value getValue() {
        return value;
    }

    /**
     * Gets the specified operator.
     * 
     * @return a {@link ValueComparator}. Cannot be <code>null</code>.
     */
    public ValueComparator getValueComparator() {
        return valueComparator;
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
