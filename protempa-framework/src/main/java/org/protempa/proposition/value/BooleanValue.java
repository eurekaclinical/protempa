package org.protempa.proposition.value;

/**
 * A boolean value.
 * 
 * @author Andrew Post
 */
public final class BooleanValue extends ValueImpl {

    private static final long serialVersionUID = 3913347786451127004L;
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);
    private final Boolean val;
    private transient volatile int hashCode;

    public BooleanValue(boolean val) {
        this(Boolean.valueOf(val));
    }

    /**
     * Creates a boolean value (<code>true</code> or <code>false</code>).
     *
     * @param val
     *            a <code>Boolean</code> object. If <code>null</code>, this
     *            <code>BooleanValue</code> object is set to
     *            <code>false</code>
     */
    public BooleanValue(Boolean val) {
        super(ValueType.BOOLEANVALUE);
        if (val != null) {
            this.val = val;
        } else {
            this.val = Boolean.FALSE;
        }
    }

    /**
     * @return
     */
    public Boolean getBoolean() {
        return val;
    }

    /**
     * @return
     */
    public boolean booleanValue() {
        return val.booleanValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.value.Value#getFormatted()
     */
    @Override
    public String getFormatted() {
        return val.toString();
    }

    /**
     * Returns the canonical string representing this value. Returns
     * "BOOLEAN_VALUE:true|false".
     *
     * @return a {@link String}.
     *
     * @see org.protempa.proposition.value.Value#getRepr()
     */
    @Override
    public String getRepr() {
        return reprType() + val.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BooleanValue)) {
            return false;
        }

        BooleanValue bv = (BooleanValue) obj;

        return val == bv.val || val.equals(bv.val);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.val.hashCode();
        }
        return this.hashCode;
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
}
