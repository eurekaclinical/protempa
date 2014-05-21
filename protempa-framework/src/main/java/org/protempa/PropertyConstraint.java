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
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public final class PropertyConstraint implements Serializable {

    private static final long serialVersionUID = 3853678590231245224L;

    private String propertyName;

    private Value value;

    private ValueComparator valueComp;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public ValueComparator getValueComp() {
        return valueComp;
    }

    public void setValueComp(ValueComparator valueComp) {
        this.valueComp = valueComp;
    }

    /**
     * Returns whether a proposition satisfies this constraint. If no
     * property name or value comparator are specified, this method always
     * returns <code>true</code>.
     * 
     * @param proposition a proposition. Cannot be <code>null</code>.
     * @return <code>true</code> if the proposition satisfies this constraint,
     * <code>false</code> if not.
     */
    public boolean isSatisfiedBy(Proposition proposition) {
        if (proposition == null) {
            throw new IllegalArgumentException("proposition cannot be null");
        }
        if (this.valueComp != null && this.propertyName != null) {
            Value value = proposition.getProperty(this.propertyName);
            if (!valueComp.compare(value, this.value)) {
                return false;
            }
        }
        return true;
    }
}
