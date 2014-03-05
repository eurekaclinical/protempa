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
import org.protempa.EventDefinition;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /* prepared statement for retrieving terms from the database */
    private PreparedStatement findStmt;

    /* prepared statement for retrieving the children of a term */
    private PreparedStatement childrenStmt;

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
                this.findStmt = conn.prepareStatement("SELECT display_name, code, ontology FROM " + this.ontologiesTable + " WHERE purl_id = ?");
                this.childrenStmt = conn.prepareStatement("SELECT purl_id FROM " + this.ontologiesTable + " WHERE parent_id = ?");
            } catch (SQLException e) {
                throw new KnowledgeSourceBackendInitializationException("Failed to initialize BioPortal knowledge source backend", e);
            }
        }
    }

    private static class BioportalTerm {
        String id;
        String displayName;
        String code;
        String ontology;
    }

    private BioportalTerm readFromDatabase(String id) throws KnowledgeSourceReadException {
        try {
            this.findStmt.setString(1, id);
            ResultSet rs = this.findStmt.executeQuery();
            if (rs.next()) {
                BioportalTerm result = new BioportalTerm();
                result.id = id;
                result.displayName = rs.getString(1);
                result.code = rs.getString(2);
                result.ontology = rs.getString(3);

                return result;
            }
        } catch (SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }

        return null;
    }

    private Set<String> readChildrenFromDatabase(String id) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<>();
        try {
            this.childrenStmt.setString(1, id);
            ResultSet rs = this.childrenStmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }

        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        BioportalTerm term = readFromDatabase(id);
        if (term != null) {
            EventDefinition result = new EventDefinition(id);
            result.setDisplayName(term.displayName);
            result.setAbbreviatedDisplayName(term.code);

            Set<String> children = readChildrenFromDatabase(id);
            String[] iia = new String[children.size()];

            int i = 0;
            for (String child : children) {
                iia[i] = child;
                i++;
            }
            result.setInverseIsA(iia);

            return result;
        }
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
