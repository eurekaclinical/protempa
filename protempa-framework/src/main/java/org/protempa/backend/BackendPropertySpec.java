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
import org.arp.javautil.arrays.Arrays;

/**
 *
 * @author Andrew Post
 */
public final class BackendPropertySpec {
    static final Class<?>[] ALLOWED_CLASSES = {
        String.class,
        Boolean.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class
    };

    static String allowedClassesPrettyPrint() {
        String[] allowedClassesStringArray = new String[ALLOWED_CLASSES.length];
        for (int i = 0; i < ALLOWED_CLASSES.length; i++)
            allowedClassesStringArray[i] = ALLOWED_CLASSES[i].getName();
        return StringUtils.join(allowedClassesStringArray, ", ");
    }

    private final String name;
//    private final String displayName;
    private final String description;
    private final Class<?> type;
    private final BackendPropertyValidator validator;

    public BackendPropertySpec(String name,
            String displayName,
            String description, Class<?> type,
            BackendPropertyValidator validator) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        if (!Arrays.contains(ALLOWED_CLASSES, type))
            throw new IllegalArgumentException("type must be one of " +
                    allowedClassesPrettyPrint() + " but was " + type);
        this.name = name;
//        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.validator = validator;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void validate(Object value) throws InvalidPropertyValueException {
        if (this.validator != null)
            this.validator.validate(this.name, value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
