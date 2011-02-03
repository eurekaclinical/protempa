package org.protempa.proposition.value;

import java.math.BigDecimal;

/**
 * A factory for creating <code>NumberValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class NumberValueFactory extends NumericalValueFactory {

    private static final long serialVersionUID = 5725140172150432391L;

    /**
     * Package-private constructor (use the constants defined in
     * <code>ValueFactory</code>.
     */
    NumberValueFactory(ValueType valueType) {
        super(valueType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
     */
    @Override
    public Value parseValue(String val) {
        if (val != null) {
            try {
                return NumberValue.getInstance(new BigDecimal(val.trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
