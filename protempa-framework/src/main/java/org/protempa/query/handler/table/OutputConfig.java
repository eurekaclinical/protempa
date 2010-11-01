package org.protempa.query.handler.table;

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
    private final String valueHeading;
    private final String displayNameHeading;
    private final String abbrevDisplayNameHeading;
    private final String startOrTimestampHeading;
    private final String finishHeading;
    private final String lengthHeading;

    public OutputConfig() {
        this.showValue = false;
        this.showDisplayName = false;
        this.showAbbrevDisplayName = false;
        this.showStartOrTimestamp = false;
        this.showFinish = false;
        this.showLength = false;
        this.valueHeading = "";
        this.displayNameHeading = "";
        this.abbrevDisplayNameHeading = "";
        this.startOrTimestampHeading = "";
        this.finishHeading = "";
        this.lengthHeading = "";
    }

    public OutputConfig(boolean showValue, boolean showDisplayName,
            boolean showAbbrevDisplayName,
            boolean showStartOrTimestamp, boolean showFinish,
            boolean showLength,
            String valueHeading, String displayNameHeading,
            String abbrevDisplayNameHeading, String startOrTimestampHeading,
            String finishHeading, String lengthHeading) {
        this.showValue = showValue;
        this.showDisplayName = showDisplayName;
        this.showAbbrevDisplayName = showAbbrevDisplayName;
        this.showStartOrTimestamp = showStartOrTimestamp;
        this.showFinish = showFinish;
        this.showLength = showLength;
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
