/*
 * #%L
 * Protempa Test Suite
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Inserts sample data into a database
 *
 *
 * @author Himanshu Rathod, Michel Mansour
 *
 */
final class DataInserter {

    private static final String PATIENT = "patient";
    private static final String ENCOUNTER = "encounter";
    private static final String PROVIDER = "provider";
    private static final String CPT = "cpt_event";
    private static final String ICD9D = "icd9d_event";
    private static final String ICD9P = "icd9p_event";
    private static final String LABS = "labs_event";
    private static final String MEDS = "meds_event";
    private static final String VITALS = "vitals_event";
    private static final String SCHEMA = "TEST";

    private static final String TABLES[] = new String[]{PATIENT, ENCOUNTER,
        PROVIDER, CPT, ICD9D, ICD9P, LABS, MEDS, VITALS};

    private final Connection connection;

    /**
     * Initializes with a database driver.
     *
     * @param connectionString the connection string describing the connection
     *
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    DataInserter(String connectionString) throws SQLException {
        this.connection = DriverManager.getConnection(connectionString);
        this.connection.setAutoCommit(false);
    }

    /**
     * Closes the connection. Must be called when data insertion is complete.
     *
     * @throws SQLException
     */
    void close() throws SQLException {
        this.connection.close();
    }

    /**
     * Truncate all the tables that we will be inserting into later.
     *
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void truncateTables() throws SQLException {
        final List<String> sqlStatements = new ArrayList<>();
        for (String table : TABLES) {
            sqlStatements.add("truncate table " + table);
        }
        sqlStatements.add("drop schema " + SCHEMA);

        try {
            for (String sql : sqlStatements) {
                try (Statement statement = this.connection.createStatement()) {
                    statement.executeUpdate(sql);
                }
            }
            this.connection.commit();
        } catch (SQLException ex) {
            try {
                this.connection.rollback();
            } catch (SQLException ignore) {}
            throw ex;
        }

    }

    /**
     * Insert the given stream of ICD9 diagnosis codes to a target database using
     * the given connection.
     *
     * @param diagnoses The diagnosis codes to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertIcd9Diagnoses(Stream<Icd9Diagnosis> diagnoses)
            throws SQLException {
        this.insertObservations(diagnoses, "icd9d_event");
    }

    /**
     * Insert the given stream of ICD9 procedure codes to a target database using
     * the given connection.
     *
     * @param procedures The procedure codes to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertIcd9Procedures(Stream<Icd9Procedure> procedures)
            throws SQLException {
        this.insertObservations(procedures, "icd9p_event");
    }

    /**
     * Insert the given stream of medications to a target database using the given
     * connection.
     *
     * @param medications The medications to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertMedications(Stream<Medication> medications)
            throws SQLException {
        this.insertObservations(medications, "meds_event");
    }

    /**
     * Insert the given stream of lab results to a target database using the given
     * connection.
     *
     * @param labs The lab results to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertLabs(Stream<Lab> labs) throws SQLException {
        this.insertObservationsWithResult(labs, "labs_event");
    }

    /**
     * Insert the given list of vital signs to a target database using the given
     * connection.
     *
     * @param vitals The vitals to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertVitals(Stream<Vital> vitals) throws SQLException {
        this.insertObservationsWithResult(vitals, "vitals_event");
    }

    /**
     * Add the given list of observation objects to a target database using the
     * given connection.
     *
     * @param observations The list of observations to insert.
     * @param table The table in which the observations should be inserted.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    private <T extends Observation> void insertObservations(Stream<T> observations,
            String table) throws SQLException {
        int counter = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ").append(table)
                .append(" values (?,?,?,?,?,?,?)");
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(sqlBuilder.toString())) {
            for (T observation : (Iterable<T>) observations::iterator) {
                preparedStatement.setString(1, observation.getId());
                preparedStatement.setLong(2, observation.getEncounterId()
                        .longValue());
                preparedStatement.setTimestamp(3, new Timestamp(observation
                        .getTimestamp().getTime()));
                preparedStatement.setString(4, observation.getEntityId());
                preparedStatement.setTimestamp(5, toTimestamp(observation.getCreateDate()));
                preparedStatement.setTimestamp(6, toTimestamp(observation.getUpdateDate()));
                preparedStatement.setTimestamp(7, toTimestamp(observation.getDeleteDate()));
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
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw ex;
        }
    }
    
    /**
     * Insert a stream of observations and their related results to a target
     * database using the given connection.
     *
     * @param observations The observations to insert.
     * @param table The table in which the observations should be inserted.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    private <T extends ObservationWithResult> void insertObservationsWithResult(
            Stream<T> observations, String table)
            throws SQLException {
        int counter = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ").append(table)
                .append(" values (?,?,?,?,?,?,?,?,?,?,?)");
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(sqlBuilder.toString())) {
            for (T observation : (Iterable<T>) observations::iterator) {
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
                preparedStatement.setTimestamp(9, toTimestamp(observation.getCreateDate()));
                preparedStatement.setTimestamp(10, toTimestamp(observation.getUpdateDate()));
                preparedStatement.setTimestamp(11, toTimestamp(observation.getDeleteDate()));
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
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw ex;
        }

    }

    /**
     * Insert a stream of patients to the data base using the given connection.
     *
     * @param patients The stream of patients to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertPatients(Stream<Patient> patients) throws SQLException {
        int counter = 0;
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("insert into patient values (?,?,?,?,?,?,?,?,?,?,?)")) {
            for (Patient patient : (Iterable<Patient>) patients::iterator) {
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
                preparedStatement.setTimestamp(9, toTimestamp(patient.getCreateDate()));
                preparedStatement.setTimestamp(10, toTimestamp(patient.getUpdateDate()));
                preparedStatement.setTimestamp(11, toTimestamp(patient.getDeleteDate()));
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
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw ex;
        }
    }

    /**
     * Insert the given stream of encounters to a target database using the given
     * connection.
     *
     * @param encounters The stream of encounters to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertEncounters(Stream<Encounter> encounters)
            throws SQLException {
        int counter = 0;
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("insert into encounter values (?,?,?,?,?,?,?,?,?,?)")) {
            for (Encounter encounter : (Iterable<Encounter>) encounters::iterator) {
                preparedStatement.setLong(1, encounter.getId().longValue());
                preparedStatement.setLong(2, encounter.getPatientId().longValue());
                preparedStatement.setLong(3, encounter.getProviderId().longValue());
                preparedStatement.setTimestamp(4, new Timestamp(encounter
                        .getStart().getTime()));
                preparedStatement.setTimestamp(5, new Timestamp(encounter.getEnd()
                        .getTime()));
                preparedStatement.setString(6, encounter.getType());
                preparedStatement.setString(7, encounter.getDischargeDisposition());
                preparedStatement.setTimestamp(8, toTimestamp(encounter.getCreateDate()));
                preparedStatement.setTimestamp(9, toTimestamp(encounter.getUpdateDate()));
                preparedStatement.setTimestamp(10, toTimestamp(encounter.getDeleteDate()));
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
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw ex;
        }
    }

    /**
     * Insert the given stream of providers to a target database using the given
     * connection.
     *
     * @param providers The stream of providers to insert.
     * @throws SQLException Thrown if there are any JDBC errors.
     */
    void insertProviders(Stream<Provider> providers) throws SQLException {
        int counter = 0;
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("insert into provider values (?,?,?,?,?,?)")) {
            for (Provider provider : (Iterable<Provider>) providers::iterator) {
                preparedStatement.setLong(1, provider.getId().longValue());
                preparedStatement.setString(2, provider.getFirstName());
                preparedStatement.setString(3, provider.getLastName());
                preparedStatement.setTimestamp(4, toTimestamp(provider.getCreateDate()));
                preparedStatement.setTimestamp(5, toTimestamp(provider.getUpdateDate()));
                preparedStatement.setTimestamp(6, toTimestamp(provider.getDeleteDate()));
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
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw ex;
        }
    }

    private static Timestamp toTimestamp(java.util.Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }

}
