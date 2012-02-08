package org.protempa.query.handler.table;

public class ValueOutputConfig {
    private final String propertyValueDisplayName;
    private final String propertyValueAbbrevDisplayName;
    private final boolean showPropertyValueDisplayName;
    private final boolean showPropertyValueAbbrevDisplayName;

    public ValueOutputConfig() {
        this.propertyValueAbbrevDisplayName = "";
        this.propertyValueDisplayName = "";
        this.showPropertyValueAbbrevDisplayName = false;
        this.showPropertyValueDisplayName = false;
    }

    public ValueOutputConfig(boolean showPropertyValueDisplayName,
            boolean showPropertyValueAbbrevDisplayName,
            String propertyValueDisplayName,
            String propertyValueAbbrevDisplayName) {
        this.propertyValueAbbrevDisplayName = propertyValueAbbrevDisplayName;
        this.propertyValueDisplayName = propertyValueDisplayName;
        this.showPropertyValueAbbrevDisplayName = showPropertyValueAbbrevDisplayName;
        this.showPropertyValueDisplayName = showPropertyValueDisplayName;
    }

    public String getPropertyValueDisplayName() {
        return propertyValueDisplayName;
    }

    public String getPropertyValueAbbrevDisplayName() {
        return propertyValueAbbrevDisplayName;
    }

    public boolean isShowPropertyValueDisplayName() {
        return showPropertyValueDisplayName;
    }

    public boolean isShowPropertyValueAbbrevDisplayName() {
        return showPropertyValueAbbrevDisplayName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((propertyValueAbbrevDisplayName == null) ? 0 : propertyValueAbbrevDisplayName.hashCode());
		result = prime * result + ((propertyValueDisplayName == null) ? 0 : propertyValueDisplayName.hashCode());
		result = prime * result + (showPropertyValueAbbrevDisplayName ? 1231 : 1237);
		result = prime * result + (showPropertyValueDisplayName ? 1231 : 1237);
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
		ValueOutputConfig other = (ValueOutputConfig) obj;
		if (propertyValueAbbrevDisplayName == null) {
			if (other.propertyValueAbbrevDisplayName != null)
				return false;
		} else if (!propertyValueAbbrevDisplayName.equals(other.propertyValueAbbrevDisplayName))
			return false;
		if (propertyValueDisplayName == null) {
			if (other.propertyValueDisplayName != null)
				return false;
		} else if (!propertyValueDisplayName.equals(other.propertyValueDisplayName))
			return false;
		if (showPropertyValueAbbrevDisplayName != other.showPropertyValueAbbrevDisplayName)
			return false;
		if (showPropertyValueDisplayName != other.showPropertyValueDisplayName)
			return false;
		return true;
	}
}
