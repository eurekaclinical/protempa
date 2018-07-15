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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.value.Value;

/**
 * @author Andrew Post
 */
public abstract class TemporalParameter extends TemporalProposition
        implements Parameter {

    private static final long serialVersionUID = 1553031842442677948L;
    private Value value;

    TemporalParameter(String id, UniqueId uniqueId) {
        super(id, uniqueId);
    }

    /**
     * Here only for use by deserialization. Do not use this for any other
     * reason!
     */
    protected TemporalParameter() {
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public String getValueFormatted() {
        return this.value != null ? this.value.getFormatted() : "";
    }

    public void setValue(Value value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.TemporalProposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TemporalParameter)) {
            return false;
        }

        TemporalParameter p = (TemporalParameter) o;
        return super.isEqual(p)
                && (this.value == p.value
                || (this.value != null && this.value.equals(p.value)));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).toString();
    }

    protected void writeTemporalParameter(ObjectOutputStream s) 
            throws IOException {
        s.writeObject(this.value);
    }

    protected void readTemporalParameter(ObjectInputStream s) 
            throws IOException, ClassNotFoundException {
        Object obj = s.readObject();
        if (obj != null) {
            setValue(((Value) obj).replace());
        }
    }


}
