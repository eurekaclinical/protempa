package org.protempa.proposition.value;

/**
 * A factory for creating <code>NumericalValue</code> objects.
 * 
 * @author Andrew Post
 */
public class NumericalValueFactory extends ValueFactory {

    private static final long serialVersionUID = 3863928880426027508L;

    /**
     * Package-private constructor (use the constants defined in
     * <code>ValueFactory</code>.
     */
    NumericalValueFactory(ValueType valueType) {
        super(valueType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
     */
    @Override
    public Value parseValue(String val) {
        Value result = NUMBER.parseValue(val);
        if (result == null) {
            return INEQUALITY.parseValue(val);
        } else {
            return result;
        }
    }
}
