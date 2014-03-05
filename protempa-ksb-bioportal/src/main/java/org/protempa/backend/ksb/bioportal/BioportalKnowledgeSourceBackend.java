package org.protempa.backend.ksb.bioportal;

/*
 * #%L
 * Protempa BioPortal Knowledge Source Backend
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

import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
@BackendInfo(displayName = "BioPortal Knowledge Source Backend")
public class BioportalKnowledgeSourceBackend extends AbstractCommonsKnowledgeSourceBackend {

    /* the ID of the database that holds the ontologies */
    private String databaseId;

    /* name of the table that holds the ontologies */
    private String ontologiesTable;

    /* connection to the database that holds the ontologies */
    private Connection conn;

    public String getDatabaseId() {
        return databaseId;
    }

    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getOntologiesTable() {
        return ontologiesTable;
    }

    @BackendProperty
    public void setOntologiesTable(String ontologiesTable) {
        this.ontologiesTable = ontologiesTable;
    }

    @Override
    public void initialize(BackendInstanceSpec config) throws BackendInitializationException {
        super.initialize(config);
        if (this.conn == null) {
            try {
                this.conn = DriverManager.getConnection(this.databaseId);
            } catch (SQLException e) {
                throw new KnowledgeSourceBackendInitializationException("Failed to initialize BioPortal knowledge source backend", e);
            }
        }
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public String[] readAbstractedInto(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readIsA(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public List<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException {
        return null;
    }
}
