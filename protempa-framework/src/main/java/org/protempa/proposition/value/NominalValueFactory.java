package org.protempa.proposition.value;

/**
 * A factory for creating <code>NominalValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class NominalValueFactory extends ValueFactory {

    private static final long serialVersionUID = 9116626325566355765L;

    /**
     * Package-private constructor (use the constants defined in
     * <code>ValueFactory</code>.
     */
    NominalValueFactory(ValueType valueType) {
        super(valueType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
     */
    @Override
    public NominalValue parseValue(String val) {
        return new NominalValue(val);
    }
}
