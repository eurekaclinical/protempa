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

import java.text.NumberFormat;
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
 * Implements de-identification. Only replaces key ids so far.
 *
 * @author Andrew Post
 */
public final class DeidentifiedQueryResultsHandler
        implements QueryResultsHandler {

    private static final ThreadLocal<NumberFormat> disguisedKeyFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getInstance();
        }
    };

    private final QueryResultsHandler handler;
    private final Map<String, String> keyMapper;
    private boolean keyIdDisguised;
    private int nextDisguisedKey;

    DeidentifiedQueryResultsHandler(QueryResultsHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }
        this.handler = handler;
        this.nextDisguisedKey = 1;
        this.keyIdDisguised = true;
        this.keyMapper = new HashMap<>();
    }

    /**
     * Returns whether key ids will be disguised. Default is <code>true</code>.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isKeyIdDisguised() {
        return keyIdDisguised;
    }

    /**
     * Sets whether to disguise key ids.
     *
     * @param keyDisguised <code>true</code> or <code>false</code>.
     */
    public void setKeyIdDisguised(boolean keyDisguised) {
        this.keyIdDisguised = keyDisguised;
    }
    
    @Override
    public void validate() throws QueryResultsHandlerValidationFailedException {
        this.handler.validate();
    }

    @Override
    public String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException {
        return this.handler.getPropositionIdsNeeded();
    }

    @Override
    public void start(Collection<PropositionDefinition> cache) throws QueryResultsHandlerProcessingException {
        this.handler.start(cache);
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions, Map<Proposition, List<Proposition>> forwardDerivations, Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references) throws QueryResultsHandlerProcessingException {
        keyId = disguise(keyId);
        this.handler.handleQueryResult(keyId, propositions, forwardDerivations, backwardDerivations, references);
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        this.handler.finish();
        this.keyMapper.clear();
    }

    @Override
    public void close() throws QueryResultsHandlerCloseException {
        this.handler.close();
    }

    @Override
    public void cancel() {
        this.handler.cancel();
    }

    private String disguise(String keyId) {
        if (this.keyIdDisguised) {
            if (this.keyMapper.containsKey(keyId)) {
                keyId = this.keyMapper.get(keyId);
            } else {
                keyId = disguisedKeyFormat.get().format(nextDisguisedKey++);
            }
        }
        return keyId;
    }

}
