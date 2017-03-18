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
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable class for configurating {@link TableColumnSpec}s.
 *
 * @author Himanshu Rathod
 */
public final class OutputConfig {

    public static final class Builder {

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

        public Builder() {
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

        public Builder idHeading(String idHeading) {
            if (idHeading == null) {
                idHeading = "";
            }
            this.idHeading = idHeading;
            return this;
        }

        public String getUniqueIdHeading() {
            return uniqueIdHeading;
        }

        public Builder uniqueIdHeading(String uniqueIdHeading) {
            this.uniqueIdHeading = uniqueIdHeading;
            return this;
        }

        public String getValueHeading() {
            return valueHeading;
        }

        public Builder valueHeading(String valueHeading) {
            if (valueHeading == null) {
                valueHeading = "";
            }
            this.valueHeading = valueHeading;
            return this;
        }

        public String getDisplayNameHeading() {
            return displayNameHeading;
        }

        public Builder displayNameHeading(String displayNameHeading) {
            if (displayNameHeading == null) {
                displayNameHeading = "";
            }
            this.displayNameHeading = displayNameHeading;
            return this;
        }

        public String getAbbrevDisplayNameHeading() {
            return abbrevDisplayNameHeading;
        }

        public Builder abbrevDisplayNameHeading(String abbrevDisplayNameHeading) {
            if (abbrevDisplayNameHeading == null) {
                abbrevDisplayNameHeading = "";
            }
            this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
            return this;
        }

        public String getStartOrTimestampHeading() {
            return startOrTimestampHeading;
        }

        public Builder startOrTimestampHeading(String startOrTimestampHeading) {
            if (startOrTimestampHeading == null) {
                startOrTimestampHeading = "";
            }
            this.startOrTimestampHeading = startOrTimestampHeading;
            return this;
        }

        public String getFinishHeading() {
            return finishHeading;
        }

        public Builder finishHeading(String finishHeading) {
            if (finishHeading == null) {
                finishHeading = "";
            }
            this.finishHeading = finishHeading;
            return this;
        }

        public String getLengthHeading() {
            return lengthHeading;
        }

        public Builder lengthHeading(String lengthHeading) {
            if (lengthHeading == null) {
                lengthHeading = "";
            }
            this.lengthHeading = lengthHeading;
            return this;
        }

        public boolean getShowId() {
            return this.showId;
        }

        public Builder showId() {
            this.showId = true;
            return this;
        }

        public boolean getShowUniqueId() {
            return this.showUniqueId;
        }

        public Builder showUniqueId() {
            this.showUniqueId = true;
            return this;
        }

        public boolean getShowValue() {
            return showValue;
        }

        public Builder showValue() {
            this.showValue = true;
            return this;
        }

        public boolean getShowDisplayName() {
            return showDisplayName;
        }

        public Builder showDisplayName() {
            this.showDisplayName = true;
            return this;
        }

        public boolean getShowAbbrevDisplayName() {
            return showAbbrevDisplayName;
        }

        public Builder showAbbrevDisplayName() {
            this.showAbbrevDisplayName = true;
            return this;
        }

        public boolean getShowStartOrTimestamp() {
            return showStartOrTimestamp;
        }

        public Builder showStartOrTimestamp() {
            this.showStartOrTimestamp = true;
            return this;
        }

        public boolean getShowFinish() {
            return showFinish;
        }

        public Builder showFinish() {
            this.showFinish = true;
            return this;
        }

        public boolean getShowLength() {
            return showLength;
        }

        public Builder showLength() {
            this.showLength = true;
            return this;
        }

        public HashMap<String, String> getPropertyHeadings() {
            return new HashMap<>(this.propertyHeadings);
        }

        public String getPropertyHeading(String propertyName) {
            return this.propertyHeadings.get(propertyName);
        }

        public Builder propertyHeadings(HashMap<String, String> propertyHeadings) {
            if (propertyHeadings == null) {
                this.propertyHeadings = new HashMap<>();
            } else {
                this.propertyHeadings = propertyHeadings;
            }
            return this;
        }

        public Builder putPropertyHeading(String propertyName, String propertyHeading) {
            this.propertyHeadings.put(propertyName, propertyHeading);
            return this;
        }

        public DateFormat getDateFormat() {
            return dateFormat;
        }

        public Builder dateFormat(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
            return this;
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

    private final boolean showValue;
    private final boolean showDisplayName;
    private final boolean showAbbrevDisplayName;
    private final boolean showStartOrTimestamp;
    private final boolean showFinish;
    private final boolean showLength;
    private final boolean showId;
    private final boolean showUniqueId;
    private final String valueHeading;
    private final String displayNameHeading;
    private final String abbrevDisplayNameHeading;
    private final String startOrTimestampHeading;
    private final String finishHeading;
    private final String lengthHeading;
    private final String idHeading;
    private final String uniqueIdHeading;
    private final HashMap<String, String> propertyHeadings;
    private final Format positionFormat;

    public OutputConfig() {
        this.showValue = false;
        this.showDisplayName = false;
        this.showAbbrevDisplayName = false;
        this.showStartOrTimestamp = false;
        this.showFinish = false;
        this.showLength = false;
        this.showId = false;
        this.showUniqueId = false;
        this.valueHeading = "";
        this.displayNameHeading = "";
        this.abbrevDisplayNameHeading = "";
        this.startOrTimestampHeading = "";
        this.finishHeading = "";
        this.lengthHeading = "";
        this.idHeading = "";
        this.uniqueIdHeading = "";
        this.propertyHeadings = new HashMap<>();
        this.positionFormat = null;
    }

    public OutputConfig(boolean showId, boolean showValue, boolean showDisplayName,
            boolean showAbbrevDisplayName,
            boolean showStartOrTimestamp, boolean showFinish,
            boolean showLength, boolean showUniqueId, String idHeading,
            String valueHeading, String displayNameHeading,
            String abbrevDisplayNameHeading, String startOrTimestampHeading,
            String finishHeading, String lengthHeading, String uniqueIdHeading,
            Map<String, String> propertyHeadings,
            Format positionFormat) {
        this.showId = showId;
        this.showUniqueId = showUniqueId;
        this.showValue = showValue;
        this.showDisplayName = showDisplayName;
        this.showAbbrevDisplayName = showAbbrevDisplayName;
        this.showStartOrTimestamp = showStartOrTimestamp;
        this.showFinish = showFinish;
        this.showLength = showLength;
        if (idHeading == null) {
            idHeading = "";
        }
        this.idHeading = idHeading;
        if (valueHeading == null) {
            valueHeading = "";
        }
        this.valueHeading = valueHeading;
        if (displayNameHeading == null) {
            displayNameHeading = "";
        }
        this.displayNameHeading = displayNameHeading;
        if (abbrevDisplayNameHeading == null) {
            abbrevDisplayNameHeading = "";
        }
        this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
        if (startOrTimestampHeading == null) {
            startOrTimestampHeading = "";
        }
        this.startOrTimestampHeading = startOrTimestampHeading;
        if (finishHeading == null) {
            finishHeading = "";
        }
        this.finishHeading = finishHeading;
        if (lengthHeading == null) {
            lengthHeading = "";
        }
        this.lengthHeading = lengthHeading;
        if (uniqueIdHeading == null) {
            uniqueIdHeading = "";
        }
        this.uniqueIdHeading = uniqueIdHeading;
        if (propertyHeadings != null) {
            this.propertyHeadings = new HashMap<>(propertyHeadings);
        } else {
            this.propertyHeadings = new HashMap<>();
        }
        this.positionFormat = positionFormat;
    }

    public String getIdHeading() {
        return idHeading;
    }

    public String getUniqueIdHeading() {
        return uniqueIdHeading;
    }

    public String getValueHeading() {
        return valueHeading;
    }

    public String getDisplayNameHeading() {
        return displayNameHeading;
    }

    public String getAbbrevDisplayNameHeading() {
        return abbrevDisplayNameHeading;
    }

    public String getStartOrTimestampHeading() {
        return startOrTimestampHeading;
    }

    public String getFinishHeading() {
        return finishHeading;
    }

    public String getLengthHeading() {
        return lengthHeading;
    }

    public Format getPositionFormat() {
        return positionFormat;
    }

    public boolean showId() {
        return showId;
    }

    public boolean showUniqueId() {
        return showUniqueId;
    }

    public boolean showValue() {
        return showValue;
    }

    public boolean showDisplayName() {
        return showDisplayName;
    }

    public boolean showAbbrevDisplayName() {
        return showAbbrevDisplayName;
    }

    public boolean showStartOrTimestamp() {
        return showStartOrTimestamp;
    }

    public boolean showFinish() {
        return showFinish;
    }

    public boolean showLength() {
        return showLength;
    }

    public Map<String, String> getPropertyHeadings() {
        return new HashMap<>(this.propertyHeadings);
    }

    public String getPropertyHeading(String propertyName) {
        return this.propertyHeadings.get(propertyName);
    }

    public int getNumberOfColumns() {
        int i = 0;
        if (this.showId) {
            i++;
        }
        if (this.showUniqueId) {
            i++;
        }
        if (this.showAbbrevDisplayName) {
            i++;
        }
        if (this.showDisplayName) {
            i++;
        }
        if (this.showFinish) {
            i++;
        }
        if (this.showLength) {
            i++;
        }
        if (this.showStartOrTimestamp) {
            i++;
        }
        if (this.showValue) {
            i++;
        }
        i += this.propertyHeadings.size();
        return i;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((abbrevDisplayNameHeading == null) ? 0 : abbrevDisplayNameHeading.hashCode());
        result = prime * result + ((displayNameHeading == null) ? 0 : displayNameHeading.hashCode());
        result = prime * result + ((finishHeading == null) ? 0 : finishHeading.hashCode());
        result = prime * result + ((idHeading == null) ? 0 : idHeading.hashCode());
        result = prime * result + ((uniqueIdHeading == null) ? 0 : uniqueIdHeading.hashCode());
        result = prime * result + ((lengthHeading == null) ? 0 : lengthHeading.hashCode());
        result = prime * result + (showAbbrevDisplayName ? 1231 : 1237);
        result = prime * result + (showDisplayName ? 1231 : 1237);
        result = prime * result + (showFinish ? 1231 : 1237);
        result = prime * result + (showId ? 1231 : 1237);
        result = prime * result + (showUniqueId ? 1231 : 1237);
        result = prime * result + (showLength ? 1231 : 1237);
        result = prime * result + (showStartOrTimestamp ? 1231 : 1237);
        result = prime * result + (showValue ? 1231 : 1237);
        result = prime * result + ((startOrTimestampHeading == null) ? 0 : startOrTimestampHeading.hashCode());
        result = prime * result + ((valueHeading == null) ? 0 : valueHeading.hashCode());
        result = prime * result + propertyHeadings.hashCode();
        result = prime * result + ((positionFormat == null) ? 0 : positionFormat.hashCode());

        return result;
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
        OutputConfig other = (OutputConfig) obj;
        if (abbrevDisplayNameHeading == null) {
            if (other.abbrevDisplayNameHeading != null) {
                return false;
            }
        } else if (!abbrevDisplayNameHeading.equals(other.abbrevDisplayNameHeading)) {
            return false;
        }
        if (displayNameHeading == null) {
            if (other.displayNameHeading != null) {
                return false;
            }
        } else if (!displayNameHeading.equals(other.displayNameHeading)) {
            return false;
        }
        if (finishHeading == null) {
            if (other.finishHeading != null) {
                return false;
            }
        } else if (!finishHeading.equals(other.finishHeading)) {
            return false;
        }
        if (idHeading == null) {
            if (other.idHeading != null) {
                return false;
            }
        } else if (!idHeading.equals(other.idHeading)) {
            return false;
        }
        if (uniqueIdHeading == null) {
            if (other.uniqueIdHeading != null) {
                return false;
            }
        } else if (!uniqueIdHeading.equals(other.uniqueIdHeading)) {
            return false;
        }
        if (lengthHeading == null) {
            if (other.lengthHeading != null) {
                return false;
            }
        } else if (!lengthHeading.equals(other.lengthHeading)) {
            return false;
        }
        if (showAbbrevDisplayName != other.showAbbrevDisplayName) {
            return false;
        }
        if (showDisplayName != other.showDisplayName) {
            return false;
        }
        if (showFinish != other.showFinish) {
            return false;
        }
        if (showId != other.showId) {
            return false;
        }
        if (showUniqueId != other.showUniqueId) {
            return false;
        }
        if (showLength != other.showLength) {
            return false;
        }
        if (showStartOrTimestamp != other.showStartOrTimestamp) {
            return false;
        }
        if (showValue != other.showValue) {
            return false;
        }
        if (startOrTimestampHeading == null) {
            if (other.startOrTimestampHeading != null) {
                return false;
            }
        } else if (!startOrTimestampHeading.equals(other.startOrTimestampHeading)) {
            return false;
        }
        if (valueHeading == null) {
            if (other.valueHeading != null) {
                return false;
            }
        } else if (!valueHeading.equals(other.valueHeading)) {
            return false;
        }
        if (!propertyHeadings.equals(other.propertyHeadings)) {
            return false;
        }
        if (positionFormat == null) {
            if (other.positionFormat != null) {
                return false;
            }
        } else if (!positionFormat.equals(other.positionFormat)) {
            return false;
        }
        return true;
    }

}
