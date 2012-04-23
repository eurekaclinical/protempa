/*
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Inserts sample data into a databse
 * 
 * 
 * @author Himanshu Rathod, Michel Mansour
 *
 */
public final class DataInserter {

    private static final String PATIENT = "patient";
    private static final String ENCOUNTER = "encounter";
    private static final String PROVIDER = "provider";
    private static final String CPT = "cpt_event";
    private static final String ICD9D = "icd9d_event";
    private static final String ICD9P = "icd9p_event";
    private static final String LABS = "labs_event";
    private static final String MEDS = "meds_event";
    private static final String VITALS = "vitals_event";

    private static final String TABLES[] = new String[] { PATIENT, ENCOUNTER,
            PROVIDER, CPT, ICD9D, ICD9P, LABS, MEDS, VITALS };

    private final Connection connection;

    /**
     * Initializes with a database driver.
     * 
     * @param connectionString
     *            the connection string describing the connection
     * 
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public DataInserter(String connectionString) throws SQLException {
        this.connection = DriverManager.getConnection(connectionString);
    }

    /**
     * Closes the connection. Must be called when data insertion is complete.
     * 
     * @throws SQLException
     */
    public void close() throws SQLException {
        this.connection.close();
    }

    private Connection getConnection() throws SQLException {
        return connection;
    }

