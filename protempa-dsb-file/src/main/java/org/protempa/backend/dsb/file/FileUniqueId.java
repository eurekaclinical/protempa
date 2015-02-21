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

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.LocalUniqueId;

/**
 *
 * @author Andrew Post
 */
public final class FileUniqueId implements LocalUniqueId {
    private final int colNum;
    private final int rowNum;
    private final int propNum;
    private final String propId;

    FileUniqueId(int rowNum, int colNum, String propId, int propNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.propNum = propNum;
        this.propId = propId;
    }

    public int getColNum() {
        return colNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getPropNum() {
        return propNum;
    }

    public String getPropId() {
        return propId;
    }
    
    @Override
    public LocalUniqueId clone() {
        try {
            return (FileUniqueId) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Never reached!");
        }
    }

    @Override
    public String getId() {
        return "" + this.rowNum + "^" + this.colNum + "^" + this.propId + "^" + this.propNum;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.colNum;
        hash = 47 * hash + this.rowNum;
        hash = 47 * hash + this.propNum;
        hash = 47 * hash + Objects.hashCode(this.propId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileUniqueId other = (FileUniqueId) obj;
        if (this.colNum != other.colNum) {
            return false;
        }
        if (this.rowNum != other.rowNum) {
            return false;
        }
        if (this.propNum != other.propNum) {
            return false;
        }
        if (!Objects.equals(this.propId, other.propId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

   
    
}
