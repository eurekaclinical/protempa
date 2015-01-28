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

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueType;

/**
 * Defines a property of a proposition definition.
 *
 * @author Andrew Post
 */
public final class PropertyDefinition implements Serializable {
    private static final long serialVersionUID = 5258018980150529695L;

    private final String name;
    private final ValueType valueType;
    private final String valueSetId;
    private final String declaringPropId;
    private final String propId;

    /**
     * Initializes the property definition with a name, a value type and a
     * value set.
     * 
     * @param name a name {@link String}. Cannot be <code>null</code>.
     * @param valueType a {@link ValueType}. Cannot be <code>null</code>.
     * @param valueSet a {@link EnumeratedValueSet} that is compatible with
     * the given <code>valueType</code>.
     *
     * @see ValueType#isCompatible(ValueSet) 
     */
    public PropertyDefinition(String propId, String name, ValueType valueType,
            String valueSetId, String declaringPropId) {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("valueType cannot be null");
        }
        if (declaringPropId == null) {
            throw new IllegalArgumentException("declaringPropId cannot be null");
        }
        this.name = name.intern();
        this.propId = propId;
        this.valueType = valueType;
        this.valueSetId = valueSetId;
        this.declaringPropId = declaringPropId;
    }

    public String getPropId() {
        return propId;
    }
    
    /**
     * Returns the property's name.
     *
     * @return a {@link String}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the property's value type.
     *
     * @return a {@link ValueType}.
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * Returns the property's value set (for nominal and ordinal values).
     *
     * @return the {@link EnumeratedValueSet}, or <code>null</code> if none is
     * defined.
     */
    public String getValueSetId() {
        return this.valueSetId;
    }

    public String getDeclaringPropId() {
        return this.declaringPropId;
    }
    
    public boolean isInherited() {
        return !this.propId.equals(this.declaringPropId);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
