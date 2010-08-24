package org.protempa.proposition.value;

import java.math.BigDecimal;

/**
 * A factory for creating <code>InequalityNumberValue</code> objects.
 * 
 * @author Andrew Post
 */
public class InequalityNumberValueFactory extends NumericalValueFactory {

    private static final long serialVersionUID = 2165408517405160378L;

    /**
     * Package-private constructor (use the constants defined in
     * <code>ValueFactory</code>.
     */
    InequalityNumberValueFactory(ValueType valueType) {
        super(valueType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
     */
    @Override
    public Value parseValue(String s) {
        InequalityNumberValue result = null;
        try {
            s = s.trim();
            ValueComparator comparator = ValueComparator.parse(s.substring(0, 1));
            BigDecimal val = new BigDecimal(s.substring(1).trim());
            result = new InequalityNumberValue(comparator, val);
        } catch (Exception ex) {
        }
        return result;
    }
}
