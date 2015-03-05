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
public class NominalValueBuilder implements ValueBuilder<NominalValue> {
    private String val;
    
    public NominalValueBuilder() {
        
    }
    
    public NominalValueBuilder(NominalValue nominalValue) {
        this.val = nominalValue.getString();
    }
    
    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
    
    @Override
    public NominalValue build() {
        return NominalValue.getInstance(this.val);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.val);
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
        final NominalValueBuilder other = (NominalValueBuilder) obj;
        if (!Objects.equals(this.val, other.val)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
