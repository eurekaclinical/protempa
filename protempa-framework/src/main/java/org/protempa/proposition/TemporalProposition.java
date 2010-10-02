package org.protempa.proposition;

import java.text.Format;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.Granularity;

/**
 * A proposition with a valid timestamp or interval.
 * 
 * @author Andrew Post
 */
public abstract class TemporalProposition extends AbstractProposition {

    private static final long serialVersionUID = 3263217702318065414L;
    /**
     * The interval over which the proposition is valid.
     */
    private Interval interval;

    private final IntervalFactory intervalFactory;

    /**
     * Creates a proposition with an id.
     *
     * @param id
     *            an identification <code>String</code> for this proposition.
     */
    TemporalProposition(String id) {
        super(id);
        this.intervalFactory = new IntervalFactory();
        this.interval = this.intervalFactory.getInstance();
    }

    /**
     * The range of time over which this parameter's value is true.
     *
     * @return an <code>Interval</code>.
     */
    public Interval getInterval() {
        return this.interval;
    }

    /**
     * Sets the valid interval.
     *
     * @param interval
     *            an <code>Interval</code>.
     */
    public void setInterval(Interval interval) {
        if (interval == null)
            interval = this.intervalFactory.getInstance();
        this.interval = interval;
        this.hashCode = 0;
    }

    public final Granularity getStartGranularity() {
        Interval ival = getInterval();
        return ival.getStartGranularity();
    }

    public final Granularity getFinishGranularity() {
        Interval ival = getInterval();
        return ival.getFinishGranularity();
    }

    public String getStartRepr() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getReprFormat() :
            null);
    }

    /**
     * Returns the earliest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public String getStartFormattedLong() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getLongFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.TemporalProposition#getFinishRepr()
     */
    public String getFinishRepr() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getReprFormat()
                : null);
    }

    /**
     * Returns the latest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public String getFinishFormattedLong() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getLongFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public String getStartFormattedMedium() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getMediumFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public String getFinishFormattedMedium() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getMediumFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public String getStartFormattedShort() {
        Granularity startGran = getStartGranularity();
        return formatStart(startGran != null ? startGran.getShortFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public String getFinishFormattedShort() {
        Granularity finishGran = getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getShortFormat()
                : null);
    }

    /**
     * Uses the given <code>Format</code> to format the start of this
     * parameter's interval.
     *
     * @param format
     *            a <code>Format</code> object.
     * @return the start of this parameter's interval as a formatted
     *         <code>String</code>.
     */
    private String formatStart(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            Long minStart = interval.getMinimumStart();
            if (minStart != null)
                return format.format(minStart);
            else
                return "Unknown";
        } else if (interval != null) {
            Long minStart = interval.getMinimumStart();
            if (minStart != null)
                return "" + minStart;
            else
                return "Unknown";
        } else {
            return "Unknown";
        }
    }

    /**
     * Uses the given <code>Format</code> to format the finish of this
     * parameter's interval.
     *
     * @param format
     *            a <code>Format</code> object.
     * @return the finish of this parameter's interval as a formatted
     *         <code>String</code>.
     */
    private String formatFinish(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            Long minFinish = interval.getMinimumFinish();
            if (minFinish != null)
                return format.format(minFinish);
            else
                return "Unknown";
        } else if (interval != null) {
            Long minFinish = interval.getMinimumFinish();
            if (minFinish != null)
                return "" + minFinish;
            else
                return "Unknown";
        } else {
            return "Unknown";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isEqual(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TemporalProposition)) {
            return false;
        }

        TemporalProposition p = (TemporalProposition) other;
        return super.isEqual(p)
                && (this.interval == p.interval
                || this.interval.equals(p.interval));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("interval", this.interval)
                .toString();
    }
}
