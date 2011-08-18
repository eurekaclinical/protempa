package org.protempa.proposition.value;

/**
 * Represents types of values of propositions and properties.
 * 
 * @author Andrew Post
 */
public enum ValueType {

    VALUE {
        
        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return true;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.VALUE;
        }
    },
    NOMINALVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.NOMINALVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.NOMINAL;
        }
    },
    BOOLEANVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.BOOLEANVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.BOOLEAN;
        }
    },
    ORDEREDVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            ValueType valueType = value.getType();
            return valueType == ValueType.NUMERICALVALUE
                    || valueType == ValueType.ORDINALVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            throw new UnsupportedOperationException("Not implemented");
        }
    },
    INEQUALITYNUMBERVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.INEQUALITYNUMBERVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.INEQUALITY;
        }
    },
    NUMERICALVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            ValueType valueType = value.getType();
            return valueType == ValueType.NUMBERVALUE
                    || valueType == ValueType.INEQUALITYNUMBERVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.NUMERICAL;
        }
    },
    NUMBERVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.NUMBERVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.NUMBER;
        }
    },
    ORDINALVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.ORDINALVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.ORDINAL;
        }
    },
    LISTVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.LISTVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.LIST;
        }
    },
    DATEVALUE {
        
        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.DATEVALUE;
        }
        
        @Override
        public ValueFactory getValueFactory() {
            return ValueFactory.DATE;
        }
    };

    /**
     * Returns whether a value is an instance of this value type.
     *
     * @param value a {@link Value}.
     * @return <code>true</code> or <code>false</code>.
     */
    public abstract boolean isInstance(Value value);
    
    /**
     * Returns a value factory for creating values of this type.
     * 
     * @return a {@link ValueFactory}. Guaranteed not <code>null</code>.
     */
    public abstract ValueFactory getValueFactory();
}
