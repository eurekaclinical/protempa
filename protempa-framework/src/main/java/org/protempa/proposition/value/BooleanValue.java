package org.protempa.proposition.value;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A boolean value.
 * 
 * @author Andrew Post
 */
public final class BooleanValue extends ValueImpl {

    private static final long serialVersionUID = 3913347786451127004L;
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);
    private final boolean val;
    private transient volatile int hashCode;

    public BooleanValue(boolean val) {
        super(ValueType.BOOLEANVALUE);
        this.val = val;
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
            this.val = val.booleanValue();
        } else {
            this.val = false;
        }
    }
    
    @Override
    public BooleanValue replace() {
        return this;
    }

    /**
     * @return
     */
    public Boolean getBoolean() {
        return Boolean.valueOf(this.val);
    }

    /**
     * @return
     */
    public boolean booleanValue() {
        return val;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.value.Value#getFormatted()
     */
    @Override
    public String getFormatted() {
        return Boolean.toString(this.val);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BooleanValue other = (BooleanValue) obj;
        if (this.val != other.val) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 17 * hash + (this.val ? 1 : 0);
            this.hashCode = hash;
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
