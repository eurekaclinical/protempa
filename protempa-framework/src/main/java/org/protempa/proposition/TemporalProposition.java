/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition;

import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.SimpleInterval;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.NumberFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

/**
 * A proposition with a valid timestamp or interval.
 * 
 * @author Andrew Post
 */
public abstract class TemporalProposition extends AbstractProposition {

    private static final long serialVersionUID = 3263217702318065414L;

    private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue () {
            NumberFormat format = NumberFormat.getInstance();
            format.setGroupingUsed(true);
            return format;
        }
    };

    protected static final IntervalFactory INTERVAL_FACTORY = new IntervalFactory();

    /**
     * The interval over which the proposition is valid.
     */
    private transient Interval interval;

    /**
     * Creates a proposition with an id.
     * 
     * @param id
     *            an identification <code>String</code> for this proposition.
     * @param uniqueId
     *            a <code>UniqueId</code> that uniquely identifies this
     *            proposition.
     */
    TemporalProposition(String id, UniqueId uniqueId) {
        super(id, uniqueId);
        this.interval = INTERVAL_FACTORY.getInstance();
    }

    /**
     * Here only for use by deserialization. Do not use this for any other
     * reason!
     */
    protected TemporalProposition() {
    }

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
        return formatLength(lengthUnit != null ? lengthUnit.getLongFormat()
                : null);
    }

    public final String getLengthFormattedMedium() {
        Unit lengthUnit = this.interval.getLengthUnit();
        return formatLength(lengthUnit != null ? lengthUnit.getMediumFormat()
                : null);
    }

    public final String getLengthFormattedShort() {
        Unit lengthUnit = this.interval.getLengthUnit();
        return formatLength(lengthUnit != null ? lengthUnit.getShortFormat()
                : null);
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
    public final String formatStart(Format format) {
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
                return numberFormat.get().format(minStart);
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
    public final String formatFinish(Format format) {
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
                return numberFormat.get().format(minFinish);
            } else {
                return "Unknown";
            }
        }
    }

    public final String formatLength(Format format) {
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
                return numberFormat.get().format(minLength);
            } else {
                return "Unknown";
            }
        }
    }

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
                && (this.interval == p.interval || this.interval
                        .equals(p.interval));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interval=" + this.interval)
                .appendSuper(super.toString()).toString();
    }

    /**
     * Called while serializing a temporal proposition. It optimizes for when
     * the temporal proposition's interval is a {@link SimpleInterval}.
     * 
     * @param s
     *            an {@link ObjectOutputStream}.
     * @throws IOException
     *             when an error occurs during serialization.
     */
    protected void writeTemporalProposition(ObjectOutputStream s)
            throws IOException {
        if (this.interval instanceof SimpleInterval) {
            long start = this.interval.getMinStart();
            long finish = this.interval.getMinFinish();
            Granularity startGran = this.interval.getStartGranularity();
            Granularity finishGran = this.interval.getFinishGranularity();
            if (start == finish && startGran == finishGran) {
                s.writeChar(0);
                s.writeLong(start);
                s.writeObject(startGran);
            } else {
                s.writeChar(1);
                s.writeLong(start);
                s.writeObject(startGran);
                s.writeLong(finish);
                s.writeObject(finishGran);
            }
        } else {
            s.writeChar(2);
            s.writeObject(this.interval.getMinStart());
            s.writeObject(this.interval.getMaxStart());
            s.writeObject(this.interval.getStartGranularity());
            s.writeObject(this.interval.getMinFinish());
            s.writeObject(this.interval.getMaxFinish());
            s.writeObject(this.interval.getFinishGranularity());
        }

    }

    /**
     * Called while deserializing a temporal proposition.
     * 
     * @param s
     *            an {@link ObjectInputStream}.
     * @throws IOException
     *             input/output error during deserialization.
     * @throws ClassNotFoundException
     *             class of a serialized object cannot be found.
     */
    protected void readTemporalProposition(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        int mode = s.readChar();
        try {
            switch (mode) {
                case 0:
                    setInterval(INTERVAL_FACTORY.getInstance(s.readLong(),
                            (Granularity) s.readObject()));
                    break;
                case 1:
                    setInterval(INTERVAL_FACTORY.getInstance(s.readLong(),
                            (Granularity) s.readObject(), s.readLong(),
                            (Granularity) s.readObject()));
                    break;
                case 2:
                    setInterval(INTERVAL_FACTORY.getInstance(
                            (Long) s.readObject(), (Long) s.readObject(),
                            (Granularity) s.readObject(),
                            (Long) s.readObject(), (Long) s.readObject(),
                            (Granularity) s.readObject()));
                    break;
                default:
                    throw new InvalidObjectException(
                            "Can't restore. Invalid mode: " + mode);
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidObjectException("Can't restore: "
                    + iae.getMessage());
        }
    }
}
