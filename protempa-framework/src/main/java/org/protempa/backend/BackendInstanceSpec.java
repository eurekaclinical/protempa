/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.backend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;

/**
 *
 * @author Andrew Post
 */
public final class BackendInstanceSpec<B extends Backend> {

    private final BackendSpec<B> backendSpec;
    private String configurationsId;
    private final Map<String, Object> properties;
    private final List<BackendPropertySpec> propertySpecs;
    private final Map<String, BackendPropertySpec> propertyMap;

    BackendInstanceSpec(
            BackendSpec<B> backendSpec,
            List<BackendPropertySpec> propertySpecs) {
        assert backendSpec != null : "backendSpec cannot be null";
        assert propertySpecs != null : "info cannot be null";
        this.backendSpec = backendSpec;
        this.properties = new HashMap<>();
        this.propertySpecs = Collections.unmodifiableList(
                new ArrayList<>(propertySpecs));
        this.propertyMap = new HashMap<>();
        for (BackendPropertySpec bps : this.propertySpecs) {
            this.propertyMap.put(bps.getName(), bps);
        }
    }

    public BackendSpec<B> getBackendSpec() {
        return this.backendSpec;
    }

    public void setConfigurationsId(String configurationsId) {
        this.configurationsId = configurationsId;
    }

    public String getConfigurationsId() {
        return this.configurationsId;
    }

    public List<BackendPropertySpec> getBackendPropertySpecs() {
        return this.propertySpecs;
    }

    public void parseProperty(String name, String valueStr)
            throws InvalidPropertyNameException, InvalidPropertyValueException {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (valueStr == null) {
            throw new IllegalArgumentException("valueStr cannot be null");
        }
        BackendPropertySpec spec = this.propertyMap.get(name);
        if (spec != null) {
            Class cls = spec.getType();
            try {
                if (String.class.equals(cls)) {
                    setProperty(spec, valueStr);
                } else if (Double.class.equals(cls)) {
                    setProperty(spec, Double.valueOf(valueStr));
                } else if (Float.class.equals(cls)) {
                    setProperty(spec, Float.valueOf(valueStr));
                } else if (Integer.class.equals(cls)) {
                    setProperty(spec, Integer.valueOf(valueStr));
                } else if (Long.class.equals(cls)) {
                    setProperty(spec, Long.valueOf(valueStr));
                } else if (Boolean.class.equals(cls)) {
                    setProperty(spec, Boolean.valueOf(valueStr));
                } else if (Character.class.equals(cls)) {
                    if (valueStr.length() > 1) {
                        throw new InvalidPropertyValueException(valueStr);
                    }
                    setProperty(spec, valueStr.charAt(0));
                } else if (String[].class.equals(cls)) {
                    addProperty(spec, valueStr, String.class);
                } else if (Double[].class.equals(cls)) {
                    addProperty(spec, Double.valueOf(valueStr), Double.class);
                } else if (Float[].class.equals(cls)) {
                    addProperty(spec, Float.valueOf(valueStr), Float.class);
                } else if (Integer[].class.equals(cls)) {
                    addProperty(spec, Integer.valueOf(valueStr), Integer.class);
                } else if (Long[].class.equals(cls)) {
                    addProperty(spec, Long.valueOf(valueStr), Long.class);
                } else if (Boolean[].class.equals(cls)) {
                    addProperty(spec, Boolean.valueOf(valueStr), Boolean.class);
                } else if (Character[].class.equals(cls)) {
                    if (valueStr.length() > 1) {
                        throw new InvalidPropertyValueException(valueStr);
                    }
                    addProperty(spec, valueStr.charAt(0), Character.class);
                } else {
                    throw new AssertionError("name's type, " + cls.getName()
                            + ", is invalid, must be one of "
                            + BackendPropertySpec.allowedClassesPrettyPrint());
                }
            } catch (InvalidPropertyValueException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new InvalidPropertyValueException(valueStr, ex);
            }
        } else {
            throw new InvalidPropertyNameException(name);
        }
    }

    public <E> void addProperty(String name, E value, Class<E> cls) throws InvalidPropertyNameException, InvalidPropertyValueException {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        BackendPropertySpec spec = this.propertyMap.get(name);
        if (spec == null) {
            throw new InvalidPropertyNameException(name);
        }
        addProperty(spec, value, cls);
    }

    public <E> void addProperty(BackendPropertySpec spec, E value, Class<E> cls) throws InvalidPropertyValueException {
        if (spec == null) {
            throw new IllegalArgumentException("spec cannot be null");
        }
        if (cls == null) {
            throw new IllegalArgumentException("cls cannot be null");
        }
        if (value != null) {
            Class<?> componentType = spec.getType().getComponentType();
            if (componentType == null) {
                throw new AssertionError("property must have an array type");
            }
            if (!componentType.isInstance(value)) {
                throw new IllegalArgumentException("value should be "
                        + componentType + " but was " + value.getClass());
            }
        }
        spec.validate(value);
        Object arrValue = this.properties.get(spec.getName());
        if (arrValue == null) {
            E[] arrValue2 = (E[]) Array.newInstance(cls, 1);
            arrValue2[0] = value;
            arrValue = arrValue2;
        } else {
            List<E> lValue = Arrays.asList((E[]) arrValue);
            lValue.add(value);
            arrValue = lValue.toArray((E[]) Array.newInstance(cls, lValue.size()));
        }
        this.properties.put(spec.getName(), arrValue);
    }

    public void setProperty(String name, Object value) throws InvalidPropertyNameException, InvalidPropertyValueException {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        BackendPropertySpec spec = this.propertyMap.get(name);
        if (spec == null) {
            throw new InvalidPropertyNameException(name);
        }
        setProperty(spec, value);
    }

    public void setProperty(BackendPropertySpec spec, Object value)
            throws InvalidPropertyValueException {
        if (spec == null) {
            throw new IllegalArgumentException("spec cannot be null");
        }
        if (value != null && !spec.getType().isInstance(value)) {
            throw new IllegalArgumentException("value should be "
                    + spec.getType() + " but was " + value.getClass());
        }
        spec.validate(value);
        this.properties.put(spec.getName(), value);
    }

    public Object getProperty(String name)
            throws InvalidPropertyNameException {
        BackendPropertySpec get = this.propertyMap.get(name);
        if (get == null) {
            throw new InvalidPropertyNameException(name);
        } else {
            return this.properties.get(name);
        }
    }

    /**
     * Instantiates a new {@link Backend} and calls its
     * {@link Backend#initialize} method before returning it.
     *
     * @return
     * @throws org.protempa.BackendInitializationException
     * @throws org.protempa.backend.BackendNewInstanceException
     */
    public B getInstance()
            throws BackendInitializationException, BackendNewInstanceException {
        B backend = backendSpec.newBackendInstance();
        backend.initialize(this);
        return backend;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
