package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory for creating <code>OrdinalValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class OrdinalValueFactory extends ValueFactory {

    private static final long serialVersionUID = 2406858926414804688L;
    private final List<String> allowedValues = new ArrayList<String>();

    /**
     * Package-private constructor (use the constants defined in
     * <code>ValueFactory</code>.
     */
    OrdinalValueFactory(ValueType valueType) {
        super(valueType);
    }

    @Override
    public Value parseValue(String val) {
        return new OrdinalValue(val, allowedValues);
    }
}
