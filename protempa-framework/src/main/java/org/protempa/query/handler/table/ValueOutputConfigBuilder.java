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
