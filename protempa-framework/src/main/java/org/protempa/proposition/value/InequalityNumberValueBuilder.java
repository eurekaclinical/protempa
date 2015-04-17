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

import java.math.BigDecimal;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public class InequalityNumberValueBuilder implements NumericalValueBuilder {
    private ValueComparator comparator;
    private BigDecimal val;

    public InequalityNumberValueBuilder() {
    }

    public InequalityNumberValueBuilder(InequalityNumberValue inequalityNumberValue) {
        this.comparator = inequalityNumberValue.getComparator();
        this.val = inequalityNumberValue.getBigDecimal();
    }

    public ValueComparator getComparator() {
        return comparator;
    }

    public void setComparator(ValueComparator comparator) {
        this.comparator = comparator;
    }

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
    }
    
    @Override
    public InequalityNumberValue build() {
        return new InequalityNumberValue(comparator, val);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.comparator);
        hash = 11 * hash + Objects.hashCode(this.val);
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
        final InequalityNumberValueBuilder other = (InequalityNumberValueBuilder) obj;
        if (this.comparator != other.comparator) {
            return false;
        }
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
