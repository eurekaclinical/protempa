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
package org.protempa.proposition.visitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.ProtempaException;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
public class AbstractPropositionCheckedVisitor
        implements PropositionCheckedVisitor {

    @Override
    public void visit(Map<String, List<Proposition>> finderResult)
            throws ProtempaException {
        for (List<Proposition> listOfProps : finderResult.values()) {
            visit(listOfProps);
        }

    }

    @Override
    public void visit(Collection<? extends Proposition> propositions)
            throws ProtempaException {
        for (Proposition proposition : propositions) {
            proposition.acceptChecked(this);
        }
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter)
            throws ProtempaException {
    }

    @Override
    public void visit(Event event)
            throws ProtempaException {
    }

    @Override
    public void visit(AbstractParameter abstractParameter)
            throws ProtempaException {
    }

    @Override
    public void visit(Constant constantParameter)
            throws ProtempaException {
    }

    @Override
    public void visit(Context context) throws ProtempaException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
