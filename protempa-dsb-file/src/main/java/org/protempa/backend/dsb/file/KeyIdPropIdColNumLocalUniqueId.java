package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
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

import org.protempa.proposition.LocalUniqueId;

/**
 *
 * @author Andrew Post
 */
public class KeyIdPropIdColNumLocalUniqueId implements LocalUniqueId {
    private final int colNum;
    private final String propId;
    private final String keyId;
    private final String id;

    KeyIdPropIdColNumLocalUniqueId(String keyId, String propId, int colNum) {
        assert keyId != null : "keyId cannot be null";
        assert propId != null : "propId cannot be null";
        this.keyId = keyId;
        this.propId = propId;
        this.colNum = colNum;
        this.id = this.keyId + "^" + this.propId + "^" + this.colNum;
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getNumericalId() {
        return 1;
    }

    @Override
    public LocalUniqueId clone() {
        try {
            return (LocalUniqueId) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Never reached!");
        }
    }
    
}
