package org.protempa.proposition.value;

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

/**
 *
 * @author Andrew Post
 */
public class OrdinalValueBuilder implements OrderedValueBuilder {
    private String value;
    private int index;

    public OrdinalValueBuilder() {
    }
    
    public OrdinalValueBuilder(OrdinalValue ordinalValue) {
        this.value = ordinalValue.getValue();
        this.index = ordinalValue.getIndex();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public OrdinalValue build() {
        return new OrdinalValue(this.value, this.index);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + this.index;
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
        final OrdinalValueBuilder other = (OrdinalValueBuilder) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
