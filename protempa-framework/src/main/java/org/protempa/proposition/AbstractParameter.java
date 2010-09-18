package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.text.Format;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;
import org.protempa.proposition.value.Granularity;

/**
 * An parameter over an interval. We call it "abstract" because medical
 * databases usually store data as time-stamped raw data, so we have to infer
 * the values of interval parameters.
 * 
 * @author Andrew Post
 */
public final class AbstractParameter extends TemporalParameter {

    private static final long serialVersionUID = -137441242472941229L;

    /**
     * Creates an abstract parameter with an id.
     *
     * @param id
     *            an identification <code>String</code> for this parameter. If
     *            <code>null</code>, the default is used (<code>""</code>).
     */
    public AbstractParameter(String id) {
        super(id);
    }

    @Override
    public String getStartRepr() {
        Granularity startGranularity = getStartGranularity();
        return formatStart(startGranularity != null ?
            startGranularity.getReprFormat() : null);
    }

    @Override
    public String getFinishRepr() {
        Granularity finishGranularity = getFinishGranularity();
        return formatFinish(finishGranularity != null ?
            finishGranularity.getReprFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedLong()
     */
    @Override
    public String getStartFormattedLong() {
        Granularity startGranularity = getStartGranularity();
        return formatStart(startGranularity != null ?
            startGranularity.getLongFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedLong()
     */
    @Override
    public String getFinishFormattedLong() {
        Granularity finishGranularity = getFinishGranularity();
        return formatFinish(finishGranularity != null ?
            finishGranularity.getLongFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedMedium()
     */
    @Override
    public String getStartFormattedMedium() {
        Granularity startGranularity = getStartGranularity();
        return formatStart(startGranularity != null ?
            startGranularity.getMediumFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedMedium()
     */
    @Override
    public String getFinishFormattedMedium() {
        Granularity finishGranularity = getFinishGranularity();
        return formatFinish(finishGranularity != null ?
            finishGranularity.getMediumFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedShort()
     */
    @Override
    public String getStartFormattedShort() {
        Granularity startGranularity = getStartGranularity();
        return formatStart(startGranularity != null ?
            startGranularity.getShortFormat() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedShort()
     */
    @Override
    public String getFinishFormattedShort() {
        Granularity finishGranularity = getFinishGranularity();
        return formatFinish(finishGranularity != null ?
            finishGranularity.getShortFormat() : null);
    }

    /**
     * Converts the start of this parameter's interval into a meaningful string
     * representation.
     *
     * @param format
     *            a {@link Format} that creates a meaningful string
     *            representation of the start of an {@link Interval}.
     * @return a {@link String}.
     */
    public String formatStart(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            return format.format(interval.getMinimumStart());
        } else if (interval != null) {
            return "" + interval.getMinimumStart();
        } else {
            return null;
        }
    }

    /**
     * Converts the finish of this parameter's interval into a meaningful string
     * representation.
     *
     * @param format
     *            a {@link Format} that creates a meaningful string
     *            representation of the finish of an {@link Interval}.
     * @return a {@link String}.
     */
    public String formatFinish(Format format) {
        Interval interval = getInterval();
        if (format != null && interval != null) {
            return format.format(interval.getMinimumFinish());
        } else if (interval != null) {
            return "" + interval.getMinimumFinish();
        } else {
            return null;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.Proposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AbstractParameter)) {
            return false;
        }

        AbstractParameter a = (AbstractParameter) o;
        Granularity startGranularity = getStartGranularity();
        Granularity aStartGranularity = a.getStartGranularity();
        Granularity finishGranularity = getFinishGranularity();
        Granularity aFinishGranularity = a.getFinishGranularity();
        return super.isEqual(a)
                && (startGranularity == aStartGranularity ||
                (startGranularity != null &&
                startGranularity.equals(aStartGranularity)))
                && (finishGranularity == aFinishGranularity ||
                (finishGranularity != null &&
                finishGranularity.equals(aFinishGranularity)));
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionCheckedVisitor
            propositionCheckedVisitor) throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
