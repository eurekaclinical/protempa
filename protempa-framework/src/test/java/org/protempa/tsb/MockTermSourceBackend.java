package org.protempa.tsb;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import org.protempa.BackendCloseException;
import org.protempa.BackendListener;
import org.protempa.Term;
import org.protempa.TermSourceReadException;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.tsb.TermSourceBackend;

import java.util.List;
import java.util.Map;
import org.protempa.ProtempaEventListener;

/**
 *
 */
@BackendInfo(displayName = "Mock Term Source Backend")
public class MockTermSourceBackend implements TermSourceBackend {

    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Term> readTerms(String[] ids) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getSubsumption(String id) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize(BackendInstanceSpec<?> config) throws BackendInitializationException {

    }

    @Override
    public String getDisplayName() {
        return "Mock Term Source Backend";
    }

    @Override
    public String getConfigurationsId() {
        return "";
    }

    @Override
    public void close() throws BackendCloseException {

    }

    @Override
    public void addBackendListener(BackendListener<TermSourceBackendUpdatedEvent> listener) {

    }

    @Override
    public void removeBackendListener(BackendListener<TermSourceBackendUpdatedEvent> listener) {

    }

    @Override
    public String getId() {
        return "mocktsb";
    }

    @Override
    public void setEventListeners(List<? extends ProtempaEventListener> eventListeners) {
        
    }
}
