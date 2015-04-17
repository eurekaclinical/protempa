/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.valueset;

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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.Attribute;
import org.protempa.AttributeBuilder;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.value.Value;

/**
 * For specifying the values of a value set.
 */
public final class ValueSetElement {
    private static final Attribute[] EMPTY_ATTRIBUTE_ARR = new Attribute[0];
    private final Value value;
    private final String displayName;
    private final String abbrevDisplayName;
    private final Attribute[] attributes;
    
    public ValueSetElement(Value value) {
        this(value, null, null);
    }
    
    public ValueSetElement(Value value, String displayName) {
        this(value, displayName, null);
    }
    
    public ValueSetElement(Value value, String displayName, String abbrevDisplayName) {
        this(value, displayName, abbrevDisplayName, null);
    }

    /**
     * Instantiates a value of a value set.
     *
     * @param value the {@link Value}. Cannot be <code>null</code>.
     * @param displayName the value's display name {@link String}. If
     * <code>null</code> is specified, {@link #getDisplayName()} will
     * return the empty string.
     * @param abbrevDisplayName the value's abbreviated display name
     * {@link String}. If <code>null</code> is specified,
     * {@link #getAbbrevDisplayName()} will return the empty string.
     */
    public ValueSetElement(Value value, String displayName, String abbrevDisplayName, Attribute[] attributes) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (displayName == null) {
            displayName = "";
        } else {
            displayName = displayName.intern();
        }
        if (abbrevDisplayName == null) {
            abbrevDisplayName = "";
        } else {
            abbrevDisplayName = abbrevDisplayName.intern();
        }
        this.value = value;
        this.displayName = displayName;
        this.abbrevDisplayName = abbrevDisplayName;
        if (attributes != null) {
            ProtempaUtil.checkArrayForNullElement(attributes, "attributes");
            this.attributes = attributes.clone();
        } else {
            this.attributes = EMPTY_ATTRIBUTE_ARR;
        }
        
    }

    /**
     * Returns the value's abbreviated display name. Guaranteed not
     * <code>null</code>.
     *
     * @return a {@link String}.
     */
    public String getAbbrevDisplayName() {
        return abbrevDisplayName;
    }

    /**
     * Returns the value's display name. Guaranteed not <code>null</code>.
     *
     * @return a {@link String}.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the value. Guaranteed not <code>null</code>.
     *
     * @return a {@link Value}.
     */
    public Value getValue() {
        return value;
    }

    public Attribute[] getAttributes() {
        return attributes.clone();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    public ValueSetElementBuilder asBuilder() {
        ValueSetElementBuilder builder = new ValueSetElementBuilder();
        builder.setAbbrevDisplayName(this.abbrevDisplayName);
        builder.setDisplayName(this.displayName);
        builder.setValueBuilder(this.value.asBuilder());
        AttributeBuilder[] builders = new AttributeBuilder[this.attributes.length];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = this.attributes[i].asBuilder();
        }
        builder.setAttributeBuilders(builders);
        return builder;
    }
    
}
