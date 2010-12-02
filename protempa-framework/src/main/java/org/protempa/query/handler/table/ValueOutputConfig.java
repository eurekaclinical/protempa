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
}
