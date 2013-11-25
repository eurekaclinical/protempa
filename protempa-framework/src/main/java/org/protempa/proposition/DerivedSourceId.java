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
package org.protempa.proposition;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class DerivedSourceId extends SourceId {

    private static final int hashCode = 31;

    private static class DerivedSourceIdContainer {
        private static final DerivedSourceId derivedSourceId =
                new DerivedSourceId();
    }

    public static DerivedSourceId getInstance() {
        return DerivedSourceIdContainer.derivedSourceId;
    }


    private DerivedSourceId() {
    }

    @Override
    String getId() {
        return "DERIVED";
    }

    @Override
    public int hashCode() {
        return hashCode;
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
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
