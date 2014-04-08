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

import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
@BackendInfo(displayName = "BioPortal Knowledge Source Backend")
public class BioportalKnowledgeSourceBackend extends AbstractCommonsKnowledgeSourceBackend {

    private static final Logger logger = Logger.getLogger(BioportalKnowledgeSourceBackend.class.getName());

    private static final String DEFAULT_TABLE_NAME = "bioportal";

    /* database API to use */
    private DatabaseAPI databaseApi;

    /* the ID of the database that holds the ontologies */
    private String databaseId;

    /* the database username */
    private String username;

    /* the database password */
    private String password;

    /* name of the table that holds the ontologies */
    private String ontologiesTable;

    public BioportalKnowledgeSourceBackend() {
        this.databaseApi = DatabaseAPI.DRIVERMANAGER;
        this.ontologiesTable = DEFAULT_TABLE_NAME;
    }

    /**
     * Returns which Java database API this backend is configured to use.
     *
     * @return a {@link DatabaseAPI}. The default value is
     * {@link org.arp.javautil.sql.DatabaseAPI}<code>.DRIVERMANAGER</code>
     */
    public DatabaseAPI getDatabaseApi() {
        return databaseApi;
    }

    /**
     * Configures which Java database API to use ({@link java.sql.DriverManager}
     * or {@link javax.sql.DataSource}. If
     * <code>null</code>, the default is assigned
     * ({@link org.arp.javautil.sql.DatabaseAPI}<code>.DRIVERMANAGER</code>).
     *
     * @param databaseApi a {@link DatabaseAPI}.
     */
    public void setDatabaseApi(DatabaseAPI databaseApi) {
        this.databaseApi = databaseApi;
    }

    /**
     * Configures which Java database API to use ({@link java.sql.DriverManager}
     * or {@link javax.sql.DataSource} by parsing a {@link DatabaseAPI}'s name.
     * Cannot be null.
     *
     * @param databaseApiString a {@link DatabaseAPI}'s name.
     */
    @BackendProperty(propertyName = "databaseAPI")
    public void parseDatabaseApi(String databaseApiString) {
        setDatabaseApi(DatabaseAPI.valueOf(databaseApiString));
    }

    public String getDatabaseId() {
        return databaseId;
    }

    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getUsername() {
        return username;
    }

    @BackendProperty
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getOntologiesTable() {
        return ontologiesTable;
    }

    @BackendProperty
    public void setOntologiesTable(String ontologiesTable) {
        this.ontologiesTable = ontologiesTable;
    }

    private Connection openConnection() throws InvalidConnectionSpecArguments, SQLException {
        return this.databaseApi.newConnectionSpecInstance(this.databaseId, this.username, this.password).getOrCreate();
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to close BioPortal database", e);
        }
    }

    private static class BioportalTerm {
        String id;
        String displayName;
        String code;
        String ontology;
    }

    private BioportalTerm readFromDatabase(String id) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection();
             PreparedStatement findStmt = conn.prepareStatement("SELECT display_name, code, ontology FROM " + this.ontologiesTable + " WHERE term_id = ?")) {
            findStmt.setString(1, id);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                BioportalTerm result = new BioportalTerm();
                result.id = id;
                result.displayName = rs.getString(1);
                result.code = rs.getString(2);
                result.ontology = rs.getString(3);

                return result;
            }
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }

        return null;
    }

    private Set<String> readChildrenFromDatabase(String id) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<>();
        try (Connection conn = openConnection();
             PreparedStatement childrenStmt = conn.prepareStatement("SELECT term_id FROM " + this.ontologiesTable + " WHERE parent_id = ?")) {
            childrenStmt.setString(1, id);
            ResultSet rs = childrenStmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }

        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        logger.log(Level.FINEST, "Looking for proposition in {0}: {1}", new Object[]{ this.ontologiesTable, id });
        BioportalTerm term = readFromDatabase(id);
        if (term != null) {
            logger.log(Level.FINEST, "Found proposition id: {0}", id);
            EventDefinition result = new EventDefinition(id);
            result.setDisplayName(term.displayName);
            result.setAbbreviatedDisplayName(term.code);
            result.setInDataSource(true);

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
        logger.log(Level.FINER, "Failed to find proposition id: {0}", id);
        return null;
    }

    @Override
    public String[] readIsA(String propId) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection();
             PreparedStatement parentStmt = conn.prepareStatement("SELECT parent_id FROM " + this.ontologiesTable + " WHERE term_id = ?")) {
            List<String> result = new ArrayList<>();
            parentStmt.setString(1, propId);
            ResultSet rs = parentStmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result.toArray(new String[result.size()]);
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }
    }

    @Override
    public Set<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection();
             PreparedStatement searchStmt = conn.prepareStatement("SELECT term_id FROM " + this.ontologiesTable + " WHERE UPPER(display_name) LIKE UPPER(?)")) {
            Set<String> result = new HashSet<>();
            searchStmt.setString(1, "%" + searchKey + "%");
            ResultSet rs = searchStmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }
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
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }
}
