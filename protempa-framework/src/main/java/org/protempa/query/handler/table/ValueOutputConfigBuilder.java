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
package org.protempa.query.handler.table;

public final class ValueOutputConfigBuilder {
    private String propertyValueDisplayName;
    private String propertyValueAbbrevDisplayName;
    private boolean showPropertyValueDisplayName;
    private boolean showPropertyValueAbbrevDisplayName;

    public ValueOutputConfigBuilder() {
        reset();
    }

    public String getPropertyValueDisplayName() {
        return propertyValueDisplayName;
    }

    public void setPropertyValueDisplayName(String propertyValueDisplayName) {
        if (propertyValueDisplayName == null) {
            propertyValueDisplayName = "";
        }
        this.propertyValueDisplayName = propertyValueDisplayName;
    }

    public String getPropertyValueAbbrevDisplayName() {
        return propertyValueAbbrevDisplayName;
    }

    public void setPropertyValueAbbrevDisplayName(
            String propertyValueAbbrevDisplayName) {
        if (propertyValueAbbrevDisplayName == null) {
            propertyValueAbbrevDisplayName = "";
        }
        this.propertyValueAbbrevDisplayName = propertyValueAbbrevDisplayName;
    }

    public boolean isShowPropertyValueDisplayName() {
        return showPropertyValueDisplayName;
    }

    public void setShowPropertyValueDisplayName(
            boolean showPropertyValueDisplayName) {
        this.showPropertyValueDisplayName = showPropertyValueDisplayName;
    }

    public boolean isShowPropertyValueAbbrevDisplayName() {
        return showPropertyValueAbbrevDisplayName;
    }

    public void setShowPropertyValueAbbrevDisplayName(
            boolean showPropertyValueAbbrevDisplayName) {
        this.showPropertyValueAbbrevDisplayName = showPropertyValueAbbrevDisplayName;
    }

    /**
     * Creates a new {@link ValueOutputConfig} instance.
     * 
     * @return a {@link ValueOutputConfig}.
     */
    public ValueOutputConfig build() {
        return new ValueOutputConfig(this.showPropertyValueDisplayName,
                this.showPropertyValueAbbrevDisplayName,
                this.propertyValueDisplayName,
                this.propertyValueAbbrevDisplayName);
    }

    public void reset() {
        this.propertyValueAbbrevDisplayName = "";
        this.propertyValueDisplayName = "";
        this.showPropertyValueAbbrevDisplayName = false;
        this.showPropertyValueDisplayName = false;
    }
}
