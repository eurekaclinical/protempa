package org.protempa.backend;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andrew Post
 */
public enum BackendPropertyType {

    STRING(String.class) {

                public boolean isInstance(Object val) {
                    return val instanceof String;
                }

                @Override
                public Object parse(String propertyName, String value) {
                    return value;
                }

            },
    BOOLEAN(Boolean.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Boolean;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Boolean.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    INTEGER(Integer.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Integer;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Integer.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    LONG(Long.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Long;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Long.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    FLOAT(Float.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Float;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Float.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    DOUBLE(Double.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Double;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Double.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    CHARACTER(Character.class) {

                public boolean isInstance(Object val) {
                    return val instanceof Character;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    if (value.length() > 1) {
                        throw new InvalidPropertyValueException(propertyName, value, null);
                    }
                    return value.charAt(0);
                }

            },
    STRING_ARRAY(String[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof String;
                }

                @Override
                public Object parse(String propertyName, String value) {
                    return value;
                }

            },
    DOUBLE_ARRAY(Double[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Double;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Double.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    FLOAT_ARRAY(Float[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Float;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Float.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    INTEGER_ARRAY(Integer[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Integer;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Integer.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    LONG_ARRAY(Long[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Long;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Long.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    BOOLEAN_ARRAY(Boolean[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Boolean;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    try {
                        return Boolean.valueOf(value);
                    } catch (NumberFormatException ex) {
                        throw new InvalidPropertyValueException(propertyName, value, ex);
                    }
                }

            },
    CHARACTER_ARRAY(Character[].class) {

                public boolean isInstance(Object val) {
                    return val instanceof Character;
                }

                @Override
                public Object parse(String propertyName, String value) throws InvalidPropertyValueException {
                    if (value.length() > 1) {
                        throw new InvalidPropertyValueException(propertyName, value, null);
                    }
                    return value.charAt(0);
                }

            };

    private static final Map<Class<?>, BackendPropertyType> clsToEnum = new HashMap<>();

    static {
        for (BackendPropertyType elt : values()) {
            clsToEnum.put(elt.getCls(), elt);
        }
    }

    public static BackendPropertyType fromCls(Class<?> cls) {
        return clsToEnum.get(cls);
    }

    public static boolean isAllowed(Class<?> cls) {
        return clsToEnum.containsKey(cls);
    }

    private Class<?> cls;

    private BackendPropertyType(Class<?> cls) {
        this.cls = cls;
    }

    public abstract boolean isInstance(Object val);

    public Class<?> getCls() {
        return this.cls;
    }

    public abstract Object parse(String propertyName, String value) throws InvalidPropertyValueException;

    public void setProperty(BackendInstanceSpec instanceSpec, BackendPropertySpec propertySpec, Object value) throws InvalidPropertyValueException {
        instanceSpec.setProperty(propertySpec, value);
    }

    public void parseProperty(BackendInstanceSpec instanceSpec, BackendPropertySpec propertySpec, String value) throws InvalidPropertyValueException {
        setProperty(instanceSpec, propertySpec, parse(propertySpec.getName(), value));
    }

}
