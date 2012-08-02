/*
 * #%L 
 * Protempa Framework 
 * %% 
 * Copyright (C) 2012 Emory University 
 * %% 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition.value;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.collections.map.ReferenceMap;

/**
 * Represents types of values of propositions and properties, and provides a
 * method for parsing them from strings.
 * 
 * @author Andrew Post
 */
public enum ValueType {

    VALUE {
        private ValueType[] parseOrder;

        @Override
        public Value parse(String val) {
            if (parseOrder == null) {
                parseOrder = new ValueType[] { BOOLEANVALUE, NUMBERVALUE,
                        INEQUALITYNUMBERVALUE, DATEVALUE, VALUELIST,
                        NOMINALVALUE };
            }
            Value result = null;

            for (int i = 0; i < parseOrder.length; i++) {
                if ((result = parseOrder[i].parse(val)) != null) {
                    break;
                }
            }

            return result;
        }

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
        public NominalValue parse(String val) {
            return NominalValue.getInstance(val);
        }

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
        public Value parse(String val) {
            if ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
                return Boolean.valueOf(val).booleanValue() ? BooleanValue.TRUE
                        : BooleanValue.FALSE;
            }
            return null;
        }

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
        public Value parse(String val) {
            Value result = NUMERICALVALUE.parse(val);
            if (result == null) {
                result = DATEVALUE.parse(val);
            }
            return result;
        }

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
        public Value parse(String s) {
            if (s != null) {
                InequalityNumberValue result = null;
                s = s.trim();
                String comparatorString;
                String numberString;
                if (s.startsWith(">=") || s.startsWith("<=")) {
                    comparatorString = s.substring(0, 2);
                    numberString = s.substring(2).trim();
                } else if (s.startsWith(">") || s.startsWith("<")) {
                    comparatorString = s.substring(0, 1);
                    numberString = s.substring(1).trim();
                } else {
                    return null;
                }
                ValueComparator comparator = ValueComparator
                        .parse(comparatorString);
                BigDecimal val = new BigDecimal(numberString);
                result = new InequalityNumberValue(comparator, val);
                return result;
            } else {
                return null;
            }
        }

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
        public Value parse(String val) {
            Value result = NUMBERVALUE.parse(val);
            if (result == null) {
                return INEQUALITYNUMBERVALUE.parse(val);
            } else {
                return result;
            }
        }

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
        @SuppressWarnings("unchecked")
        private Map<String, BigDecimal> cache = new ReferenceMap();

        @Override
        public Value parse(String val) {
            if (val != null) {
                try {
                    /*
                     * BigDecimal constructor returns a NumberFormatException if
                     * there are spaces before or after the number in val.
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

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.NUMBERVALUE;
        }
    },
    ORDINALVALUE {
        private final List<String> allowedValues = new ArrayList<String>();

        @Override
        public Value parse(String val) {
            return new OrdinalValue(val, allowedValues);
        }

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.ORDINALVALUE;
        }
    },
    VALUELIST {
        @Override
        public Value parse(String val) {
            if (val == null) {
                return null;
            }
            if (val.startsWith("[") && val.endsWith("]")) {
                String[] vals = val.substring(1, val.length() - 1).split(",");
                List<String> mergedInnerLists = new ArrayList<String>(
                        vals.length);
                StringBuilder b = new StringBuilder();
                int refCount = 0;
                for (String str : vals) {
                    String strTrimmed = str.trim();
                    boolean startsWithOpenBracket = strTrimmed.startsWith("[");
                    boolean endsWithCloseBracket = strTrimmed.endsWith("]");
                    if (startsWithOpenBracket && endsWithCloseBracket) {
                        mergedInnerLists.add(strTrimmed);
                    } else if (startsWithOpenBracket) {
                        b.append(strTrimmed);
                        refCount++;
                    } else if (endsWithCloseBracket) {
                        b.append(',');
                        b.append(strTrimmed);
                        if (refCount-- == 1) {
                            mergedInnerLists.add(b.toString());
                            b.setLength(0);
                        }
                    } else if (refCount > 0) {
                        b.append(',');
                        b.append(strTrimmed);
                    } else {
                        mergedInnerLists.add(strTrimmed);
                    }
                }
                List<Value> l = new ArrayList<Value>(vals.length);
                for (String s : mergedInnerLists) {
                    if ((s.startsWith("'") && s.endsWith("'"))
                            || (s.startsWith("\"") && s.endsWith("\""))) {
                        l.add(ValueType.NOMINALVALUE.parse(s.substring(1,
                                s.length() - 1)));
                    } else {
                        l.add(ValueType.VALUE.parse(s));
                    }
                }
                return new ValueList<Value>(l);
            } else {
                return null;
            }
        }

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.VALUELIST;
        }
    },
    DATEVALUE {
        @Override
        public DateValue parse(String string) {
            DateValue result;
            if (string != null) {
                DateFormat dateFormat = AbsoluteTimeGranularity.DAY
                        .getShortFormat();
                try {
                    result = DateValue.getInstance(dateFormat.parse(string));
                    ValueUtil.logger().log(Level.WARNING,
                            "String {0} could not be parsed into a date",
                            string);
                } catch (ParseException ex) {
                    result = null;
                }
            } else {
                result = null;
            }
            return result;
        }

        @Override
        public boolean isInstance(Value value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }
            return value.getType() == ValueType.DATEVALUE;
        }
    };

    /**
     * Returns whether a value is an instance of this value type.
     * 
     * @param value
     *            a {@link Value}.
     * @return <code>true</code> or <code>false</code>.
     */
    public abstract boolean isInstance(Value value);

    /**
     * Creates a {@link Value} instance by parsing the given string.
     * 
     * @param val
     *            a <code>String</code>. May be <code>null</code>.
     * @return a <code>Value</code>, or <code>null</code> if the supplied string
     *         is <code>null</code> or has an invalid format.
     */
    public abstract Value parse(String val);
}
