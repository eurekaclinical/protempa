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
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.lang.builder.ToStringBuilder;

public final class DerivedUniqueId implements LocalUniqueId {

    private static final long serialVersionUID = -7548400029812453768L;
    private String id;
    private transient volatile int hashCode;

    public DerivedUniqueId(String newId) {
        if (newId == null) {
            throw new IllegalArgumentException("newId cannot be null");
        }
        this.id = newId;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DerivedUniqueId other = (DerivedUniqueId) obj;
        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 53 * hash + this.id.hashCode();
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public LocalUniqueId clone() {
        try {
            DerivedUniqueId clone = (DerivedUniqueId) super.clone();
            // TODO:  Do we need to copy the ID?  Or is the cloned object 
            // a new object without a proper ID?
            // clone.id = this.id;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new AssertionError(cnse);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.id);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        this.id = (String) s.readObject();
        if (this.id == null) {
            throw new InvalidObjectException("Can't restore. Null id");
        }
    }
}
