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

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import org.protempa.proposition.value.ValueType;

/**
 * Defines measurable or observable time-stamped data types.
 * 
 * @author Andrew Post
 */
public final class PrimitiveParameterDefinition 
        extends AbstractPropositionDefinition 
        implements TemporalPropositionDefinition, ParameterDefinition {

    private static final long serialVersionUID = 4469613843480322419L;
    /**
     * The default value factory (<code>ValueFactory.NOMINAL</code>).
     */
    public static final ValueType DEFAULT_VALUE_TYPE = ValueType.NOMINALVALUE;
    /**
     * The allowed types of values for this primitive parameter.
     */
    private ValueType valueType;
    /**
     * The units for values of this primitive parameter.
     */
    private String units;

    public PrimitiveParameterDefinition(String id) {
        super(id);
        this.valueType = DEFAULT_VALUE_TYPE;
    }

    /**
     * Gets the value factory for this primitive parameter definition.
     *
     * @return a {@link ValueFactory}, guaranteed not to be
     *         <code>null</code>.
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * Sets the value factory for this primitive parameter definition.
     *
     * @param vf
     *            a {@link ValueFactory}. If <code>null</code>, the
     *            default value factory (defined by
     *            <code>DEFAULT_VALUE_FACTORY</code> is set.
     */
    public void setValueType(ValueType vf) {
        if (vf == null) {
            vf = DEFAULT_VALUE_TYPE;
        }
        ValueType old = this.valueType;
        this.valueType = vf;
        if (this.changes != null) {
            this.changes.firePropertyChange("valueType", old, this.valueType);
        }
    }

    /**
     * Returns the units for values of this primitive parameter.
     *
     * @return a units {@link String}.
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units for values of this primitive parameter.
     *
     * @param units
     *            a units {@link String}.
     */
    public void setUnits(String units) {
        String old = this.units;
        this.units = units;
        if (this.changes != null) {
            this.changes.firePropertyChange("units", old, this.units);
        }
    }

    @Override
    public void reset() {
        super.reset();
        setUnits(null);
        setValueType(null);
    }

    @Override
    public void accept(PropositionDefinitionVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("visitor cannot be null.");
        }
        visitor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor visitor)
            throws ProtempaException {
        if (visitor == null) {
            throw new IllegalArgumentException("visitor cannot be null.");
        }
        visitor.visit(this);
    }

    /**
     * By definition, primitive parameters are not concatenable.
     *
     * @return <code>false</code>.
     * @see org.protempa.PropositionDefinition#isConcatenable()
     */
    @Override
    public boolean isConcatenable() {
        return false;
    }

    /**
     * By definition, primitive parameters are solid.
     * @return <code>true</code>.
     * @see org.protempa.PropositionDefinition#isSolid()
     */
    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        Set<String> c = new HashSet<String>();
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                c.add(propId);
            }
        }
        this.children = c.toArray(new String[c.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
