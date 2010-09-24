package org.protempa.tsb.umls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.MalformedTermIdException;
import org.protempa.Term;
import org.protempa.TermSourceBackendInitializationException;
import org.protempa.TermSourceReadException;
import org.protempa.Terminology;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsTermSourceBackend;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;

import edu.emory.cci.aiw.umls.SAB;
import edu.emory.cci.aiw.umls.TerminologyCode;
import edu.emory.cci.aiw.umls.UMLSDatabaseConnection;
import edu.emory.cci.aiw.umls.UMLSPreferred;
import edu.emory.cci.aiw.umls.UMLSQueryException;
import edu.emory.cci.aiw.umls.UMLSQueryExecutor;
import edu.emory.cci.aiw.umls.UMLSQueryStringValue;

@BackendInfo(displayName = "UMLS term source backend")
public final class UMLSTermSourceBackend extends
        AbstractCommonsTermSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private UMLSQueryExecutor umls;

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

            SAB sab = umls.getSAB(
                    UMLSQueryStringValue.fromString(term.getTerminology()
                            .getName())).get(0);
            TerminologyCode code = TerminologyCode.fromStringAndSAB(id, sab);
            List<TerminologyCode> children = umls.getChildrenByCode(code);
            List<Term> childTerms = new ArrayList<Term>();
            for (TerminologyCode child : children) {
                childTerms.add(Term.fromTerminologyAndCode(term
                        .getTerminology(), code.getCode()));
            }
            term.setDirectChildren(childTerms.toArray(new Term[childTerms
                    .size()]));
            term.setSemanticType(umls.getSemanticType(
                    UMLSQueryStringValue.fromString(id), sab).get(0).getType());
            term.setDisplayName(umls.getSTR(
                    UMLSQueryStringValue.fromString(id), sab, null,
                    UMLSPreferred.PREFERRED).get(0).getValue());
//            term.setDescription(umls.getSTR(
//                    UMLSQueryStringValue.fromString(id), sab, null,
//                    UMLSPreferred.NO_PREFERENCE).get(0).getValue());

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

    @Override
    public void initialize(BackendInstanceSpec config)
            throws TermSourceBackendInitializationException {
        super.initialize(config);

        if (this.databaseAPI == null) {
            throw new TermSourceBackendInitializationException(
                    "Term source backend "
                            + nameForErrors()
                            + " requires a Java database API (DriverManager or "
                            + "DataSource) to be specified in its configuration");
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
