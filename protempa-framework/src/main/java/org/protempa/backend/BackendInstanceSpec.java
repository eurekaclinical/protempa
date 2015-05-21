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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public final class BackendInstanceSpec<B extends Backend> {

    private final BackendSpec<B> backendSpec;
    private String configurationsId;
    private final Map<String, Object> properties;
    private final Map<String, BackendPropertySpec> propertyMap;
    private final Set<String> propertyRequiredOverrides;
    private final Map<String, String> propertyDisplayNameOverrides;

    BackendInstanceSpec(BackendSpec<B> backendSpec) {
        assert backendSpec != null : "backendSpec cannot be null";
        this.backendSpec = backendSpec;
        this.properties = new HashMap<>();
        this.propertyMap = new HashMap<>();
        for (BackendPropertySpec bps : backendSpec.getPropertySpecs()) {
            this.propertyMap.put(bps.getName(), bps);
        }
        this.propertyRequiredOverrides = new HashSet<>();
        this.propertyDisplayNameOverrides = new HashMap<>();
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
            spec.getType().parseProperty(this, spec, valueStr);
        } else {
            throw new InvalidPropertyNameException(name);
        }
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
        if (spec.getType().getCls().isArray()) {
            addArrayProperty(spec, value);
        } else {
            spec.validate(value);
            this.properties.put(spec.getName(), value);
        }
    }
    
    public Object getProperty(String name)
            throws InvalidPropertyNameException {
        BackendPropertySpec get = this.propertyMap.get(name);
        if (get == null) {
            throw new InvalidPropertyNameException(name);
        } else {
            Object value = this.properties.get(name);
            if (value != null && value instanceof List) {
                List<?> l = (List<?>) value;
                Object result = Array.newInstance(get.getType().getCls().getComponentType(), l.size());
                for (int i = 0, n = l.size(); i < n; i++) {
                    Array.set(result, i, l.get(i));
                }
                return result;
            } else {
                return this.properties.get(name);
            }
        }
    }

    public String[] getPropertyNames() {
        return this.properties.keySet().toArray(new String[this.properties.size()]);
    }
    
    public void addRequiredOverride(String propertyName) 
            throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        this.propertyRequiredOverrides.add(propertyName);
    }
    
    public void removeRequiredOverride(String propertyName) throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        this.propertyRequiredOverrides.remove(propertyName);
    }
    
    public boolean isRequired(String propertyName) throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        return this.propertyRequiredOverrides.contains(propertyName) || this.propertyMap.get(propertyName).isRequired();
    }
    
    public void addDisplayNameOverride(String propertyName, String displayName) 
            throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        this.propertyDisplayNameOverrides.put(propertyName, displayName);
    }
    
    public void removeDisplayNameOverride(String propertyName) throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        this.propertyDisplayNameOverrides.remove(propertyName);
    }
    
    public String getDisplayName(String propertyName) throws InvalidPropertyNameException {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (!this.propertyMap.containsKey(propertyName)) {
            throw new InvalidPropertyNameException(propertyName);
        }
        return this.propertyDisplayNameOverrides.containsKey(propertyName) ? 
                this.propertyDisplayNameOverrides.get(propertyName) : 
                this.propertyMap.get(propertyName).getDisplayName();
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

    private void addArrayProperty(BackendPropertySpec spec, Object value) throws InvalidPropertyValueException {
        if (spec == null) {
            throw new IllegalArgumentException("spec cannot be null");
        }
        spec.validate(value);
        List<Object> lValue = (List<Object>) this.properties.get(spec.getName());
        if (lValue == null) {
            List<Object> l = new ArrayList<>();
            l.add(value);
            this.properties.put(spec.getName(), l);
        } else {
            lValue.add(value);
        }
    }
}
