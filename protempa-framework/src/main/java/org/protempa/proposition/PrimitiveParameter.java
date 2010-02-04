package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.text.Format;

import java.util.Set;
import org.arp.javautil.arrays.Arrays;
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

	/**
	 * This primitive parameter's timestamp. Could also store some other kind of
	 * position value.
	 */
	private long timestamp;

	/**
	 * This primitive parameter's granularity.
	 */
	private Granularity granularity;

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
	 * 
	 * @return a <code>long</code>.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets this parameter's timestamp (or other kind of position value).
	 * 
	 * @param timestamp
	 *            a <code>long</code>.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		resetInterval();
	}

	/**
	 * Returns the granularity of this parameter's timestamp.
	 * 
	 * @return a {@link Granularity} object.
	 */
	public Granularity getGranularity() {
		return this.granularity;
	}

	/**
	 * Sets the granularity of this parameter's timestamp.
	 * 
	 * @param granularity
	 *            a {@link Granularity} object.
	 */
	public void setGranularity(Granularity granularity) {
		this.granularity = granularity;
		resetInterval();
	}

	private void resetInterval() {
		/*
		 * As per Combi et al. Methods Inf. Med. 1995;34:458-74.
		 */
		setInterval(new PointInterval(timestamp, granularity, timestamp,
				granularity));
	}

	@Override
	public String getStartRepr() {
		return getTimestampRepr();
	}

	@Override
	public String getFinishRepr() {
		return getTimestampRepr();
	}

	public String getTimestampRepr() {
		return formatTimestamp(granularity != null ? granularity
				.getReprFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedLong()
	 */
	@Override
	public String getStartFormattedLong() {
		return formatTimestamp(granularity != null ? granularity
				.getLongFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedLong()
	 */
	@Override
	public String getFinishFormattedLong() {
		return formatTimestamp(granularity != null ? granularity
				.getLongFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedMedium()
	 */
	@Override
	public String getStartFormattedMedium() {
		return formatTimestamp(granularity != null ? granularity
				.getMediumFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedMedium()
	 */
	@Override
	public String getFinishFormattedMedium() {
		return formatTimestamp(granularity != null ? granularity
				.getMediumFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getStartFormattedShort()
	 */
	@Override
	public String getStartFormattedShort() {
		return formatTimestamp(granularity != null ? granularity
				.getShortFormat() : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Parameter#getFinishFormattedShort()
	 */
	@Override
	public String getFinishFormattedShort() {
		return formatTimestamp(granularity != null ? granularity
				.getShortFormat() : null);
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

	/**
	 * Converts this parameter's timestamp into a meaningful string
	 * representation.
	 * 
	 * @param format
	 *            a {@link Format} that creates a meaningful string
	 *            representation of a <code>long</code>.
	 * @return a {@link String}.
	 */
	public String formatTimestamp(Format format) {
		if (format != null) {
			return format.format(timestamp);
		} else {
			return "" + timestamp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.proposition.Proposition#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.changes.addPropertyChangeListener(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.proposition.Proposition#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		this.changes.removePropertyChangeListener(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        StringBuilder buf = new StringBuilder();
        Set<String> propNames = propertyNames();
        String[] propStrings = new String[propNames.size()];
        int i = 0;
        for (String propertyName : propNames) {
            propStrings[i] = propertyName + ": " +
                    getProperty(propertyName).getRepr();
        }
        buf.append(Arrays.join(propStrings, ", "));
		return "PrimitiveParameter: " + getId() + "; " +  getValueFormatted()
                + ": "
				+ getTimestampFormattedShort() + "; " + buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.proposition.TemporalParameter#isEqual(java.lang.Object)
	 */
	@Override
	public boolean isEqual(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof PrimitiveParameter)) {
			return false;
		}

		PrimitiveParameter p = (PrimitiveParameter) o;
		return super.isEqual(p)
				&& (granularity == p.granularity || (granularity != null && granularity
						.equals(p.granularity)));
	}

	public void accept(PropositionVisitor propositionVisitor) {
		propositionVisitor.visit(this);
	}

    public void acceptChecked(PropositionCheckedVisitor
            propositionCheckedVisitor) throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }
}
