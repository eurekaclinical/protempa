package org.protempa.proposition;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;
import org.protempa.proposition.value.Granularity;

/**
 * A raw data parameter with a timestamp (or other position value). A primitive
 * parameter has a parameter id, attribute id, timestamp, and value. A single
 * parameter type can have multiple attributes. Each attribute is represented by
 * an unique parameter id and attribute id combination.
 * 
 * @author Andrew Post
 */
public final class PrimitiveParameter extends TemporalParameter {

    private static final long serialVersionUID = 693807976086426915L;

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    /**
     * Creates a parameter with an identification string.
     *
     * @param id
     *            an identification string. If passed <code>null</code>, an
     *            id string of <code>""</code> will be used.
     */
    public PrimitiveParameter(String id) {
        super(id);
    }

    /**
     * Returns this parameter's timestamp (or other kind of position value).
     * A <code>null</code> value means the timestamp is unknown.
     *
     * @return a {@link Long}.
     */
    public Long getTimestamp() {
        Interval interval = getInterval();
        if (interval != null) {
            return interval.getMinStart();
        } else {
            return null;
        }
    }

    /**
     * Sets this parameter's timestamp (or other kind of position value).
     *
     * @param timestamp
     *            a <code>long</code>.
     */
    public void setTimestamp(Long timestamp) {
        Interval interval = getInterval();
        if (interval != null) {
            resetInterval(timestamp, interval.getStartGranularity());
        } else {
            resetInterval(null, interval.getStartGranularity());
        }
    }

    /**
     * Returns the granularity of this parameter's timestamp.
     *
     * @return a {@link Granularity} object.
     */
    public Granularity getGranularity() {
        Interval interval = getInterval();
        if (interval != null) {
            return interval.getStartGranularity();
        } else {
            return null;
        }
    }

    /**
     * Sets the granularity of this parameter's timestamp.
     *
     * @param granularity
     *            a {@link Granularity} object.
     */
    public void setGranularity(Granularity granularity) {
        Interval interval = getInterval();
        if (interval != null) {
            resetInterval(interval.getMinStart(), granularity);
        } else {
            resetInterval(null, granularity);
        }
    }

    private void resetInterval(Long timestamp, Granularity granularity) {
        /*
         * As per Combi et al. Methods Inf. Med. 1995;34:458-74.
         */
        setInterval(intervalFactory.getInstance(
                timestamp, granularity, timestamp, granularity));
    }

    /**
     * Returns this parameter's timestamp as a long formatted string.
     *
     * @return a <code>String</code>.
     */
    public String getTimestampFormattedLong() {
        return getStartFormattedLong();
    }

    /**
     * Returns this parameter's timestamp as a medium-length formatted string.
     *
     * @return a <code>String</code>.
     */
    public String getTimestampFormattedMedium() {
        return getStartFormattedMedium();
    }

    /**
     * Returns this parameter's timestamp as a short formatted string.
     *
     * @return a <code>String</code>.
     */
    public String getTimestampFormattedShort() {
        return getStartFormattedShort();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .toString();
    }
    
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrimitiveParameter)) {
            return false;
        }

        PrimitiveParameter p = (PrimitiveParameter) o;
        return super.isEqual(p);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(
            PropositionCheckedVisitor propositionCheckedVisitor)
            throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }
}