    /**
     * Truncate all the tables that we will be inserting into later.
     * 
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    private void truncateTables() throws SQLException {
        final Connection connection = this.getConnection();
        final List<String> sqlStatements = new ArrayList<String>();
        for (String table : TABLES) {
            sqlStatements.add("truncate table " + table);
        }

        for (String sql : sqlStatements) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        }
        connection.commit();

    }

    public void createTables(String createScriptFile) throws SQLException {
        getConnection().createStatement().execute(
                "RUNSCRIPT FROM '" + createScriptFile + "'");
    }

    public int getCount(String table) throws SQLException {
        ResultSet rs = getConnection().createStatement().executeQuery(
                "SELECT COUNT(*) FROM " + table);
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return -1;
        }

    }

    /**
     * Insert a list of patients to the data base using the given connection.
     * 
     * @param patients
     *            The list of patients to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertPatients(List<Patient> patients) throws SQLException {
        int counter = 0;
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into patient values (?,?,?,?,?,?,?,?)");
        for (Patient patient : patients) {
            Date dateOfBirth;
            if (patient.getDateOfBirth() == null) {
                dateOfBirth = null;
            } else {
                dateOfBirth = new Date(patient.getDateOfBirth().getTime());
            }
            preparedStatement.setLong(1, patient.getId().longValue());
            preparedStatement.setString(2, patient.getFirstName());
            preparedStatement.setString(3, patient.getLastName());
            preparedStatement.setDate(4, dateOfBirth);
            preparedStatement.setString(5, patient.getLanguage());
            preparedStatement.setString(6, patient.getMaritalStatus());
            preparedStatement.setString(7, patient.getRace());
            preparedStatement.setString(8, patient.getGender());
            preparedStatement.addBatch();

            counter++;
            if (counter >= 128) {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
                counter = 0;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.clearBatch();
        preparedStatement.close();
    }

    /**
     * Insert the given list of encounters to a target database using the given
     * connection.
     * 
     * @param encounters
     *            The list of encounters to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertEncounters(List<Encounter> encounters)
            throws SQLException {
        int counter = 0;
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into encounter values (?,?,?,?,?,?,?)");
        for (Encounter encounter : encounters) {
            preparedStatement.setLong(1, encounter.getId().longValue());
            preparedStatement.setLong(2, encounter.getPatientId().longValue());
            preparedStatement.setLong(3, encounter.getProviderId().longValue());
            preparedStatement.setTimestamp(4, new Timestamp(encounter
                    .getStart().getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(encounter.getEnd()
                    .getTime()));
            preparedStatement.setString(6, encounter.getType());
            preparedStatement.setString(7, encounter.getDischargeDisposition());
            preparedStatement.addBatch();

            counter++;
            if (counter >= 128) {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
                counter = 0;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.clearBatch();
        preparedStatement.close();
    }

    /**
     * Insert the given list of providers to a target database using the given
     * connection.
     * 
     * @param providers
     *            The list of providers to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertProviders(List<Provider> providers) throws SQLException {
        int counter = 0;
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into provider values (?,?,?)");
        for (Provider provider : providers) {
            preparedStatement.setLong(1, provider.getId().longValue());
            preparedStatement.setString(2, provider.getFirstName());
            preparedStatement.setString(3, provider.getLastName());
            preparedStatement.addBatch();

            counter++;
            if (counter >= 128) {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
                counter = 0;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.clearBatch();
        preparedStatement.close();
        ;
    }

    /**
     * Insert the given list of CPT codes to a target database using the given
     * connection.
     * 
     * @param cptCodes
     *            The list of CPT codes to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertCptCodes(List<CPT> cptCodes) throws SQLException {
        this.insertObservations(cptCodes, "cpt_event");
    }

    /**
     * Insert the given list of ICD9 diagnosis codes to a target database using
     * the given connection.
     * 
     * @param diagnoses
     *            The list of diagnosis codes to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertIcd9Diagnoses(List<Icd9Diagnosis> diagnoses)
            throws SQLException {
        this.insertObservations(diagnoses, "icd9d_event");
    }

    /**
     * Insert the given list of ICD9 procedure codes to a target database using
     * the given connection.
     * 
     * @param procedures
     *            The list of procedure codes to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertIcd9Procedures(List<Icd9Procedure> procedures)
            throws SQLException {
        this.insertObservations(procedures, "icd9p_event");
    }

    /**
     * Insert the given list of medications to a target database using the given
     * connection.
     * 
     * @param medications
     *            The list of medications to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertMedications(List<Medication> medications)
            throws SQLException {
        this.insertObservations(medications, "meds_event");
    }

    /**
     * Insert the given list of lab results to a target database using the given
     * connection.
     * 
     * @param labs
     *            The list of lab results to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertLabs(List<Lab> labs) throws SQLException {
        this.insertObservationsWithResult(labs, "labs_event");
    }

    /**
     * Insert the given list of vital signs to a target database using the given
     * connection.
     * 
     * @param vitals
     *            The list of vitals to insert.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    public void insertVitals(List<Vital> vitals) throws SQLException {
        this.insertObservationsWithResult(vitals, "vitals_event");
    }

    /**
     * Add the given list of observation objects to a target database using the
     * given connection.
     * 
     * @param observations
     *            The list of observations to insert.
     * @param table
     *            The table in which the observations should be inserted.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    private void insertObservations(List<? extends Observation> observations,
            String table) throws SQLException {
        int counter = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ").append(table)
                .append(" values (?,?,?,?)");
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement(sqlBuilder.toString());
        for (Observation observation : observations) {
            preparedStatement.setString(1, observation.getId());
            preparedStatement.setLong(2, observation.getEncounterId()
                    .longValue());
            preparedStatement.setTimestamp(3, new Timestamp(observation
                    .getTimestamp().getTime()));
            preparedStatement.setString(4, observation.getEntityId());
            preparedStatement.addBatch();

            counter++;
            if (counter >= 128) {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
                counter = 0;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.clearBatch();
        preparedStatement.close();
        ;
    }

    /**
     * Insert a list of observations and their related results to a target
     * database using the given connection.
     * 
     * @param observations
     *            The observations to insert.
     * @param table
     *            The table in which the observations should be inserted.
     * @throws SQLException
     *             Thrown if there are any JDBC errors.
     */
    private void insertObservationsWithResult(
            List<? extends ObservationWithResult> observations, String table)
            throws SQLException {
        int counter = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ").append(table)
                .append(" values (?,?,?,?,?,?,?,?)");
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement(sqlBuilder.toString());
        for (ObservationWithResult observation : observations) {
            preparedStatement.setString(1, observation.getId());
            preparedStatement.setLong(2, observation.getEncounterId()
                    .longValue());
            preparedStatement.setTimestamp(3, new Timestamp(observation
                    .getTimestamp().getTime()));
            preparedStatement.setString(4, observation.getEntityId());
            preparedStatement.setString(5, observation.getResultAsStr());
            preparedStatement.setDouble(6, observation.getResultAsNum()
                    .doubleValue());
            preparedStatement.setString(7, observation.getUnits());
            preparedStatement.setString(8, observation.getFlag());
            preparedStatement.addBatch();

            counter++;
            if (counter >= 128) {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
                counter = 0;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.clearBatch();
        preparedStatement.close();
        ;
    }

}
