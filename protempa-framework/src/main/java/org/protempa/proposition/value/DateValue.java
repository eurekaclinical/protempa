package org.protempa.proposition.value;

import java.util.Date;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public class DateValue extends ValueImpl implements OrderedValue, 
        Comparable<DateValue> {
    private static final AbsoluteTimeGranularity gran =
            AbsoluteTimeGranularity.DAY;
    
    @SuppressWarnings("unchecked")
    private static final Map<Date,DateValue> cache = new ReferenceMap();
    
    public static DateValue getInstance(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        DateValue result = cache.get(date);
        if (result == null) {
            result = new DateValue(date);
            cache.put(date, result);
        }
        return result;
    }
    
    private final Date date;
    
    public DateValue(Date date) {
        super(ValueType.DATEVALUE);
        this.date = date;
    }
    
    public Date getDate() {
        return this.date;
    }

    @Override
    public String getFormatted() {
        return gran.getShortFormat().format(this.date);
    }

    @Override
    public Value replace() {
        DateValue result = cache.get(this.date);
        if (result != null) {
            return result;
        } else {
            return this;
        }
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(DateValue other) {
        return this.date.compareTo(other.date);
    }
    
}
