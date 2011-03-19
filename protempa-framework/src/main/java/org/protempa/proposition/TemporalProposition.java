package org.protempa.proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.NumberFormat;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

/**
 * A proposition with a valid timestamp or interval.
 * 
 * @author Andrew Post
 */
public abstract class TemporalProposition extends AbstractProposition {

    private static final long serialVersionUID = 3263217702318065414L;

    private static final NumberFormat numberFormat =
            NumberFormat.getInstance();
    static {
        numberFormat.setGroupingUsed(true);
    }

    protected static final IntervalFactory INTERVAL_FACTORY =
            new IntervalFactory();

    /**
     * The interval over which the proposition is valid.
     */
    private transient Interval interval;

    /**
     * Creates a proposition with an id.
     *
     * @param id
     *            an identification <code>String</code> for this proposition.
     */
    TemporalProposition(String id) {
        super(id);
        this.interval = INTERVAL_FACTORY.getInstance();
    }

    protected TemporalProposition() {}

    /**
     * The range of time over which this parameter's value is true.
     *
     * @return an <code>Interval</code>.
     */
    public final Interval getInterval() {
        return this.interval;
    }

    /**
     * Sets the valid interval.
     *
     * @param interval
     *            an <code>Interval</code>.
     */
    protected void setInterval(Interval interval) {
        if (interval == null) {
            interval = INTERVAL_FACTORY.getInstance();
        }
        this.interval = interval;
        this.hashCode = 0;
    }

    /**
     * Returns the earliest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public final String getStartFormattedLong() {
        Granularity startGran = this.interval.getStartGranularity();
        return formatStart(startGran != null ? startGran.getLongFormat() : null);
    }

    public final String getLengthFormattedLong() {
        Unit lengthUnit = this.interval.getLengthUnit();
        return formatLength(lengthUnit != null
                ? lengthUnit.getLongFormat() : null);
    }

    public final String getLengthFormattedMedium() {
        Unit lengthUnit = this.interval.getLengthUnit();
        return formatLength(lengthUnit != null
                ? lengthUnit.getMediumFormat() : null);
    }

    public final String getLengthFormattedShort() {
        Unit lengthUnit = this.interval.getLengthUnit();
        return formatLength(lengthUnit != null
                ? lengthUnit.getShortFormat() : null);
    }

    /**
     * Returns the latest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public final String getFinishFormattedLong() {
        Granularity finishGran = this.interval.getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getLongFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public final String getStartFormattedMedium() {
        Granularity startGran = this.interval.getStartGranularity();
        return formatStart(startGran != null ? startGran.getMediumFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public final String getFinishFormattedMedium() {
        Granularity finishGran = this.interval.getFinishGranularity();
        return formatFinish(finishGran != null ? finishGran.getMediumFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public final String getStartFormattedShort() {
        Granularity startGran = this.interval.getStartGranularity();
        return formatStart(startGran != null ? startGran.getShortFormat()
                : null);
    }

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public final String getFinishFormattedShort() {
        Granularity finishGran = this.interval.getFinishGranularity();
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
        if (format != null) {
            Long minStart = interval.getMinStart();
            if (minStart != null) {
                return format.format(minStart);
            } else {
                return "Unknown";
            }
        } else {
            Long minStart = interval.getMinStart();
            if (minStart != null) {
                return numberFormat.format(minStart);
            } else {
                return "Unknown";
            }
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
        if (format != null) {
            Long minFinish = interval.getMinFinish();
            if (minFinish != null) {
                return format.format(minFinish);
            } else {
                return "Unknown";
            }
        } else {
            Long minFinish = interval.getMinFinish();
            if (minFinish != null) {
                return numberFormat.format(minFinish);
            } else {
                return "Unknown";
            }
        }
    }

    private String formatLength(Format format) {
        if (format != null) {
            Long minLength = interval.getMinLength();
            if (minLength != null) {
                return format.format(minLength);
            } else {
                return "Unknown";
            }
        } else {
            Long minLength = interval.getMinLength();
            if (minLength != null) {
                return numberFormat.format(minLength);
            } else {
                return "Unknown";
            }
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
                .append("interval", this.interval).toString();
    }

    protected void writeTemporalProposition(ObjectOutputStream s) throws IOException {
        s.writeObject(this.interval.getMinStart());
        s.writeObject(this.interval.getMaxStart());
        s.writeObject(this.interval.getStartGranularity());
        s.writeObject(this.interval.getMinFinish());
        s.writeObject(this.interval.getMaxFinish());
        s.writeObject(this.interval.getFinishGranularity());
    }

    protected void readTemporalProposition(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        setInterval(INTERVAL_FACTORY.getInstance((Long) s.readObject(),
                (Long) s.readObject(), (Granularity) s.readObject(),
                (Long) s.readObject(), (Long) s.readObject(),
                (Granularity) s.readObject()));
    }
}
