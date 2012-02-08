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
package org.protempa.proposition.visitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;

/**
 * An abstract base class for implementing proposition visitors. Except for
 * {@link #visit(Collection)} and {@link #visit(Map)}, the default
 * implementations are no-ops. Override those methods to implement your
 * visitor's functionality.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractPropositionVisitor
        implements PropositionVisitor {

    @Override
    public void visit(Map<String, List<Proposition>> finderResult) {
        for (List<Proposition> listOfProps : finderResult.values()) {
            visit(listOfProps);
        }

    }

    @Override
    public void visit(Collection<? extends Proposition> propositions) {
        for (Proposition proposition : propositions) {
            proposition.accept(this);
        }
    }

    /**
     * Processes abstract parameters. This default implementation is a no-op.
     *
     * @param abstractParameter
     *            an {@link AbstractParameter}.
     */
    @Override
    public void visit(AbstractParameter abstractParameter) {
    }

    /**
     * Processes events. This default implementation is a no-op.
     *
     * @param event
     *            an {@link Event}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(Event event) {
    }

    /**
     * Processes primitive parameters. This default implementation is a no-op.
     *
     * @param primitiveParameter
     *            an {@link PrimitiveParameter}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
    }

    /**
     * Processes constants. This default implementation is a no-op.
     *
     * @param primitiveParameter
     *            an {@link PrimitiveParameter}.
     */
    @Override
    public void visit(Constant constant) {
    }

    /**
     * Processes contexts. This default implementation is a no-op.
     *
     * @param context
     *            a {@link Context}.
     */
    @Override
    public void visit(Context context) {
    }
}
