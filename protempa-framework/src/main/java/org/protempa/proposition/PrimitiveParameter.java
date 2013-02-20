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

import org.protempa.proposition.visitor.PropositionCheckedVisitor;
import org.protempa.proposition.visitor.PropositionVisitor;
import org.protempa.proposition.interval.Interval;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
public final class PrimitiveParameter extends TemporalParameter implements
        Serializable {

    private static final long serialVersionUID = 693807976086426915L;

    /**
     * Creates a parameter with an identification string.
     * 
     * @param id
     *            an identification string. If passed <code>null</code>, an id
     *            string of <code>""</code> will be used.
     * @param uniqueId
     *            a <code>UniqueId</code> that uniquely identifies this
     *            parameter.
     */
    public PrimitiveParameter(String id, UniqueId uniqueId) {
        super(id, uniqueId);
    }

    /**
     * Returns this parameter's timestamp (or other kind of position value). A
     * <code>null</code> value means the timestamp is unknown.
     * 
     * @return a {@link Long}.
     */
    public Long getPosition() {
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
     * @param pos
     *            a <code>long</code>.
     */
    public void setPosition(Long pos) {
        Interval interval = getInterval();
        if (interval != null) {
            resetInterval(pos, interval.getStartGranularity());
        } else {
            resetInterval(pos, null);
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
        setInterval(INTERVAL_FACTORY.getInstance(timestamp, granularity,
                timestamp, granularity));
    }

    /**
     * Returns this parameter's timestamp as a long formatted string.
     * 
     * @return a <code>String</code>.
     */
    public String getPositionFormattedLong() {
        return getStartFormattedLong();
    }

    /**
     * Returns this parameter's timestamp as a medium-length formatted string.
     * 
     * @return a <code>String</code>.
     */
    public String getPositionFormattedMedium() {
        return getStartFormattedMedium();
    }

    /**
     * Returns this parameter's timestamp as a short formatted string.
     * 
     * @return a <code>String</code>.
     */
    public String getPositionFormattedShort() {
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

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
        s.writeObject(getPosition());
        s.writeObject(getGranularity());
        writeTemporalParameter(s);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        readAbstractProposition(s);
        setPosition((Long) s.readObject());
        setGranularity((Granularity) s.readObject());
        readTemporalParameter(s);
    }
}
