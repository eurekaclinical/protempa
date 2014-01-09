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

/**
 * An immutable class for configurating {@link TableColumnSpec}s.
 *
 * @author Himanshu Rathod
 */
public class OutputConfig {
    private final boolean showValue;
    private final boolean showDisplayName;
    private final boolean showAbbrevDisplayName;
    private final boolean showStartOrTimestamp;
    private final boolean showFinish;
    private final boolean showLength;
    private final boolean showId;
    private final String valueHeading;
    private final String displayNameHeading;
    private final String abbrevDisplayNameHeading;
    private final String startOrTimestampHeading;
    private final String finishHeading;
    private final String lengthHeading;
    private final String idHeading;

    public OutputConfig() {
        this.showValue = false;
        this.showDisplayName = false;
        this.showAbbrevDisplayName = false;
        this.showStartOrTimestamp = false;
        this.showFinish = false;
        this.showLength = false;
        this.showId = false;
        this.valueHeading = "";
        this.displayNameHeading = "";
        this.abbrevDisplayNameHeading = "";
        this.startOrTimestampHeading = "";
        this.finishHeading = "";
        this.lengthHeading = "";
        this.idHeading = "";
    }

    public OutputConfig(boolean showId, boolean showValue, boolean showDisplayName,
            boolean showAbbrevDisplayName,
            boolean showStartOrTimestamp, boolean showFinish,
            boolean showLength, String idHeading,
            String valueHeading, String displayNameHeading,
            String abbrevDisplayNameHeading, String startOrTimestampHeading,
            String finishHeading, String lengthHeading) {
        this.showId = showId;
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
        if (valueHeading == null)
            valueHeading = "";
        this.valueHeading = valueHeading;
        if (displayNameHeading == null)
            displayNameHeading = "";
        this.displayNameHeading = displayNameHeading;
        if (abbrevDisplayNameHeading == null)
            abbrevDisplayNameHeading = "";
        this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
        if (startOrTimestampHeading == null)
            startOrTimestampHeading = "";
        this.startOrTimestampHeading = startOrTimestampHeading;
        if (finishHeading == null)
            finishHeading = "";
        this.finishHeading = finishHeading;
        if (lengthHeading == null)
            lengthHeading = "";
        this.lengthHeading = lengthHeading;
    }

    public String getIdHeading() {
        return idHeading;
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

    public boolean showId() {
        return showId;
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

    public int numActiveColumns() {
        int i = 0;
        if (this.showId) {
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
		result = prime * result + ((lengthHeading == null) ? 0 : lengthHeading.hashCode());
		result = prime * result + (showAbbrevDisplayName ? 1231 : 1237);
		result = prime * result + (showDisplayName ? 1231 : 1237);
		result = prime * result + (showFinish ? 1231 : 1237);
		result = prime * result + (showId ? 1231 : 1237);
		result = prime * result + (showLength ? 1231 : 1237);
		result = prime * result + (showStartOrTimestamp ? 1231 : 1237);
		result = prime * result + (showValue ? 1231 : 1237);
		result = prime * result + ((startOrTimestampHeading == null) ? 0 : startOrTimestampHeading.hashCode());
		result = prime * result + ((valueHeading == null) ? 0 : valueHeading.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutputConfig other = (OutputConfig) obj;
		if (abbrevDisplayNameHeading == null) {
			if (other.abbrevDisplayNameHeading != null)
				return false;
		} else if (!abbrevDisplayNameHeading.equals(other.abbrevDisplayNameHeading))
			return false;
		if (displayNameHeading == null) {
			if (other.displayNameHeading != null)
				return false;
		} else if (!displayNameHeading.equals(other.displayNameHeading))
			return false;
		if (finishHeading == null) {
			if (other.finishHeading != null)
				return false;
		} else if (!finishHeading.equals(other.finishHeading))
			return false;
		if (idHeading == null) {
			if (other.idHeading != null)
				return false;
		} else if (!idHeading.equals(other.idHeading))
			return false;
		if (lengthHeading == null) {
			if (other.lengthHeading != null)
				return false;
		} else if (!lengthHeading.equals(other.lengthHeading))
			return false;
		if (showAbbrevDisplayName != other.showAbbrevDisplayName)
			return false;
		if (showDisplayName != other.showDisplayName)
			return false;
		if (showFinish != other.showFinish)
			return false;
		if (showId != other.showId)
			return false;
		if (showLength != other.showLength)
			return false;
		if (showStartOrTimestamp != other.showStartOrTimestamp)
			return false;
		if (showValue != other.showValue)
			return false;
		if (startOrTimestampHeading == null) {
			if (other.startOrTimestampHeading != null)
				return false;
		} else if (!startOrTimestampHeading.equals(other.startOrTimestampHeading))
			return false;
		if (valueHeading == null) {
			if (other.valueHeading != null)
				return false;
		} else if (!valueHeading.equals(other.valueHeading))
			return false;
		return true;
	}

}
