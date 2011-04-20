package org.protempa.query.handler.table;

/**
 * A bean-like class for creating {@link OutputConfig} configuration
 * objects.
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
    private String valueHeading;
    private String displayNameHeading;
    private String abbrevDisplayNameHeading;
    private String startOrTimestampHeading;
    private String finishHeading;
    private String lengthHeading;
    private String idHeading;

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
        this.showValue = false;
        this.showDisplayName = false;
        this.showAbbrevDisplayName = false;
        this.showStartOrTimestamp = false;
        this.showFinish = false;
        this.showLength = false;
        this.showId = false;
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

    public String getValueHeading() {
        return valueHeading;
    }

    public void setValueHeading(String valueHeading) {
        if (valueHeading == null)
            valueHeading = "";
        this.valueHeading = valueHeading;
    }

    public String getDisplayNameHeading() {
        return displayNameHeading;
    }

    public void setDisplayNameHeading(String displayNameHeading) {
        if (displayNameHeading == null)
            displayNameHeading = "";
        this.displayNameHeading = displayNameHeading;
    }

    public String getAbbrevDisplayNameHeading() {
        return abbrevDisplayNameHeading;
    }

    public void setAbbrevDisplayNameHeading(String abbrevDisplayNameHeading) {
        if (abbrevDisplayNameHeading == null)
            abbrevDisplayNameHeading = "";
        this.abbrevDisplayNameHeading = abbrevDisplayNameHeading;
    }

    public String getStartOrTimestampHeading() {
        return startOrTimestampHeading;
    }

    public void setStartOrTimestampHeading(String startOrTimestampHeading) {
        if (startOrTimestampHeading == null)
            startOrTimestampHeading = "";
        this.startOrTimestampHeading = startOrTimestampHeading;
    }

    public String getFinishHeading() {
        return finishHeading;
    }

    public void setFinishHeading(String finishHeading) {
        if (finishHeading == null)
            finishHeading = "";
        this.finishHeading = finishHeading;
    }

    public String getLengthHeading() {
        return lengthHeading;
    }

    public void setLengthHeading(String lengthHeading) {
        if (lengthHeading == null)
            lengthHeading = "";
        this.lengthHeading = lengthHeading;
    }

    public boolean showId() {
        return this.showId;
    }

    public void setShowId(boolean showId) {
        this.showId = showId;
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

    /**
     * Creates a new {@link OutputConfig} instance.
     * @return a {@link OutputConfig}.
     */
    public OutputConfig build() {
        return new OutputConfig(this.showId, this.showValue, this.showDisplayName,
                this.showAbbrevDisplayName, this.showStartOrTimestamp,
                this.showFinish, this.showLength, this.idHeading, this.valueHeading,
                this.displayNameHeading, this.abbrevDisplayNameHeading,
                this.startOrTimestampHeading, this.finishHeading,
                this.lengthHeading);
    }
}
