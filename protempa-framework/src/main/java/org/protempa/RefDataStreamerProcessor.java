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
package org.protempa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.proposition.*;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 *
 * @author Andrew Post
 */
final class RefDataStreamerProcessor
        extends DataStreamerProcessor<UniqueIdPair> {
    
    private static final Logger LOGGER = Logger.getLogger(RefDataStreamerProcessor.class.getName());

    private static class ReferenceAdder extends AbstractPropositionVisitor {

        private String referenceName;
        private UniqueId reference;

        @Override
        public void visit(AbstractParameter abstractParameter) {
            throw new AssertionError(
                    "Should not have been passed abstract parameter: " 
                    + abstractParameter);
        }

        @Override
        public void visit(Event event) {
            event.addReference(this.referenceName, this.reference);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) {
            primitiveParameter.addReference(this.referenceName, 
                    this.reference);
        }

        @Override
        public void visit(Constant constant) {
            constant.addReference(this.referenceName, this.reference);
        }

        @Override
        public void visit(Context context) {
            context.addReference(this.referenceName, this.reference);
        }

        void setParameters(String referenceName, UniqueId reference) {
            this.referenceName = referenceName;
            this.reference = reference;
        }
    }
    private final Map<UniqueId, Proposition> uniqueIdToPropositions;
    private final ReferenceAdder adder = new ReferenceAdder();

    RefDataStreamerProcessor() {
        this.uniqueIdToPropositions = new HashMap<>();
    }

    void setPropositions(List<? extends Proposition> propositions) {
        assert propositions != null : "propositions cannot be null";
        LOGGER.log(Level.FINEST, 
                "Setting propositions for refs: {0}", 
                new Object[]{propositions == null? 0: propositions.size()});
        for (Proposition proposition : propositions) {
            assert proposition != null : "proposition cannot be null";
            LOGGER.log(Level.FINEST, 
                    "Proposition: {0}", 
                    new Object[]{proposition == null? "null": proposition.getId()});
            this.uniqueIdToPropositions.put(
                    proposition.getUniqueId(), proposition);
        }
    }

    void clear() {
        this.uniqueIdToPropositions.clear();
    }

    @Override
    protected void fireKeyCompleted(String keyId, List<UniqueIdPair> data) throws DataSourceReadException {
        assert keyId != null : "keyId cannot be null";
        assert data != null : "data cannot be null";
        assert getKeyId() == null || keyId.equals(getKeyId()) : "incompatible keyId: expected "
                + getKeyId() + " but got " + keyId;
        for (UniqueIdPair pair : data) {
            Proposition prop = getProposition(pair.getProposition());
            if (prop != null) {
                this.adder.setParameters(pair.getReferenceName(),
                        pair.getReference());
                prop.accept(this.adder);
            } else {
                LOGGER.log(Level.WARNING, 
                        "Can''t find left-hand-side of reference: keyId={0}; references: {1}", 
                        new Object[]{keyId, pair});
            }
        }
    }

    private Proposition getProposition(UniqueId uniqueId) {
        return this.uniqueIdToPropositions.get(uniqueId);
    }
}
