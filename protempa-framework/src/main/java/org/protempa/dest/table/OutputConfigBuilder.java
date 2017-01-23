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
package org.protempa.dest.table;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * A bean-like class for creating {@link OutputConfig} configuration objects.
 *
 * @author Andrew Post
 */
public final class OutputConfigBuilder {

    private boolean showValue;
    private boolean showDisplayName;
    private boolean showAbbrevDisplayName;
    private boolean showStartOrTimestamp;
    private boolean showFinish;
    private boolean showLength;
    private boolean showId;
    private boolean showUniqueId;
    private String valueHeading;
    private String displayNameHeading;
    private String abbrevDisplayNameHeading;
    private String startOrTimestampHeading;
    private String finishHeading;
    private String lengthHeading;
    private String idHeading;
    private String uniqueIdHeading;
    private Map<String, String> propertyHeadings;
    private DateFormat dateFormat;

    public OutputConfigBuilder() {
        reset();
    }

    public void reset() {
        this.valueHeading = "";
        this.displayNameHeading = "";
        this.abbrevDisplayNameHeading = "";
        this.startOrTimestampHeading = "";
        this.finishHeading = "";
        this.lengthHeading = "";
        this.idHeading = "";
        this.uniqueIdHeading = "";
        this.showValue = false;
        this.showDisplayName = false;
        this.showAbbrevDisplayName = false;
        this.showStartOrTimestamp = false;
        this.showFinish = false;
        this.showLength = false;
        this.showId = false;
        this.showUniqueId = false;
        this.propertyHeadings = new HashMap<>();
        this.dateFormat = null;
    }

    public String getIdHeading() {
        return this.idHeading;
    }
    
    public void setIdHeading(String idHeading) {
        if (idHeading == null) {
            idHeading = "";
        }
        this.idHeading = idHeading;
    }

    public String getUniqueIdHeading() {
        return uniqueIdHeading;
    }

    public void setUniqueIdHeading(String uniqueIdHeading) {
        this.uniqueIdHeading = uniqueIdHeading;
    }

    public String getValueHeading() {
        return valueHeading;
    }

    public void setValueHeading(String valueHeading) {
        if (valueHeading == null) {
            valueHeading = "";
        }
        this.valueHeading = valueHeading;
    }

    public String getDisplayNameHeading() {
        return displayNameHeading;
    }

    public void setDisplayNameHeading(String displayNameHeading) {
        if (displayNameHeading == null) {
            displayNameHeading = "";
        }
        this.displayNameHeading = displayNameHeading;
    }

    public String getAbbrevDisplayNameHeading() {
        return abbrevDisplayNameHeading;
    }

    public void setAbbrevDisplayNameHeading(String abbrevDisplayNameHeading) {
        if (abbrevDisplayNameHeading == null) {
            abbrevDisplayNameHeading = "";
        }
        this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
    }

    public String getStartOrTimestampHeading() {
        return startOrTimestampHeading;
    }

    public void setStartOrTimestampHeading(String startOrTimestampHeading) {
        if (startOrTimestampHeading == null) {
            startOrTimestampHeading = "";
        }
        this.startOrTimestampHeading = startOrTimestampHeading;
    }

    public String getFinishHeading() {
        return finishHeading;
    }

    public void setFinishHeading(String finishHeading) {
        if (finishHeading == null) {
            finishHeading = "";
        }
        this.finishHeading = finishHeading;
    }

    public String getLengthHeading() {
        return lengthHeading;
    }

    public void setLengthHeading(String lengthHeading) {
        if (lengthHeading == null) {
            lengthHeading = "";
        }
        this.lengthHeading = lengthHeading;
    }

    public boolean showId() {
        return this.showId;
    }

    public void setShowId(boolean showId) {
        this.showId = showId;
    }
    
    public boolean showUniqueId() {
        return this.showUniqueId;
    }
    
    public void setShowUniqueId(boolean showUniqueId) {
        this.showUniqueId = showUniqueId;
    }

    public boolean showValue() {
        return showValue;
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
    }

    public boolean showDisplayName() {
        return showDisplayName;
    }

    public void setShowDisplayName(boolean showDisplayName) {
        this.showDisplayName = showDisplayName;
    }

    public boolean showAbbrevDisplayName() {
        return showAbbrevDisplayName;
    }

    public void setShowAbbrevDisplayName(boolean showAbbrevDisplayName) {
        this.showAbbrevDisplayName = showAbbrevDisplayName;
    }

    public boolean showStartOrTimestamp() {
        return showStartOrTimestamp;
    }

    public void setShowStartOrTimestamp(boolean showStartOrTimestamp) {
        this.showStartOrTimestamp = showStartOrTimestamp;
    }

    public boolean showFinish() {
        return showFinish;
    }

    public void setShowFinish(boolean showFinish) {
        this.showFinish = showFinish;
    }

    public boolean showLength() {
        return showLength;
    }

    public void setShowLength(boolean showLength) {
        this.showLength = showLength;
    }

    public HashMap<String, String> getPropertyHeadings() {
        return new HashMap<>(this.propertyHeadings);
    }

    public String getPropertyHeading(String propertyName) {
        return this.propertyHeadings.get(propertyName);
    }

    public void setPropertyHeadings(HashMap<String, String> propertyHeadings) {
        if (propertyHeadings == null) {
            this.propertyHeadings = new HashMap<>();
        } else {
            this.propertyHeadings = propertyHeadings;
        }
    }

    public void putPropertyHeading(String propertyName, String propertyHeading) {
        this.propertyHeadings.put(propertyName, propertyHeading);
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Creates a new {@link OutputConfig} instance.
     *
     * @return a {@link OutputConfig}.
     */
    public OutputConfig build() {
        return new OutputConfig(this.showId, this.showValue, this.showDisplayName,
                this.showAbbrevDisplayName, this.showStartOrTimestamp,
                this.showFinish, this.showLength, this.showUniqueId,
                this.idHeading, this.valueHeading,
                this.displayNameHeading, this.abbrevDisplayNameHeading,
                this.startOrTimestampHeading, this.finishHeading,
                this.lengthHeading, this.uniqueIdHeading, 
                this.propertyHeadings, this.dateFormat);
    }
}
