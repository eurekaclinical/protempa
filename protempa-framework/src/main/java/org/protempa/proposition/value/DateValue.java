package org.protempa.proposition.value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents dates and datetimes.
 * 
 * @author Andrew Post
 */
public class DateValue implements OrderedValue, Comparable<DateValue>,
        Serializable {

    private static final AbsoluteTimeGranularity gran =
            AbsoluteTimeGranularity.DAY;
    @SuppressWarnings("unchecked")
    private static final Map<Date, DateValue> cache = new ReferenceMap();
    private static final long serialVersionUID = 7939358587048726659L;
    
    /**
     * Parses a date from a string. Expects the format used by 
     * {@link AbsoluteTimeGranularity#DAY}'s <code>getShortFormat()</code>. 
     * 
     * @param str a string representing a date.
     * @return a date value, or <code>null</code> if no date could be parsed.
     */
    public static DateValue parse(String str) {
        return (DateValue) ValueType.DATEVALUE.parse(str);
    }

    /**
     * Constructs a new date value from a Java date object. Uses a cache to
     * reuse date value objects.
     * 
     * @param date a {@link Date}. If <code>null</code> a new date value is
     * created using the current time.
     * 
     * @return a {@link DateValue}.
     */
    public static DateValue getInstance(Date date) {
        DateValue result;
        if (date != null) {
            result = cache.get(date);
            if (result == null) {
                result = new DateValue(date);
                cache.put(date, result);
            }
        } else {
            result = getInstance(new Date());
        }
        return result;
    }

    /**
     * Constructs a new date value representing the current time.
     * 
     * @return a {@link DateValue}.
     */
    public static DateValue getInstance() {
        return getInstance(null);
    }
    
    private Date date;

    /**
     * Constructs a new date value object representing the current time. Use
     * {@link #getInstance() } instead to leverage a cache of frequently used
     * date values.
     * 
     * @return a {@link DateValue}.
     */
    public DateValue() {
        init(null);
    }

    /**
     * Constructs a new date value from a Java date object. Use
     * {@link #getInstance(Date) } instead to leverage a cache of frequently 
     * used date values.
     * 
     * @return a {@link DateValue}.
     */
    public DateValue(Date date) {
        init(date);
    }

    private void init(Date date) {
        if (date == null) {
            date = new Date();
        }
        this.date = date;
    }

    /**
     * Gets the date represented by this object as a Java {@link Date}.
     * 
     * @return a {@link Date}.
     */
    public Date getDate() {
        return this.date;
    }

    @Override
    public String getFormatted() {
        return gran.getShortFormat().format(this.date);
    }

    @Override
    public ValueType getType() {
        return ValueType.DATEVALUE;
    }

    /**
     * Compares this date value and another date value according to their 
     * natural order, or checks this date value for membership in a value list.
     * 
     * @param o a {@link Value}.
     * @return {@link ValueComparator#GREATER_THAN},
     * {@link ValueComparator#LESS_THAN} or {@link ValueComparator#EQUAL_TO}
     * depending on whether this value is greater than,
     * less than or equal to the value provided as argument. If the provided
     * value is a value list, returns {@link ValueComparator#IN} if this date
     * value is in the list, or {@link ValueComparator#NOT_IN} if this date
     * value is not in the list. Otherwise, returns 
     * {@link ValueComparator#UNKNOWN}.
     */
    @Override
    public ValueComparator compare(Value o) {
        if (o == null) {
            return ValueComparator.UNKNOWN;
        }
        switch (o.getType()) {
            case DATEVALUE:
                DateValue other = (DateValue) o;
                int comp = compareTo(other);
                return comp > 0 ? ValueComparator.GREATER_THAN
                        : (comp < 0 ? ValueComparator.LESS_THAN
                        : ValueComparator.EQUAL_TO);
            case VALUELIST:
                ValueList vl = (ValueList) o;
                return vl.contains(this) ? ValueComparator.IN : ValueComparator.NOT_IN;
            default:
                return ValueComparator.UNKNOWN;
        }
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

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.date);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        Date tmpDate = (Date) s.readObject();
        init(tmpDate);
        if (!cache.containsKey(tmpDate)) {
            cache.put(tmpDate, this);
        }
    }
}
