package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.List;

import org.arp.javautil.collections.Collections;

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
        return '[' + Collections.join(l, ", ") + ']';
    }

    @Override
    public String toString() {
        return ValueImpl.toString(this);
    }

    @Override
    public ValueType getType() {
        return ValueType.LISTVALUE;
    }

    @Override
    public String getRepr() {
        StringBuilder b = new StringBuilder();
        b.append(ValueImpl.reprType(ValueType.LISTVALUE));
        b.append('[');
        for (int i = 0, n = size(); i < n; i++) {
            b.append(get(i).getRepr());
            if (i < n - 1) {
                b.append(',');
            }
        }
        b.append(']');
        return b.toString();
    }
}
