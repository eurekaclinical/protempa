package org.protempa;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;

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

/**
 *
 * @author Andrew Post
 */
public final class Attribute implements Serializable {
    private static final long serialVersionUID = 1;
    
    private final String name;
    private final Value value;

    public Attribute(String name, Value value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public Value getValue() {
        return value;
    }
    
    public AttributeBuilder asBuilder() {
        AttributeBuilder builder = new AttributeBuilder();
        builder.setName(this.name);
        builder.setValueBuilder(this.value.asBuilder());
        return builder;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
