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
package org.protempa.backend.dsb.filter;

import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueList;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * For specifying constraints on the value of a property of a proposition.
 * 
 * @author Andrew Post
 */
public class PropertyValueFilter extends AbstractFilter {

    private final String property;
    private final ValueComparator valueComparator;
    private final Value[] values;

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
     * @param negation a <code>boolean</code>, <code>false</code> means find
     * propositions that do not have the specified value.
     */
    public PropertyValueFilter(String[] propositionIds,
            String property, ValueComparator valueComparator,
            Value... values) {
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
        if (values.length > 1) {
            if (valueComparator != ValueComparator.IN && valueComparator != ValueComparator.NOT_IN) {
                throw new IllegalArgumentException(
                        "Multiple value arguments are only allowed if the value comparator is IN or NOT_IN");
            }
        }
        for (Value val : values) {
            if (val instanceof ValueList) {
                throw new IllegalArgumentException("values connnot contain any ListValues");
            }
        }
        this.property = property.intern();
        this.valueComparator = valueComparator;
        this.values = values.clone();
    }

    /**
     * Gets the name of the specified property.
     *
     * @return a property name [@link String}. Cannot be <code>null</code>.
     */
    public String getProperty() {
        return this.property;
    }

    /**
     * Gets the specified value.
     *
     * @return a {@link Value}. Cannot be <code>null</code>.
     */
    public Value[] getValues() {
        return this.values.clone();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((valueComparator == null) ? 0 : valueComparator.hashCode());
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyValueFilter other = (PropertyValueFilter) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (valueComparator != other.valueComparator)
			return false;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}
}
