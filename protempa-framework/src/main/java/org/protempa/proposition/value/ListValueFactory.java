package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Post
 * 
 */
public class ListValueFactory extends ValueFactory {

    private static final long serialVersionUID = -8487274287381420322L;

    /**
     * @param str
     */
    ListValueFactory(ValueType valueType) {
        super(valueType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
     */
    @Override
    public Value parse(String val) {
        if (val == null) {
            return null;
        }
        if (val.startsWith("[") && val.endsWith("]")) {
            String[] vals = val.substring(1, val.length() - 1).split(",");
            List<String> mergedInnerLists = new ArrayList<String>(vals.length);
            StringBuilder b = new StringBuilder();
            int refCount = 0;
            for (String str : vals) {
                String strTrimmed = str.trim();
                boolean startsWithOpenBracket = strTrimmed.startsWith("[");
                boolean endsWithCloseBracket = strTrimmed.endsWith("]");
                if (startsWithOpenBracket && endsWithCloseBracket) {
                    mergedInnerLists.add(strTrimmed);
                } else if (startsWithOpenBracket) {
                    b.append(strTrimmed);
                    refCount++;
                } else if (endsWithCloseBracket) {
                    b.append(',');
                    b.append(strTrimmed);
                    if (refCount-- == 1) {
                        mergedInnerLists.add(b.toString());
                        b.setLength(0);
                    }
                } else if (refCount > 0) {
                    b.append(',');
                    b.append(strTrimmed);
                } else {
                    mergedInnerLists.add(strTrimmed);
                }
            }
            List<Value> l = new ArrayList<Value>(vals.length);
            ValueFactory valueFactory = ValueType.VALUE.getValueFactory();
            for (String s : mergedInnerLists) {
                if ((s.startsWith("'") && s.endsWith("'"))
                        || (s.startsWith("\"") && s.endsWith("\""))) {
                    l.add(ValueFactory.NOMINAL.parse(s.substring(1, s.length() - 1)));
                } else {
                    l.add(valueFactory.parse(s));
                }
            }
            return new ListValue(l);
        } else {
            return null;
        }
    }
}
