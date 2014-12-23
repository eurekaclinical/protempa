package org.protempa.backend.ksb.i2b2;

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

import org.apache.commons.lang3.ArrayUtils;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.arrays.Arrays;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.proposition.value.ValueType;

/**
 * Implements a knowledge source backend based on a database export of BioPortal
 * ontologies. Since BioPortal only has standard ontologies (eg, ICD-9, CPT,
 * LOINC), all of the KnowledgeSourceBackend methods that look for higher
 * level abstractions return null or empty arrays. All terms in BioPortal
 * ontologies are assumed to be {@link org.protempa.EventDefinition} objects
 * in Protempa.
 */
@BackendInfo(displayName = "I2b2 Knowledge Source Backend")
public class I2b2KnowledgeSourceBackend extends AbstractCommonsKnowledgeSourceBackend {

    private static final Logger logger = Logger.getLogger(I2b2KnowledgeSourceBackend.class.getName());
    
    private static final String ONT_PATH_SEP = "\\";

    /* database API to use */
    private DatabaseAPI databaseApi;

    /* the ID of the database that holds the ontologies */
    private String databaseId;

    /* the database username */
    private String username;

    /* the database password */
    private String password;

    public I2b2KnowledgeSourceBackend() {
        this.databaseApi = DatabaseAPI.DRIVERMANAGER;
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

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return readTemporalPropositionDefinition(id);
    }

    @Override
    public String[] readIsA(String propId) throws KnowledgeSourceReadException {
        Set<String> parents = readLevelFromDatabase(propId, false);
        return parents.toArray(new String[parents.size()]);
    }

