package org.protempa;

import java.io.Serializable;

import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueFactory;

import org.arp.javautil.arrays.Arrays;
import org.protempa.proposition.value.ValueType;

/**
 * Definition of an algorithm parameter.
 * 
 * @author Andrew Post
 */
public class AlgorithmParameter implements Serializable {

    private static final long serialVersionUID = 8764701614842981050L;
    private static final ValueComparator[] NULL_VALUE_COMPARATOR_ARRAY = new ValueComparator[0];
    /**
     * The name of the parameter.
     */
    private final String name;
    /**
     * The allowed comparators for the parameter.
     */
    private final ValueComparator[] comparators;
    /**
     * The allowed types of values for this parameter, one of
     * <code>ValueFactory</code> or its subclasses.
     */
    private final ValueType valueType;

    /**
     * Defines a parameter.
     *
     * @param name
     *            the name of the parameter.
     * @param comparators
     *            the allowed comparators for the parameter.
     * @param valueFactory
     *            the allowed types of values for this parameter, one of
     *            <code>Value</code> or its subclasses.
     */
    public AlgorithmParameter(String name, ValueComparator[] comparators,
            ValueType valueType) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        if (comparators == null) {
            this.comparators = NULL_VALUE_COMPARATOR_ARRAY;
        } else {
            this.comparators = comparators;
        }
        this.valueType = valueType;
    }

    /**
     * Returns the allowed comparators for this parameter.
     *
     * @return an array of <code>ValueComparator</code> objects. Never returns
     *         <code>null</code> (but can be length zero).
     */
    public ValueComparator[] getComparators() {
        return comparators;
    }

    /**
     * Returns the name of this parameter.
     *
     * @return a <code>String</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of value of this parameter.
     *
     * @return one of <code>ValueFactory</code> or its subclasses.
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * Returns true if the given value comparator is allowed for this parameter.
     *
     * @param valueComparator
     *            the value comparator to search for.
     *
     * @return true if the given value comparator is allowed for this parameter.
     */
    public boolean hasComparator(ValueComparator valueComparator) {
        return Arrays.contains(this.comparators, valueComparator);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " -- name: " + this.name
                + "; comparators: " + java.util.Arrays.asList(this.comparators)
                + "; valueType: " + this.valueType;
    }
}
