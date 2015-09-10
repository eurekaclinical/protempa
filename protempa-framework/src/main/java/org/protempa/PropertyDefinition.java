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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

/**
 * Defines a property of a proposition definition.
 *
 * @author Andrew Post
 */
public final class PropertyDefinition implements Serializable {
    private static final long serialVersionUID = 5258018980150529695L;
    
    private static final Attribute[] EMPTY_ATTRIBUTES = new Attribute[0];

    private final String id;
    private final String displayName;
    private final ValueType valueType;
    private final String valueSetId;
    private final String declaringPropId;
    private final String propId;
    private Attribute[] attributes;
    private Map<String, Attribute> attributeMap;
    
    public PropertyDefinition(String propId, String id, String displayName, 
            ValueType valueType, String valueSetId, String declaringPropId) {
        this(propId, id, displayName, valueType, valueSetId, declaringPropId,
                null);
    }

    /**
     * Initializes the property definition with a displayName, a value type and a
 value set.
     * 
     * @param id an unique id {@link String}. Cannot be <code>null</code>.
     * @param displayName a displayName {@link String}. If <code>null</code>, 
     * the <code>displayName</code> field is set to the value of the 
     * <code>id</code> field.
     * @param valueType a {@link ValueType}. Cannot be <code>null</code>.
     * @param valueSetId the unique id of this property's {@link ValueSet}.
     *
     * @see ValueType#isCompatible(ValueSet) 
     */
    public PropertyDefinition(String propId, String id, String displayName, 
            ValueType valueType, String valueSetId, String declaringPropId,
            Attribute[] attributes) {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("valueType cannot be null");
        }
        if (declaringPropId == null) {
            throw new IllegalArgumentException("declaringPropId cannot be null");
        }
        this.id = id.intern();
        this.displayName = displayName != null ? displayName.intern() : this.id;
        this.propId = propId;
        this.valueType = valueType;
        this.valueSetId = valueSetId;
        this.declaringPropId = declaringPropId;
        if (attributes == null) {
            this.attributes = EMPTY_ATTRIBUTES;
        } else {
            this.attributes = attributes.clone();
        }
        this.attributeMap = new HashMap<>();
        for (Attribute attribute : this.attributes) {
            this.attributeMap.put(attribute.getName(), attribute);
        }
    }

    public String getPropId() {
        return propId;
    }

    /**
     * Returns the property's id, which is unique in combination with a 
     * proposition id.
     * 
     * @return the id {@link String}. Guaranteed not <code>null</code>.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the property's displayName. Guaranteed not <code>null</code>.
     *
     * @return a {@link String}.
     */
    public String getDisplayName() {
        return this.displayName;
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
    
    public Attribute[] getAttributes() {
        return attributes.clone();
    }

    public Attribute getAttribute(String name) {
        return this.attributeMap.get(name);
    }
    
    public boolean hasAttribute(String name) {
        return getAttribute(name) != null;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
