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
package org.protempa;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public final class ValueClassification implements Serializable {
    private static final long serialVersionUID = 1L;
    final String value;
    final String lladId;
    final String lladValue;

    public ValueClassification(String value, String lowLevelAbstractionId, String lowLevelAbstractionValue) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (lowLevelAbstractionId == null) {
            throw new IllegalArgumentException("lowLevelAbstractionId cannot be null");
        }
        if (lowLevelAbstractionValue == null) {
            throw new IllegalArgumentException("lowLevelAbstractionValue cannot be null");
        }
        this.value = value;
        this.lladId = lowLevelAbstractionId;
        this.lladValue = lowLevelAbstractionValue;
    }

    public String getValue() {
        return value;
    }

    public String getLowLevelAbstractionId() {
        return lladId;
    }

    public String getLowLevelAbstractionValue() {
        return lladValue;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if (value == null) {
            throw new InvalidObjectException("id cannot be null");
        }
        if (lladId == null) {
            throw new InvalidObjectException("lowLevelAbstractionId cannot be null");
        }
        if (lladValue == null) {
            throw new InvalidObjectException("lowLevelValueDefName cannot be null");
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
