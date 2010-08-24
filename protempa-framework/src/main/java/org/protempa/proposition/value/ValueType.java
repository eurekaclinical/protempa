package org.protempa.proposition.value;

/**
 *
 * @author Andrew Post
 */
public enum ValueType {
    VALUE {
        @Override
        public boolean isInstance(Value value) {
            return true;
        }
    },
    NOMINALVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.NOMINALVALUE;
        }
    },
    BOOLEANVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.BOOLEANVALUE;
        }
    },
    ORDEREDVALUE {
        @Override
        public boolean isInstance(Value value) {
            ValueType valueType = value.getType();
            return valueType == ValueType.NUMERICALVALUE ||
                    valueType == ValueType.ORDINALVALUE;
        }
    },
    INEQUALITYNUMBERVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.INEQUALITYNUMBERVALUE;
        }
    },
    NUMERICALVALUE {
        @Override
        public boolean isInstance(Value value) {
            ValueType valueType = value.getType();
            return valueType == ValueType.NUMBERVALUE ||
                    valueType == ValueType.INEQUALITYNUMBERVALUE;
        }
    },
    NUMBERVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.NUMBERVALUE;
        }
    },
    ORDINALVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.ORDINALVALUE;
        }
    },
    LISTVALUE {
        @Override
        public boolean isInstance(Value value) {
            return value.getType() == ValueType.LISTVALUE;
        }
    };

    public abstract boolean isInstance(Value value);
}
