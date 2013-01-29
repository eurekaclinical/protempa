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
package org.protempa;

import org.apache.commons.lang.builder.ToStringBuilder;

public final class DerivedDataSourceType extends DataSourceType {
    
    private static class DerivedDataSourceTypeContainer {
        private static DerivedDataSourceType INSTANCE = 
                new DerivedDataSourceType();
    }

    public static DerivedDataSourceType getInstance() {
        return DerivedDataSourceTypeContainer.INSTANCE;
    }

    private DerivedDataSourceType() {

    }

    @Override
    public boolean isDerived() {
        return true;
    }

    @Override
    public String getStringRepresentation() {
        return "Derived";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
