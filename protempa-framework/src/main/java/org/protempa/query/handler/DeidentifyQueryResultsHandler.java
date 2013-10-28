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
package org.protempa.query.handler;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 * Implements de-identification. Only replaces key ids so far.
 *
 * @author Andrew Post
 */
public final class DeidentifyQueryResultsHandler
        implements QueryResultsHandler {

    private static final long serialVersionUID = 4289223507110468993L;
    private static final ThreadLocal<NumberFormat> disguisedKeyFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getInstance();
        }
    };
    private boolean keyIdDisguised;
    private final QueryResultsHandler handler;
    private final Map<String, String> keyMapper;
    private int nextDisguisedKey;

    public DeidentifyQueryResultsHandler(QueryResultsHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }
        this.handler = handler;
        this.keyMapper = new HashMap<String, String>();
        this.keyIdDisguised = true;
        this.nextDisguisedKey = 1;
    }

    /**
     * Returns whether key ids will be disguised. Default is
     * <code>true</code>.
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
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws QueryResultsHandlerProcessingException {
        keyId = disguiseKeyIds(keyId);
        this.handler.handleQueryResult(keyId, propositions, forwardDerivations,
                backwardDerivations, references);
    }

    @Override
    public void init(KnowledgeSource knowledgeSource, Query query) 
            throws QueryResultsHandlerInitException {
        this.handler.init(knowledgeSource, query);
        this.nextDisguisedKey = 1;
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        this.handler.finish();
        this.keyMapper.clear();
    }

    private String disguiseKeyIds(String keyId) {
        if (this.keyIdDisguised) {
            if (this.keyMapper.containsKey(keyId)) {
                keyId = this.keyMapper.get(keyId);
            } else {
                keyId = disguisedKeyFormat.get().format(nextDisguisedKey++);
            }
        }
        return keyId;
    }

    /**
     * Delegates to the query results handler passed into the constructor.
     * 
     * @throws QueryResultsHandlerValidationFailedException if the query 
     * results handler passed into the constructor is invalid.
     */
    @Override
    public void validate() throws QueryResultsHandlerValidationFailedException, 
            KnowledgeSourceReadException {
        this.handler.validate();
    }

    /**
     * Delegates to the query results handler passed into the constructor.
     *
     * @return an array of proposition id {@link String}s.
     */
    @Override
    public String[] getPropositionIdsNeeded() 
            throws KnowledgeSourceReadException {
        return this.handler.getPropositionIdsNeeded();
    }

    @Override
    public void start() throws QueryResultsHandlerProcessingException {
        this.handler.start();
    }

    @Override
    public void  close() throws QueryResultsHandlerCloseException {
        this.handler.close();
    }
}
