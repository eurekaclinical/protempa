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
import java.util.UUID;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.protempa.ProtempaException;
import org.protempa.proposition.value.Granularity;

/**
 * An parameter over an interval. We call it "abstract" because medical
 * databases usually store data as time-stamped raw data, so we have to infer
 * the values of interval parameters.
 * 
 * @author Andrew Post
 */
public final class AbstractParameter extends TemporalParameter implements
        Serializable {

    private static final long serialVersionUID = -137441242472941229L;
    
    private String contextId;

    /**
     * Creates an abstract parameter with an id.
     * 
     * @param id
     *            an identification <code>String</code> for this parameter. If
     *            <code>null</code>, the default is used (<code>""</code>).
     * @param uniqueId
     *            a <code>UniqueId</code> that uniquely identifies this
     *            parameter.
     */
    public AbstractParameter(String id) {
        super(id, new UniqueId(
                DerivedSourceId.getInstance(), new DerivedUniqueId(UUID
                .randomUUID().toString())));
    }

    @Override
    public void setInterval(Interval interval) {
        super.setInterval(interval);
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
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
        Granularity startGranularity = getInterval().getStartGranularity();
        Granularity aStartGranularity = a.getInterval().getStartGranularity();
        Granularity finishGranularity = getInterval().getFinishGranularity();
        Granularity aFinishGranularity = a.getInterval().getFinishGranularity();
        return super.isEqual(a)
                && (startGranularity == aStartGranularity || (startGranularity != null && startGranularity
                        .equals(aStartGranularity)))
                && (finishGranularity == aFinishGranularity || (finishGranularity != null && finishGranularity
                        .equals(aFinishGranularity)))
                && (this.contextId == a.contextId || (this.contextId != null && this.contextId.equals(a.contextId)));
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).toString();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
        writeTemporalProposition(s);
        writeTemporalParameter(s);
        s.writeObject(this.contextId);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        readAbstractProposition(s);
        readTemporalProposition(s);
        readTemporalParameter(s);
        this.contextId = (String) s.readObject();
    }
}
