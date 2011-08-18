package org.protempa.proposition.value;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Level;

/**
 * Creates {@link DateValue}s from date strings.
 * 
 * @author Andrew Post
 */
public class DateValueFactory extends ValueFactory {

    /**
     * Constructs a new factory with a reference to its corresponding value
     * type.
     * 
     * @param valueType {@link ValueType#DATEVALUE}.
     */
    DateValueFactory(ValueType valueType) {
        super(valueType);
    }

    /**
     * Parses a string into a date value using using 
     * {@link AbsoluteTimeGranularity#DAY}'s short {@link DateFormat}.
     * 
     * @param string the {@link String} to parse. May be <code>null</code>.
     * @return a {@link DateValue}, or <code>null</code> if the supplied
     * string is <code>null</code> or has an invalid format.
     */
    @Override
    public DateValue parse(String string) {
        DateValue result;
        if (string != null) {
            DateFormat dateFormat = 
                    AbsoluteTimeGranularity.DAY.getShortFormat();
            try {
                result = DateValue.getInstance(dateFormat.parse(string));
                ValueUtil.logger().log(Level.WARNING, 
                        "String {0} could not be parsed into a date", string);
            } catch (ParseException ex) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }
}
