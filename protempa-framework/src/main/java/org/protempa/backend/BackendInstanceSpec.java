package org.protempa.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public final class BackendInstanceSpec<B extends Backend> {
    private final BackendSpec<B> backendSpec;
    private final Map<String, Object> properties;
    private final List<BackendPropertySpec> propertySpecs;

    public BackendInstanceSpec(
            BackendSpec<B> backendSpec,
            List<BackendPropertySpec> propertySpecs) {
        assert backendSpec != null : "backendSpec cannot be null";
        assert propertySpecs != null : "info cannot be null";
        this.backendSpec = backendSpec;
        this.properties = new HashMap<String, Object>();
        this.propertySpecs = Collections.unmodifiableList(
                new ArrayList<BackendPropertySpec>(propertySpecs));
    }

    public BackendSpec<B> getBackendSpec() {
        return this.backendSpec;
    }

    public List<BackendPropertySpec> getBackendPropertySpecs() {
        return this.propertySpecs;
    }

    public void parseProperty(String name, String valueStr)
            throws InvalidPropertyNameException, InvalidPropertyValueException {
        for (BackendPropertySpec spec : this.propertySpecs) {
            if (spec.getName().equals(name)) {
                Class<?> cls = spec.getType();
                if (String.class.equals(cls))
                    setProperty(name, valueStr);
                else if (Double.class.equals(cls))
                    setProperty(name, Double.valueOf(valueStr));
                else if (Float.class.equals(cls))
                    setProperty(name, Float.valueOf(valueStr));
                else if (Integer.class.equals(cls))
                    setProperty(name, Integer.valueOf(valueStr));
                else if (Long.class.equals(cls))
                    setProperty(name, Long.valueOf(valueStr));
                else if (Boolean.class.equals(cls))
                    setProperty(name, Boolean.valueOf(valueStr));
                else
                    throw new AssertionError("name's type, " + cls.getName() 
                            + ", is invalid, must be one of "
                            + BackendPropertySpec.allowedClassesPrettyPrint());
                return;
            }
        }
    }

    public void setProperty(String name, Object value)
            throws InvalidPropertyNameException, InvalidPropertyValueException {
        for (BackendPropertySpec spec : this.propertySpecs) {
            if (spec.getName().equals(name)) {
                if (value != null && !spec.getType().isInstance(value)) {
                    throw new IllegalArgumentException("value should be " +
                            spec.getType() + " but was " + value.getClass());
                }
                spec.validate(value);
                this.properties.put(name, value);
                return;
            }
        }
        throw new InvalidPropertyNameException(name);
    }

    public Object getProperty(String name) 
            throws InvalidPropertyNameException {
        for (BackendPropertySpec spec : this.propertySpecs) {
            if (spec.getName().equals(name)) {
                return this.properties.get(name);
            }
        }
        throw new InvalidPropertyNameException(name);
    }

    /**
     * Instantiates a new {@link Backend} and calls
     * its {@link Backend#initialize} method before returning it.
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
