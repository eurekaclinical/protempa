/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
import org.protempa.ProtempaException;

/**
 * A state of affairs that, when interpreted over a time interval, can change
 * the interpretation (abstraction) of one or more parameters within the scope
 * of that time interval.
 * 
 * @author Andrew Post
 */
public final class Context extends TemporalProposition implements Serializable {

    private static final long serialVersionUID = -836727551420756824L;

    public Context(String id, UniqueId uniqueId) {
        super(id, uniqueId);
    }

    @Override
    public void setInterval(Interval interval) {
        super.setInterval(interval);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor) throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
        writeTemporalProposition(s);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        readAbstractProposition(s);
        readTemporalProposition(s);
    }
}
