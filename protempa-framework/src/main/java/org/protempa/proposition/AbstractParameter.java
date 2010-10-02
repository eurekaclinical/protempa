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
