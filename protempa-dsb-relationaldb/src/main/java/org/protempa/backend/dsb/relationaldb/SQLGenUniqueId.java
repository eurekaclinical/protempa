/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.LocalUniqueId;

final class SQLGenUniqueId implements LocalUniqueId {

    private static final long serialVersionUID = 3956023315666447630L;
    private String entitySpecName;
    private String[] dbIds;
    private String id;
    private volatile int hashCode;
    private long numericalId;

    SQLGenUniqueId(String entitySpecName, String[] dbIds) {
        assert entitySpecName != null : "entitySpecName cannot be null";
        assert dbIds != null : "dbIds cannot be null";
        assert !ArrayUtils.contains(dbIds, null) :
            "dbIds cannot contain a null element";
        
        //The entity spec name is already interned, so don't bother redoing it.
        this.entitySpecName = entitySpecName;
        
        this.dbIds = dbIds.clone();
        if (this.dbIds.length == 1) {
            try {
                this.numericalId = Long.parseLong(this.dbIds[0]);
            } catch (NumberFormatException ex) {
                this.numericalId = 1L;
            }
        } else {
            this.numericalId = 1L;
        }
    }
    
    String[] getDbIds() {
        return this.dbIds.clone();
    }

    @Override
    public String getId() {
        if (this.id == null) {
            StringBuilder builder = new StringBuilder();
            builder.append(entitySpecName);
            for (String dbId : dbIds) {
                builder.append('^');
                builder.append(dbId);
            }
            this.id = builder.toString();
        }
        return this.id;
    }

    @Override
    public long getNumericalId() {
        return this.numericalId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SQLGenUniqueId other = (SQLGenUniqueId) obj;
        if (!this.entitySpecName.equals(other.entitySpecName)) {
            return false;
        }
        if (!Arrays.equals(this.dbIds, other.dbIds)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 53 * hash + this.entitySpecName.hashCode();
            hash = 53 * hash + Arrays.hashCode(this.dbIds);
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public LocalUniqueId clone() {
        try {
            return (SQLGenUniqueId) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Never reached!");
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.entitySpecName);
        s.writeInt(this.dbIds.length);
        for (String dbId : this.dbIds) {
            s.writeObject(dbId);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        this.entitySpecName = (String) s.readObject();
        if (this.entitySpecName == null) {
            throw new InvalidObjectException(
                    "name cannot be null. Can't restore");
        }
        
        // We intern entity spec names elsewhere, so let's do it here too.
        this.entitySpecName = this.entitySpecName.intern();
        
        int dbIdsLen = s.readInt();
        if (dbIdsLen < 0) {
            throw new InvalidObjectException("dbIds length invalid (" +
                    dbIdsLen + "). Can't restore");
        }
        this.dbIds = new String[dbIdsLen];
        for (int i = 0; i < dbIdsLen; i++) {
            String dbId = (String) s.readObject();
            if (dbId == null) {
                throw new InvalidObjectException(
                        "dbIds cannot contain a null value. Can't restore");
            }
            this.dbIds[i] = dbId;
        }
    }
}
