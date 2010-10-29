package org.protempa.query.handler.table;

public class OutputConfig {
    private boolean showValue;
    private boolean showDisplayName;
    private boolean showAbbrevDisplayName;
    private boolean showStartOrTimestamp;
    private boolean showFinish;
    private boolean showLength;
    private String valueHeading;
    private String displayNameHeading;
    private String abbrevDisplayNameHeading;
    private String startOrTimestampHeading;
    private String finishHeading;
    private String lengthHeading;

    public OutputConfig(OutputConfig orig) {
        this.showValue = orig.showValue;
        this.showDisplayName = orig.showDisplayName;
        this.showAbbrevDisplayName = orig.showAbbrevDisplayName;
        this.showStartOrTimestamp = orig.showStartOrTimestamp;
        this.showFinish = orig.showFinish;
        this.showLength = orig.showLength;
        this.valueHeading = orig.valueHeading;
        this.displayNameHeading = orig.displayNameHeading;
        this.abbrevDisplayNameHeading = orig.abbrevDisplayNameHeading;
        this.startOrTimestampHeading = orig.startOrTimestampHeading;
        this.finishHeading = orig.finishHeading;
        this.lengthHeading = orig.lengthHeading;
    }

    public OutputConfig() {
    }

    public String getValueHeading() {
        return valueHeading;
    }

    public void setValueHeading(String valueHeading) {
        this.valueHeading = valueHeading;
    }

    public String getDisplayNameHeading() {
        return displayNameHeading;
    }

    public void setDisplayNameHeading(String displayNameHeading) {
        this.displayNameHeading = displayNameHeading;
    }

    public String getAbbrevDisplayNameHeading() {
        return abbrevDisplayNameHeading;
    }

    public void setAbbrevDisplayNameHeading(String abbrevDisplayNameHeading) {
        this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
    }

    public String getStartOrTimestampHeading() {
        return startOrTimestampHeading;
    }

    public void setStartOrTimestampHeading(String startOrTimestampHeading) {
        this.startOrTimestampHeading = startOrTimestampHeading;
    }

    public String getFinishHeading() {
        return finishHeading;
    }

    public void setFinishHeading(String finishHeading) {
        this.finishHeading = finishHeading;
    }

    public String getLengthHeading() {
        return lengthHeading;
    }

    public void setLengthHeading(String lengthHeading) {
        this.lengthHeading = lengthHeading;
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

    public int numActiveColumns() {
        int i = 0;
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

}
