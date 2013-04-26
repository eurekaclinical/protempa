/*
 * #%L
 * Protempa UMLS Term Source Backend
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
package org.protempa.backend.tsb.umls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.MalformedTermIdException;
import org.protempa.Term;
import org.protempa.backend.TermSourceBackendInitializationException;
import org.protempa.TermSourceReadException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.AbstractCommonsTermSourceBackend;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

import edu.emory.cci.aiw.umls.SAB;
import edu.emory.cci.aiw.umls.TerminologyCode;
import edu.emory.cci.aiw.umls.UMLSDatabaseConnection;
import edu.emory.cci.aiw.umls.UMLSNoSuchTermException;
import edu.emory.cci.aiw.umls.UMLSQueryException;
import edu.emory.cci.aiw.umls.UMLSQueryExecutor;
import org.protempa.backend.BackendInitializationException;

@BackendInfo(displayName = "UMLS term source backend")
public final class UMLSTermSourceBackend extends
        AbstractCommonsTermSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private UMLSQueryExecutor umls;

    public UMLSTermSourceBackend() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.AbstractTermSourceBackend#readTerm(java.lang.String,
     * org.protempa.Terminology)
     */
    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        try {
            Term term = Term.withId(id);

            // SAB sab = sabFromTerm(term);
            SAB sab = SAB.withName(term.getTerminology().getName());
            TerminologyCode code = TerminologyCode.fromStringAndSAB(
                    term.getCode(), sab);
            List<TerminologyCode> children = umls.getChildrenByCode(code);
            List<String> childTerms = new ArrayList<String>();
            for (TerminologyCode child : children) {
                childTerms.add(Term.fromTerminologyAndCode(sab.getName(),
                        child.getCode()).getId());
            }
            term.setDirectChildren(childTerms.toArray(new String[childTerms
                    .size()]));
            term.setSemanticType(umls.getSemanticTypeForTerm(code).getType());
            term.setDisplayName(umls.getPreferredName(code));
            term.setDescription(umls.getTermDefinition(code));

            return term;
        } catch (UMLSQueryException ue) {
            throw new TermSourceReadException(ue);
        } catch (MalformedTermIdException te) {
            throw new TermSourceReadException(te);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TermSourceBackend#readTerms(java.lang.String[],
     * org.protempa.Terminology)
     */
    @Override
    public Map<String, Term> readTerms(String[] ids)
            throws TermSourceReadException {
        Map<String, Term> result = new HashMap<String, Term>();

        for (String id : ids) {
            result.put(id, readTerm(id));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TermSourceBackend#getDescendents(java.lang.String)
     */
    @Override
    public List<String> getSubsumption(String id)
            throws TermSourceReadException {
        List<String> result = new ArrayList<String>();

        try {
            Term term = Term.withId(id);
            SAB sab = SAB.withName(term.getTerminology().getName());
            TerminologyCode code = TerminologyCode.fromStringAndSAB(
                    term.getCode(), sab);

            for (TerminologyCode tc : umls.getTermSubsumption(code)) {
                result.add(Term.fromTerminologyAndCode(tc.getSab().getName(),
                        tc.getCode()).getId());
            }
        } catch (UMLSNoSuchTermException ex) {
            throw new TermSourceReadException(ex);
        } catch (UMLSQueryException ex) {
            throw new TermSourceReadException(ex);
        } catch (MalformedTermIdException ex) {
            throw new TermSourceReadException(ex);
        }

        return result;
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        super.initialize(config);

        if (this.databaseAPI == null) {
            throw new TermSourceBackendInitializationException(
                    "Term source backend "
                            + nameForErrors()
                            + " requires a Java database API (DRIVERMANAGER or "
                            + "DATASOURCE) to be specified in its configuration");
        }
        umls = UMLSDatabaseConnection.getConnection(databaseAPI,
                getDatabaseId(), username, password);
    }

    /**
     * @return the databaseAPI
     */
    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }

    /**
     * @param databaseAPI
     *            the databaseAPI to set
     */
    public void setDatabaseAPI(DatabaseAPI databaseAPI) {
        this.databaseAPI = databaseAPI;
    }

    /**
     * Configures which Java database API to use ({@link java.sql.DriverManager}
     * or {@link javax.sql.DataSource} by parsing a {@link DatabaseAPI}'s name.
     * Cannot be null.
     * 
     * @param databaseAPIString
     *            a {@link DatabaseAPI}'s name.
     */
    @BackendProperty(propertyName = "databaseAPI")
    public void parseDatabaseAPI(String databaseAPIString) {
        setDatabaseAPI(DatabaseAPI.valueOf(databaseAPIString));
    }

    /**
     * @return the databaseId
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * @param databaseId
     *            the databaseId to set
     */
    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    @BackendProperty
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
