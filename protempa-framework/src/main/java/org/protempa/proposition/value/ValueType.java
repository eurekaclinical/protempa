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
    },
    NOMINALVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.NOMINALVALUE;
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
    },
    INEQUALITYNUMBERVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.INEQUALITYNUMBERVALUE;
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
    },
    NUMBERVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.NUMBERVALUE;
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
    },
    LISTVALUE {

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.LISTVALUE;
        }
    };

    /**
     * Returns whether a value is an instance of this value type.
     *
     * @param value a {@link Value}.
     * @return <code>true</code> or <code>false</code>.
     */
    public abstract boolean isInstance(Value value);
}
