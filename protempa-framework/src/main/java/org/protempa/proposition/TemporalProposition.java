package org.protempa.proposition;

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

    /**
     * Creates a proposition with an id.
     *
     * @param id
     *            an identification <code>String</code> for this proposition.
     */
    TemporalProposition(String id) {
        super(id);
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
        this.interval = interval;
        this.hashCode = 0;
    }

    public final Granularity getStartGranularity() {
        Interval ival = getInterval();
        if (ival != null) {
            return ival.getStartGranularity();
        } else {
            return null;
        }
    }

    public final Granularity getFinishGranularity() {
        Interval ival = getInterval();
        if (ival != null) {
            return ival.getFinishGranularity();
        } else {
            return null;
        }
    }

    public abstract String getStartRepr();

    public abstract String getFinishRepr();

    /**
     * Returns the earliest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getStartFormattedLong();

    /**
     * Returns the latest valid time of this proposition as a long string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getFinishFormattedLong();

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getStartFormattedMedium();

    /**
     * Returns the earliest valid time of this proposition as a medium-length
     * string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getFinishFormattedMedium();

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getStartFormattedShort();

    /**
     * Returns the earliest valid time of this proposition as a short string.
     *
     * @return a <code>String</code>.
     */
    public abstract String getFinishFormattedShort();

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
                || (this.interval != null
                && this.interval.equals(p.interval)));

    }
}
