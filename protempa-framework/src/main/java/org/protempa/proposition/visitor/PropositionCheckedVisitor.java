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
 * Interface for classes that do processing on propositions.
 * 
 * @author Andrew Post
 * 
 */
public interface PropositionCheckedVisitor {

    /**
     * Processes results from a PROTEMPA finder method.
     * 
     * @param finderResult
     *            a {@link Map<String, List<Proposition>>}.
     */
    void visit(Map<String, List<Proposition>> finderResult)
            throws ProtempaException;

    /**
     * Processes a collection of propositions.
     * 
     * @param propositions
     *            a {@link Collection<Proposition>}. Cannot be
     *            <code>null</code>.
     */
    void visit(Collection<? extends Proposition> propositions)
            throws ProtempaException;

    /**
     * Processes a primitive parameter.
     * 
     * @param primitiveParameter
     *            a {@link PrimitiveParameter}. Cannot be <code>null</code>.
     */
    void visit(PrimitiveParameter primitiveParameter) throws ProtempaException;

    /**
     * Processes an event.
     * 
     * @param event
     *            an {@link Event}. Cannot be <code>null</code>.
     */
    void visit(Event event) throws ProtempaException;

    /**
     * Processes an abstract parameter.
     * 
     * @param abstractParameter
     *            an {@link AbstractParameter}. Cannot be <code>null</code>.
     */
    void visit(AbstractParameter abstractParameter) throws ProtempaException;

    /**
     * Processes a constant parameter.
     * 
     * @param constantParameter
     *            an {@link ConstantParameter}. Cannot be <code>null</code>.
     */
    void visit(Constant constantParameter) throws ProtempaException;

    /**
     * Processes a context.
     * 
     * @param context
     *            a {@link Context}. Cannot be <code>null</code>.
     */
    void visit(Context context) throws ProtempaException;
}
