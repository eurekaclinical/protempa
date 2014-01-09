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
package org.protempa.dest.table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * Specifies a constraint on the value of a proposition property.
 * 
 * @author mmansou
 */
public final class PropertyConstraint {

    private final String propertyName;
    private final ValueComparator valueComparator;
    private final Value value;

    /**
     * Constructs a property constraint with a property name, comparator and
     * one or more values.
     * 
     * @param propertyName the name of the property. Cannot be 
     * <code>null</code>.
     * @param valueComparator the comparator. Cannot be <code>null</code>.
     * @param values the values to compare against.
     */
    public PropertyConstraint(String propertyName,
            ValueComparator valueComparator, Value value) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (valueComparator == null) {
            throw new IllegalArgumentException(
                    "valueComparator cannot be null");
        }
        this.propertyName = propertyName;
        this.valueComparator = valueComparator;
        this.value = value;
    }

    /**
     * Returns the name of the property.
     * 
     * @return a property name {@link String}. Guaranteed not 
     * <code>null</code>.
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Gets the value of the constraint. Use a 
     * {@link org.protempa.proposition.value.ValueList} to specify a multi-
     * valued constraint.
     * 
     * @return a {@link Value}.
     */
    public Value getValue() {
        return this.value;
    }

    /**
     * Gets the comparator of the constraint.
     * 
     * @return a {@link ValueComparator}. Guaranteed not <code>null</code>.
     */
    public ValueComparator getValueComparator() {
        return this.valueComparator;
    }
    
    /**
     * Gets the constraint expressed in a string. This is for use by
     * {@link Link}s for the table header.
     * 
     * @return the constraint as a {@link String}. Guaranteed not 
     * <code>null</code>.
     */
    String getFormatted() {
        StringBuilder str = new StringBuilder();
        str.append(this.propertyName);
        str.append(this.valueComparator.getComparatorString());
        if (this.value != null) {
            str.append(this.value.getFormatted());
        } else {
            str.append("null");
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((valueComparator == null) ? 0 : valueComparator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PropertyConstraint other = (PropertyConstraint) obj;
		if (propertyName == null) {
			if (other.propertyName != null) {
				return false;
			}
		} else if (!propertyName.equals(other.propertyName)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		if (valueComparator != other.valueComparator) {
			return false;
		}
		return true;
	}
}
