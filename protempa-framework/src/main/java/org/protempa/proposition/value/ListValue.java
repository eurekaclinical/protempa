package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Represents lists of values.
 * 
 * @author Andrew Post
 */
public class ListValue<V extends Value> extends ArrayList<V> implements Value {

    private static final long serialVersionUID = -6595541689042779012L;

    /**
     * Creates an empty list of values.
     */
    public ListValue() {
        super();
    }

    /**
     * Creates a list of values from the provided {@link List<V>} of values.
     * 
     * @param values a {@link List<V>} of values.
     */
    public ListValue(List<V> values) {
        super(values);
    }
    
    @Override
    public ListValue<V> replace() {
        return this;
    }

    @Override
    public ValueComparator compare(Value val) {
        return null;
    }

    @Override
    public String getFormatted() {
        List<String> l = new ArrayList<String>(size());
        for (Value val : this) {
            if (val instanceof NominalValue) {
                l.add("'" + val.getFormatted() + "'");
            } else {
                l.add(val.getFormatted());
            }
        }
        return '[' + StringUtils.join(l, ", ") + ']';
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public ValueType getType() {
        return ValueType.LISTVALUE;
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
}
