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
import org.protempa.SourceIdBuilder;
import org.protempa.proposition.value.OrderedValueBuilder;

/**
 *
 * @author Andrew Post
 */
public class ValueSetBuilder {

    private String id;
    private String displayName;
    private ValueSetElementBuilder[] valueSetElementBuilders;
    private OrderedValueBuilder lowerBoundBuilder;
    private OrderedValueBuilder upperBoundBuilder;
    private SourceIdBuilder sourceIdBuilder;
    private boolean ordered;

    public ValueSetBuilder() {
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

    public ValueSetElementBuilder[] getValueSetElementBuilders() {
        return valueSetElementBuilders;
    }

    public void setValueSetElementBuilders(ValueSetElementBuilder[] valueSetElementBuilders) {
        this.valueSetElementBuilders = valueSetElementBuilders;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public OrderedValueBuilder getLowerBoundBuilder() {
        return lowerBoundBuilder;
    }

    public void setLowerBoundBuilder(OrderedValueBuilder lowerBoundBuilder) {
        this.lowerBoundBuilder = lowerBoundBuilder;
    }

    public OrderedValueBuilder getUpperBoundBuilder() {
        return upperBoundBuilder;
    }

    public void setUpperBoundBuilder(OrderedValueBuilder upperBoundBuilder) {
        this.upperBoundBuilder = upperBoundBuilder;
    }

    public SourceIdBuilder getSourceIdBuilder() {
        return sourceIdBuilder;
    }

    public void setSourceIdBuilder(SourceIdBuilder sourceIdBuilder) {
        this.sourceIdBuilder = sourceIdBuilder;
    }

    public ValueSet build() {
        if (this.lowerBoundBuilder != null || this.upperBoundBuilder != null) {
            return new ValueSet(
                    this.id,
                    this.displayName,
                    this.lowerBoundBuilder != null ? this.lowerBoundBuilder.build() : null,
                    this.upperBoundBuilder != null ? this.upperBoundBuilder.build() : null,
                    this.sourceIdBuilder != null ? this.sourceIdBuilder.build() : null);
        } else {
            ValueSetElement[] vses
                    = new ValueSetElement[this.valueSetElementBuilders.length];
            for (int i = 0; i < vses.length; i++) {
                vses[i] = this.valueSetElementBuilders[i].build();
            }
            return new ValueSet(
                    this.id,
                    this.displayName,
                    vses,
                    this.sourceIdBuilder != null ? this.sourceIdBuilder.build() : null);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.displayName);
        hash = 79 * hash + Arrays.deepHashCode(this.valueSetElementBuilders);
        hash = 79 * hash + Objects.hashCode(this.lowerBoundBuilder);
        hash = 79 * hash + Objects.hashCode(this.upperBoundBuilder);
        hash = 79 * hash + Objects.hashCode(this.sourceIdBuilder);
        hash = 79 * hash + Objects.hashCode(this.ordered);
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
        if (!Objects.equals(this.displayName, other.displayName)) {
            return false;
        }
        if (this.ordered != other.ordered) {
            return false;
        }
        if (this.ordered) {
            if (!Arrays.deepEquals(this.valueSetElementBuilders, other.valueSetElementBuilders)) {
                return false;
            }
        } else {
            if (!Objects.equals(org.arp.javautil.arrays.Arrays.asSet(valueSetElementBuilders),
                    org.arp.javautil.arrays.Arrays.asSet(other.valueSetElementBuilders))) {
                return false;
            }
        }
        if (!Objects.equals(this.lowerBoundBuilder, other.lowerBoundBuilder)) {
            return false;
        }
        if (!Objects.equals(this.upperBoundBuilder, other.upperBoundBuilder)) {
            return false;
        }
        if (!Objects.equals(this.sourceIdBuilder, other.sourceIdBuilder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
