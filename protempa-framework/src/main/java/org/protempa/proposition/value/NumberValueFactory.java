package org.protempa.proposition.value;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;

/**
 * A factory for creating <code>NumberValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class NumberValueFactory extends NumericalValueFactory {

    private static final long serialVersionUID = 5725140172150432391L;

    @SuppressWarnings("unchecked")
    private Map<String, BigDecimal> cache = new ReferenceMap();

    /**
     * Use the constants defined in <code>ValueFactory</code> instead of this.
     */
    NumberValueFactory(ValueType valueType) {
        super(valueType);
    }
    
    @Override
    public Value parseValue(String val) {
        if (val != null) {
            try {
                /*
                 * BigDecimal constructor returns a NumberFormatException if
                 * if there are spaces before or after the number in val.
                 */
                String valTrimmed = val.trim();
                BigDecimal bd = this.cache.get(valTrimmed);
                if (bd == null) {
                    bd = new BigDecimal(valTrimmed);
                    this.cache.put(valTrimmed, bd);
                }
                return NumberValue.getInstance(bd);
            } catch (NumberFormatException e) {
                /**
                 * NumericalValueFactory relies on this returning null.
                 */
                return null;
            }
        } else {
            return null;
        }
    }
}
