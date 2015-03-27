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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public final class BackendPropertySpec {

    static String allowedClassesPrettyPrint() {
        return StringUtils.join(BackendPropertyType.values(), ", ");
    }

    private final String name;
    private final String displayName;
    private final String description;
    private final BackendPropertyType type;
    private final boolean required;
    private final BackendPropertyValidator validator;

    public BackendPropertySpec(String name,
            String displayName,
            String description, BackendPropertyType type, boolean required,
            BackendPropertyValidator validator) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.validator = validator;
        this.required = required;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BackendPropertyType getType() {
        return this.type;
    }

    public boolean isRequired() {
        return required;
    }

    public BackendPropertyValidator getValidator() {
        return validator;
    }

    /**
     * Checks if the value null but required, then checks if the value has the
     * right type, then calls the specified validator's 
     * {@link BackendPropertyValidator#validate(java.lang.String, java.lang.Object) } method.
     * @param value a value.
     * @throws InvalidPropertyValueException 
     */
    public void validate(Object value) throws InvalidPropertyValueException {
        if (value != null && !this.type.isInstance(value)) {
            throw new InvalidPropertyValueException("wrong type: expected " + this.type.getCls() + " but was " + value.getClass());
        }
        if (this.validator != null) {
            this.validator.validate(this.name, value);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
