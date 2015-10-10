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
package org.protempa.dest.deid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.PropositionDefinition;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerCloseException;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.dest.QueryResultsHandlerValidationFailedException;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Implements de-identification. Encrypts keyIds and offsets dates and times.
 *
 * @author Andrew Post
 */
public final class DeidentifiedQueryResultsHandler
        implements QueryResultsHandler {

    private final Encryption encryption;
    private final QueryResultsHandler handler;
    private final DeidConfig deidConfig;
    private boolean handlerClosed;
    private Map<String, PropositionDefinition> propDefCache;

    DeidentifiedQueryResultsHandler(QueryResultsHandler handler, DeidConfig deidConfig) throws EncryptionInitException {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }
        this.handler = handler;
        this.deidConfig = deidConfig;
        this.encryption = deidConfig.getEncryptionInstance();
    }

    @Override
    public void validate() throws QueryResultsHandlerValidationFailedException {
        this.handler.validate();
    }

    @Override
    public void start(Collection<PropositionDefinition> cache) throws QueryResultsHandlerProcessingException {
        this.propDefCache = new HashMap<>();
        for (PropositionDefinition propDef : cache) {
            this.propDefCache.put(propDef.getId(), propDef);
        }
        this.handler.start(cache);
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions, Map<Proposition, List<Proposition>> forwardDerivations, Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references) throws QueryResultsHandlerProcessingException {
        String encryptedKeyId;
        try {
            encryptedKeyId = this.encryption.encrypt(keyId, keyId);
        } catch (EncryptException ex) {
            throw new QueryResultsHandlerProcessingException("Could not encrypt keyId", ex);
        }

        Map<UniqueId, Proposition> deidentifiedPropsByUniqueId = new HashMap<>();
        List<Proposition> deidentifiedProps = new ArrayList<>(propositions.size());
        PropositionDeidentifierVisitor visitor = new PropositionDeidentifierVisitor(this.encryption, this.propDefCache, this.deidConfig.getOffset(keyId));
        visitor.setKeyId(keyId);

        for (Proposition prop : propositions) {
            prop.accept(visitor);
            Proposition deidentifiedProposition = visitor.getProposition();
            deidentifiedProps.add(deidentifiedProposition);
            deidentifiedPropsByUniqueId.put(deidentifiedProposition.getUniqueId(), deidentifiedProposition);
        }

        Map<UniqueId, Proposition> deidentifiedReferences = new HashMap<>();
        for (Map.Entry<UniqueId, Proposition> me : references.entrySet()) {
            Proposition prop = deidentifiedPropsByUniqueId.get(me.getKey());
            if (prop == null) {
                me.getValue().accept(visitor);
                prop = visitor.getProposition();
                deidentifiedPropsByUniqueId.put(prop.getUniqueId(), prop);
            }
            deidentifiedReferences.put(prop.getUniqueId(), prop);
        }

        Map<Proposition, List<Proposition>> deidentifiedForwardDerivations = new HashMap<>();
        for (Map.Entry<Proposition, List<Proposition>> me : forwardDerivations.entrySet()) {
            Proposition key = deidentifiedPropsByUniqueId.get(me.getKey().getUniqueId());
            if (key == null) {
                me.getKey().accept(visitor);
                key = visitor.getProposition();
                deidentifiedPropsByUniqueId.put(key.getUniqueId(), key);
            }
            List<Proposition> values = new ArrayList<>();
            for (Proposition val : me.getValue()) {
                Proposition prop = deidentifiedPropsByUniqueId.get(val.getUniqueId());
                if (prop == null) {
                    me.getKey().accept(visitor);
                    prop = visitor.getProposition();
                    deidentifiedPropsByUniqueId.put(prop.getUniqueId(), prop);
                }
                values.add(prop);
            }
            deidentifiedForwardDerivations.put(key, values);
        }

        Map<Proposition, List<Proposition>> deidentifiedBackwardDerivations = new HashMap<>();
        for (Map.Entry<Proposition, List<Proposition>> me : backwardDerivations.entrySet()) {
            Proposition key = deidentifiedPropsByUniqueId.get(me.getKey().getUniqueId());
            if (key == null) {
                me.getKey().accept(visitor);
                key = visitor.getProposition();
                deidentifiedPropsByUniqueId.put(key.getUniqueId(), key);
            }
            List<Proposition> values = new ArrayList<>();
            for (Proposition val : me.getValue()) {
                Proposition prop = deidentifiedPropsByUniqueId.get(val.getUniqueId());
                if (prop == null) {
                    me.getKey().accept(visitor);
                    prop = visitor.getProposition();
                    deidentifiedPropsByUniqueId.put(prop.getUniqueId(), prop);
                }
                values.add(prop);
            }
            deidentifiedBackwardDerivations.put(key, values);
        }

        this.handler.handleQueryResult(encryptedKeyId, deidentifiedProps, deidentifiedForwardDerivations, deidentifiedBackwardDerivations, deidentifiedReferences);
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        this.handler.finish();
    }

    @Override
    public void close() throws QueryResultsHandlerCloseException {
        try {
            this.handler.close();
            this.handlerClosed = true;
        } finally {
            try {
                this.deidConfig.close();
            } catch (Exception ex) {
                if (this.handlerClosed) {
                    throw new QueryResultsHandlerCloseException("Error cleaning up deidentification state");
                }
            }
        }
    }

    @Override
    public void cancel() {
        this.handler.cancel();
    }

}
