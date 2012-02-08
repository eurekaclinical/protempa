/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.protempa.KnowledgeSourceReadException;

/**
 * Factory for constructing a PROTEMPA proposition definition from a Protege
 * proposition.
 * 
 * @author Andrew Post
 */
final class InstanceConverterFactory {

    private final ConnectionManager connectionManager;
    private final PropositionConverter primitiveParameterConverter;
    private final PropositionConverter eventConverter;
    private final PropositionConverter constantConverter;
    private final PropositionConverter lowLevelAbstractionConverter;
    private final PropositionConverter sliceConverter;
    private final PropositionConverter highLevelAbstractionConverter;
    private final PropositionConverter pairAbstractionConverter;
    private Cls primitiveParameterCls;
    private Cls simpleAbstractionCls;
    private Cls sliceAbstractionCls;
    private Cls complexAbstractionCls;
    private Cls eventCls;
    private Cls constantCls;
    private Cls pairAbstractionCls;

    InstanceConverterFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.primitiveParameterConverter =
                new PrimitiveParameterConverter();
        this.eventConverter = new EventConverter();
        this.constantConverter = new ConstantConverter();
        this.lowLevelAbstractionConverter = new LowLevelAbstractionConverter();
        this.sliceConverter = new SliceConverter();
        this.highLevelAbstractionConverter =
                new HighLevelAbstractionConverter();
        this.pairAbstractionConverter = new PairAbstractionConverter();
    }

    /**
     * Gets an appropriate {@link PropositionConverter} for
     * constructing a PROTEMPA proposition definition from the given Protege
     * proposition instance.
     *
     * @param proposition
     *            a Protege proposition {@link Proposition} instance.
     * 
     * @return an appropriate {@link PropositionConverter} object,
     *         or <code>null</code> if the given <code>proposition</code> is
     *         <code>null</code>.
     * @throws AssertionError if the given <code>proposition</code> does not
     * have a type in the Protege <code>Proposition</code> class hierarchy.
     */
    PropositionConverter getInstance(Instance proposition)
            throws KnowledgeSourceReadException {
        if (proposition == null) {
            return null;
        } else {
            if (this.primitiveParameterCls == null) {
                this.primitiveParameterCls =
                        this.connectionManager.getCls("PrimitiveParameter");
            }
            if (this.simpleAbstractionCls == null) {
                this.simpleAbstractionCls =
                        this.connectionManager.getCls("SimpleAbstraction");
            }
            if (this.sliceAbstractionCls == null) {
                this.sliceAbstractionCls =
                        this.connectionManager.getCls("SliceAbstraction");
            }
            if (this.complexAbstractionCls == null) {
                this.complexAbstractionCls =
                        this.connectionManager.getCls("ComplexAbstraction");
            }
            if (this.eventCls == null) {
                this.eventCls =
                        this.connectionManager.getCls("Event");
            }
            if (this.constantCls == null) {
                this.constantCls =
                        this.connectionManager.getCls("Constant");
            }
            if (this.pairAbstractionCls == null) {
                this.pairAbstractionCls =
                        this.connectionManager.getCls("PairAbstraction");
            }
            if (this.connectionManager.hasType(proposition, this.eventCls)) {
                return this.eventConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.primitiveParameterCls)) {
                return this.primitiveParameterConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.constantCls)) {
                return this.constantConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.simpleAbstractionCls)) {
                return this.lowLevelAbstractionConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.sliceAbstractionCls)) {
                return this.sliceConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.complexAbstractionCls)) {
                return this.highLevelAbstractionConverter;
            } else if (this.connectionManager.hasType(proposition,
                    this.pairAbstractionCls)) {
                return this.pairAbstractionConverter;
            } else {
                String name = proposition.getName();
                Collection directTypes = proposition.getDirectTypes();
                String directTypesStr = StringUtils.join(directTypes, ", ");
                throw new AssertionError("Proposition " + name + 
                        " has invalid types: " + directTypesStr);
            }
        }
    }

    void close() {
        this.primitiveParameterCls = null;
        this.simpleAbstractionCls = null;
        this.sliceAbstractionCls = null;
        this.complexAbstractionCls = null;
        this.eventCls = null;
        this.constantCls = null;
        this.pairAbstractionCls = null;
    }
}
