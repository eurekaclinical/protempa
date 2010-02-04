package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arp.javautil.collections.Collections;

/**
 * @author Andrew Post
 * 
 */
public class ListValue extends ArrayList<Value> implements Value {

	private static final long serialVersionUID = -6595541689042779012L;

	public ListValue() {
		super();
	}

	public ListValue(Collection<Value> values) {
		super(values);
	}

	public ValueComparator compare(Value val) {
		return null;
	}

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

	public ValueFactory getValueFactory() {
		return ValueFactory.LIST;
	}

	public String getRepr() {
		StringBuilder b = new StringBuilder();
		b.append(ValueImpl.reprType(ValueFactory.LIST));
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