    @Override
    public Set<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection()) {
            Set<String> result = new HashSet<>();
            StringBuilder sql = new StringBuilder();
            List<String> ontTables = readOntologyTables(conn);
            if (ontTables.size() > 1) {
                sql.append('(');
            }
            for (String table : ontTables) {
                if (sql.length() > 0) {
                    sql.append(") UNION (");
                }
                sql.append("SELECT C_BASECODE FROM ");
                sql.append(table);
                sql.append(" WHERE UPPER(C_NAME) LIKE UPPER(?)");
            }
            if (ontTables.size() > 1) {
                sql.append(')');
            }
            String searchKeyEscaped = escapeLike(searchKey);
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0, n = ontTables.size(); i < n; i++) {
                    stmt.setString(i + 1, "%" + searchKeyEscaped + "%");
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                }
            }
            return result;
        } catch (InvalidConnectionSpecArguments | SQLException ex) {
            throw new KnowledgeSourceReadException(ex);
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
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Looking for proposition {0}", id);
        }
        TemporalPropositionDefinition result = readFromDatabase(id);
        if (result != null) {
            logger.log(Level.FINEST, "Found proposition id: {0}", id);

            return result;
        }
        logger.log(Level.FINER, "Failed to find proposition id: {0}", id);
        return null;
    }

    @Override
    public String[] readAbstractedInto(String propId) throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    
    private Connection openConnection() throws InvalidConnectionSpecArguments, SQLException {
        return this.databaseApi.newConnectionSpecInstance(this.databaseId, this.username, this.password).getOrCreate();
    }
    
    private List<String> readOntologyTables(Connection conn) throws KnowledgeSourceReadException {
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT C_TABLE_NAME FROM TABLE_ACCESS")) {
                List<String> tables = new ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
                return tables;
            }
        } catch (SQLException ex) {
            throw new KnowledgeSourceReadException(ex);
        }
    }

    private TemporalPropositionDefinition readFromDatabase(String id) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection()) {
            StringBuilder sql = new StringBuilder();
            List<String> ontTables = readOntologyTables(conn);
            if (ontTables.size() > 1) {
                sql.append('(');
            }
            for (String table : ontTables) {
                if (sql.length() > 0) {
                    sql.append(") UNION (");
                }
                sql.append("SELECT C_HLEVEL, C_NAME, C_FULLNAME, VALUETYPE_CD, C_COMMENT FROM ");
                sql.append(table);
                sql.append(" WHERE C_BASECODE=?");
            }
            if (ontTables.size() > 1) {
                sql.append(')');
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                String[] valueTypeCds = {"LAB", "DOC"};
                for (int i = 0, n = ontTables.size(); i < n; i++) {
                    stmt.setString(i + 1, id);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        if (Arrays.contains(valueTypeCds, rs.getString(4))) {
                            PrimitiveParameterDefinition result = new PrimitiveParameterDefinition(id);
                            result.setDisplayName(rs.getString(2));
                            result.setDescription(rs.getString(5));
                            result.setInDataSource(true);
                            result.setValueType(ValueType.VALUE);
                            Set<String> children = readLevelFromDatabaseHelper(rs.getInt(1), rs.getString(3), 1);
                            result.setInverseIsA(children.toArray(new String[children.size()]));
                            return result;
                        } else {
                            EventDefinition result = new EventDefinition(id);
                            result.setDisplayName(rs.getString(2));
                            result.setDescription(rs.getString(5));
                            result.setInDataSource(true);
                            Set<String> children = readLevelFromDatabaseHelper(rs.getInt(1), rs.getString(3), 1);
                            result.setInverseIsA(children.toArray(new String[children.size()]));
                            return result;
                        }
                        
                    }
                }
            }
            return null;
        } catch (InvalidConnectionSpecArguments | SQLException ex) {
            throw new KnowledgeSourceReadException(ex);
        }
    }

    private Set<String> readLevelFromDatabase(String id, boolean children) throws KnowledgeSourceReadException {
        try (Connection conn = openConnection()) {
            StringBuilder sql = new StringBuilder();
            List<String> ontTables = readOntologyTables(conn);
            if (ontTables.size() > 1) {
                sql.append('(');
            }
            for (String table : ontTables) {
                if (sql.length() > 0) {
                    sql.append(") UNION (");
                }
                sql.append("SELECT C_HLEVEL, C_FULLNAME FROM ");
                sql.append(table);
                sql.append(" WHERE C_BASECODE=?");
            }
            if (ontTables.size() > 1) {
                sql.append(')');
            }
            int c_hlevel;
            String fullName;
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0, n = ontTables.size(); i < n; i++) {
                    stmt.setString(i + 1, id);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        c_hlevel = rs.getInt(1);
                        fullName = rs.getString(2);
                    } else {
                        return Collections.emptySet();
                    }
                }
            }
            if (children) {
                return readLevelFromDatabaseHelper(c_hlevel, fullName, 1);
            } else {
                return readLevelFromDatabaseHelper(c_hlevel, fullName, -1);
            }
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }
    }
    
    private Set<String> readLevelFromDatabaseHelper(int c_hlevel, String fullName, int offset) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<>();
        try (Connection conn = openConnection()) {
            StringBuilder sql = new StringBuilder();
            List<String> ontTables = readOntologyTables(conn);
            
            String newFullName = newFullName(fullName, offset);
            
            if (ontTables.size() > 1) {
                sql.append('(');
            }
            for (String table : ontTables) {
                if (sql.length() > 0) {
                    sql.append(") UNION (");
                }
                sql.append("SELECT C_BASECODE FROM ");
                sql.append(table);
                sql.append(" WHERE C_HLEVEL=? AND C_FULLNAME LIKE ? ESCAPE '\\'");
            }
            if (ontTables.size() > 1) {
                sql.append(')');
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int j = 1;
                for (int i = 0, n = ontTables.size(); i < n; i++) {
                    stmt.setInt(j++, c_hlevel + offset);
                    stmt.setString(j++, newFullName + "%");
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                }
            }
        } catch (InvalidConnectionSpecArguments | SQLException e) {
            throw new KnowledgeSourceReadException(e);
        }

        return result;
    }

    private String newFullName(String fullName, int offset) {
        String newFullName = fullName;
        if (newFullName.length() == 0) {
            return newFullName;
        }
        if (newFullName.endsWith(ONT_PATH_SEP)) {
            newFullName = newFullName.substring(0, newFullName.length() - 1);
        }
        if (offset == -1) {
            int lastIndexOf = newFullName.lastIndexOf(ONT_PATH_SEP);
            if (lastIndexOf == -1) {
                newFullName = "";
            } else {
                newFullName = newFullName.substring(0, lastIndexOf);
            }
        }
        newFullName = escapeLike(newFullName);
        return newFullName;
    }
    
    private static String escapeLike(String str) {
        return str.replaceAll("[\\\\%_]", "\\\\$0");
    }
}
