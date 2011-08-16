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
    public Value parse(String val) {
        Value result = NUMBER.parse(val);
        if (result == null) {
            return INEQUALITY.parse(val);
        } else {
            return result;
        }
    }
}
