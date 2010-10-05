package org.protempa.proposition.value;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * @author Andrew Post
 */
class RelativeTimeUnitFormat extends Format {
    private static final long serialVersionUID = -5276847494971693256L;

    private final Unit units;
    private final MessageFormat messageFormat;
    private final long length;
    private final NumberFormat numberFormat;

    RelativeTimeUnitFormat(Unit units, long length, String pattern) {
        this(units, length, pattern, true);
    }

    RelativeTimeUnitFormat(Unit units, long length, String pattern,
            boolean groupingUsed) {
        this.units = units;
        this.messageFormat = new MessageFormat(pattern);
        this.length = length;
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setGroupingUsed(groupingUsed);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        assert obj instanceof Long : obj + " is not a Long";
        String val = this.numberFormat.format(obj);
        return messageFormat.format(new Object[]{val}, toAppendTo, pos);
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        Object result = messageFormat.parseObject(source, pos);

        if (result != null && result instanceof Object[]) {
            Object[] resultArr = (Object[]) result;
            if (resultArr.length > 0) {
                Number num = numberFormat.parse((String) resultArr[0],
                        new ParsePosition(0));
                if (units != null) {
                    return num.longValue() * length;
                } else {
                    return num;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
