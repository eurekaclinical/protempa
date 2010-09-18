package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;
import org.protempa.proposition.value.Value;

/**
 * A parameter with no temporal component.
 * 
 * @author Andrew Post
 */
public final class ConstantParameter extends AbstractProposition implements
        Parameter {

    private static final long serialVersionUID = 7205801414947324421L;
    private static final String DEFAULT_ATTRIBUTE_ID = "ATTR_VALUE";
    /**
     * This parameter's value.
     */
    private Value value;
    /**
     * This primitive parameter's attribute id.
     */
    private final String attributeId;

    /**
     * Creates a constant parameter with an identifier <code>String</code> and
     * no attribute id.
     *
     * @param id
     *            an identifier <code>String</code>.
     */
    public ConstantParameter(String id) {
        this(id, null);
    }

    /**
     * Creates a constant parameter with an identifier <code>String</code> and
     * an attribute id <code>String</code>.
     *
     * @param id
     *            an identifier <code>String</code>.
     * @param attributeId
     *            an attribute identifier <code>String</code>.
     */
    public ConstantParameter(String id, String attributeId) {
        super(id);
        if (attributeId != null) {
            this.attributeId = attributeId;
        } else {
            this.attributeId = DEFAULT_ATTRIBUTE_ID;
        }
    }

    /**
     * Gets the value of this parameter.
     *
     * @return the <code>Value</code> of this parameter.
     */
    public Value getValue() {
        return value;
    }

    /**
     * Sets the value of this parameter.
     *
     * @param value
     *            a <code>Value</code>.
     */
    public void setValue(Value value) {
        this.value = value;
        hashCode = 0;
    }

    /**
     * Returns this parameter's attribute id.
     *
     * @return an attribute id <code>String</code>.
     */
    public String getAttributeId() {
        return attributeId;
    }

    /**
     * Returns this parameter's value formatted as a string. This is equivalent
     * to calling <code>getValue().getFormatted()</code>, but it handles the
     * case where <code>getValue()</code> returns <code>null</code>.
     *
     * @return a <code>String</code> object, or an empty string if this
     *         parameter's value is <code>null</code>.
     */
    public final String getValueFormatted() {
        return value != null ? value.getFormatted() : "";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.Proposition#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.Proposition#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.AbstractProposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConstantParameter)) {
            return false;
        }

        ConstantParameter p = (ConstantParameter) o;
        return super.isEqual(p)
                && (this.attributeId == p.attributeId || (this.attributeId.equals(p.attributeId)))
                && (this.value == p.value || (this.value != null && this.value.equals(p.value)));
    }

    public void accept(PropositionVisitor propositionVisitor) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor) throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("value", this.value)
                .append("attributeId", this.attributeId)
                .toString();
    }
}
