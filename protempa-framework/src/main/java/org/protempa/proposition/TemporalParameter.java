package org.protempa.proposition;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;

/**
 * @author Andrew Post
 */
public abstract class TemporalParameter extends TemporalProposition
        implements Parameter {

    private static final long serialVersionUID = 1553031842442677948L;
    private Value value;

    TemporalParameter(String id) {
        super(id);
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public String getValueFormatted() {
        return this.value != null ? this.value.getFormatted() : "";
    }

    public void setValue(Value value) {
        this.value = value;
        this.hashCode = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.TemporalProposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TemporalParameter)) {
            return false;
        }

        TemporalParameter p = (TemporalParameter) o;
        return super.isEqual(p)
                && (this.value == p.value ||
                (this.value != null && this.value.equals(p.value)));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("value", this.value)
                .toString();
    }
}
