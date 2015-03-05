package org.protempa.valueset;

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

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueBuilder;

/**
 *
 * @author Andrew Post
 */
public class ValueSetElementBuilder {
    private ValueBuilder valueBuilder;
    private String displayName;
    private String abbrevDisplayName;
    
    public ValueSetElementBuilder() {
        
    }
    
    public ValueSetElementBuilder(ValueSetElement valueSetElement) {
        Value value = valueSetElement.getValue();
        this.valueBuilder = value.asBuilder();
        this.displayName = valueSetElement.getDisplayName();
        this.abbrevDisplayName = valueSetElement.getAbbrevDisplayName();
    }

    public ValueBuilder getValueBuilder() {
        return valueBuilder;
    }

    public void setValueBuilder(ValueBuilder value) {
        this.valueBuilder = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAbbrevDisplayName() {
        return abbrevDisplayName;
    }

    public void setAbbrevDisplayName(String abbrevDisplayName) {
        this.abbrevDisplayName = abbrevDisplayName;
    }
    
    public ValueSetElement build() {
        return new ValueSetElement(this.valueBuilder != null ? this.valueBuilder.build() : null, this.displayName, this.abbrevDisplayName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.valueBuilder);
        hash = 61 * hash + Objects.hashCode(this.displayName);
        hash = 61 * hash + Objects.hashCode(this.abbrevDisplayName);
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
        final ValueSetElementBuilder other = (ValueSetElementBuilder) obj;
        if (!Objects.equals(this.valueBuilder, other.valueBuilder)) {
            return false;
        }
        if (!Objects.equals(this.displayName, other.displayName)) {
            return false;
        }
        if (!Objects.equals(this.abbrevDisplayName, other.abbrevDisplayName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    
    
}
