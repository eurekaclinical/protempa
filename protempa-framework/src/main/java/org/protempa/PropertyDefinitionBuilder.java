package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import java.io.Serializable;
import java.util.Objects;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
public final class PropertyDefinitionBuilder implements Serializable {
    private static final long serialVersionUID = 1;
    
    private String id;
    private String displayName;
    private ValueType valueType;
    private String valueSetId;
    private String declaringPropId;
    private String propId;

    public PropertyDefinitionBuilder() {
    }
    
    public PropertyDefinitionBuilder(PropertyDefinition propertyDefinition) {
        if (propertyDefinition != null) {
            this.id = propertyDefinition.getId();
            this.displayName = propertyDefinition.getDisplayName();
            this.valueType = propertyDefinition.getValueType();
            this.valueSetId = propertyDefinition.getValueSetId();
            this.declaringPropId = propertyDefinition.getDeclaringPropId();
            this.propId = propertyDefinition.getPropId();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getValueSetId() {
        return valueSetId;
    }

    public void setValueSetId(String valueSetId) {
        this.valueSetId = valueSetId;
    }

    public String getDeclaringPropId() {
        return declaringPropId;
    }

    public void setDeclaringPropId(String declaringPropId) {
        this.declaringPropId = declaringPropId;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }
    
    public PropertyDefinition build() {
        return new PropertyDefinition(propId, id, displayName, valueType, valueSetId, declaringPropId);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.displayName);
        hash = 83 * hash + Objects.hashCode(this.valueType);
        hash = 83 * hash + Objects.hashCode(this.valueSetId);
        hash = 83 * hash + Objects.hashCode(this.declaringPropId);
        hash = 83 * hash + Objects.hashCode(this.propId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyDefinitionBuilder other = (PropertyDefinitionBuilder) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.displayName, other.displayName)) {
            return false;
        }
        if (this.valueType != other.valueType) {
            return false;
        }
        if (!Objects.equals(this.valueSetId, other.valueSetId)) {
            return false;
        }
        if (!Objects.equals(this.declaringPropId, other.declaringPropId)) {
            return false;
        }
        if (!Objects.equals(this.propId, other.propId)) {
            return false;
        }
        return true;
    }
    
}
