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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;

/**
 * A parameter with no temporal component.
 * 
 * @author Andrew Post
 */
public final class Constant extends AbstractProposition 
        implements Serializable {

    private static final long serialVersionUID = 7205801414947324421L;

    /**
     * Creates a constant with an identifier <code>String</code>.
     * 
     * @param id
     *            an identifier <code>String</code>.
     * @param uniqueId
     *            a <code>UniqueId</code> that uniquely identifies this
     *            propositions.
     */
    public Constant(String id, UniqueId uniqueId) {
        super(id, uniqueId);
    }

    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Constant)) {
            return false;
        }

        Constant p = (Constant) o;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .toString();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        readAbstractProposition(s);
    }
}
