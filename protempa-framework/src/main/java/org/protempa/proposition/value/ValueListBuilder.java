package org.protempa.proposition.value;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public class ValueListBuilder<V extends Value> implements ValueBuilder<ValueList<V>> {
    private List<V> elements;

    public ValueListBuilder() {
    }
    
    public ValueListBuilder(ValueList<V> valueList) {
        this.elements = new ArrayList<>(valueList);
    }

    public ValueBuilder<V>[] getElements() {
        return (ValueBuilder<V>[]) elements.toArray();
    }

    public void setElements(ValueBuilder<V>[] elements) {
        this.elements = new ArrayList<>();
        for (ValueBuilder<V> elt : elements) {
            this.elements.add(elt.build());
        }
    }
    
    @Override
    public ValueList<V> build() {
        return new ValueList<>(this.elements);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.elements);
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
        final ValueListBuilder<?> other = (ValueListBuilder<?>) obj;
        if (!Objects.equals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
