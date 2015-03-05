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

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.SourceId;
import org.protempa.proposition.value.OrderedValue;

/**
 *
 * @author Andrew Post
 */
public class ValueSetBuilder {
    private String id;
    private ValueSetElementBuilder[] valueSetElementBuilders;
    private OrderedValue lowerBound;
    private OrderedValue upperBound;
    private SourceId sourceId;
    
    public ValueSetBuilder() {
        
    }
    
    public ValueSetBuilder(ValueSet valueSet) {
        this.id = valueSet.getId();
        ValueSetElement[] valueSetElements = valueSet.getValueSetElements();
        this.valueSetElementBuilders = new ValueSetElementBuilder[valueSetElements.length];
        for (int i = 0; i < valueSetElements.length; i++) {
            this.valueSetElementBuilders[i] = new ValueSetElementBuilder(valueSetElements[i]);
        }
        this.lowerBound = valueSet.getLowerBound();
        this.upperBound = valueSet.getUpperBound();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ValueSetElementBuilder[] getValueSetElementBuilders() {
        return valueSetElementBuilders;
    }

    public void setValueSetElementBuilders(ValueSetElementBuilder[] valueSetElementBuilders) {
        this.valueSetElementBuilders = valueSetElementBuilders;
    }

    public OrderedValue getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(OrderedValue lowerBound) {
        this.lowerBound = lowerBound;
    }

    public OrderedValue getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(OrderedValue upperBound) {
        this.upperBound = upperBound;
    }

    public SourceId getSourceId() {
        return sourceId;
    }

    public void setSourceId(SourceId sourceId) {
        this.sourceId = sourceId;
    }
    
    public ValueSet build() {
        if (this.lowerBound != null || this.upperBound != null) {
            return new ValueSet(this.id, this.lowerBound, this.upperBound, this.sourceId);
        } else {
            ValueSetElement[] vses = new ValueSetElement[this.valueSetElementBuilders.length];
            for (int i = 0; i < vses.length; i++) {
                vses[i] = this.valueSetElementBuilders[i].build();
            }
            return new ValueSet(this.id, vses, this.sourceId);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Arrays.deepHashCode(this.valueSetElementBuilders);
        hash = 79 * hash + Objects.hashCode(this.lowerBound);
        hash = 79 * hash + Objects.hashCode(this.upperBound);
        hash = 79 * hash + Objects.hashCode(this.sourceId);
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
        final ValueSetBuilder other = (ValueSetBuilder) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Arrays.deepEquals(this.valueSetElementBuilders, other.valueSetElementBuilders)) {
            return false;
        }
        if (!Objects.equals(this.lowerBound, other.lowerBound)) {
            return false;
        }
        if (!Objects.equals(this.upperBound, other.upperBound)) {
            return false;
        }
        if (!Objects.equals(this.sourceId, other.sourceId)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    
}
