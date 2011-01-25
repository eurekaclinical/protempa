package org.protempa;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

/**
 * Defines measurable or observable time-stamped data types.
 * 
 * @author Andrew Post
 */
public final class PrimitiveParameterDefinition extends 
        AbstractPropositionDefinition implements
        TemporalPropositionDefinition {

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

    public PrimitiveParameterDefinition(KnowledgeBase kb, String id) {
        super(kb, id);
        this.valueType = DEFAULT_VALUE_TYPE;
        kb.addPrimitiveParameterDefinition(this);
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
        this.changes.firePropertyChange("valueType", old, this.valueType);
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
        this.changes.firePropertyChange("units", old, this.units);
    }

    @Override
    public void reset() {
        super.reset();
        setUnits(null);
        setValueType(null);
    }

    public void accept(PropositionDefinitionVisitor processor) {
        processor.visit(this);
    }

    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        processor.visit(this);
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
    protected void recalculateDirectChildren() {
        String[] old = this.directChildren;
        Set<String> c = new HashSet<String>();
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                c.add(propId);
            }
        }
        this.directChildren = c.toArray(new String[c.size()]);
        this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                this.directChildren);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("valueType", this.valueType)
                .append("units", this.units)
                .toString();
    }


}
