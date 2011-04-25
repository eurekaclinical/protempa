package org.protempa.proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

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

    protected TemporalParameter() {}

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
                && (this.value == p.value
                || (this.value != null && this.value.equals(p.value)));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("value", this.value).toString();
    }

    protected void writeTemporalParameter(ObjectOutputStream s) throws IOException {
        if (this.value != null) {
            s.writeObject(this.value.getType());
            s.writeObject(this.value.getRepr());
        } else {
            s.writeObject(null);
        }
    }

    protected void readTemporalParameter(ObjectInputStream s) throws IOException, ClassNotFoundException {
        ValueType valueType = (ValueType) s.readObject();
        if (valueType != null) {
            this.value = ValueFactory.parseRepr((String) s.readObject());
        }
    }


}
