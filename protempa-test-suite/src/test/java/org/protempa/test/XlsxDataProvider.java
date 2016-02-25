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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * An implementation of the {@link DataProvider} interface, using an Excel
 * workbook as the data source.
 *
 * @author hrathod
 *
 */
public class XlsxDataProvider implements DataProvider {

    /**
     * The standard date format for data held in the workbook.
     */
    static final SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss");
    /**
     * The data file used as the data store.
     */
    private final File dataFile;
    /**
     * Holds the workbook associated with the data file.
     */
    private final XSSFWorkbook workbook;
    /**
     * The list of patients parsed from the data file.
     */
    private List<Patient> patients;
    /**
     * The list of providers parsed from the data file.
     */
    private List<Provider> providers;
    /**
     * The list of encounters parsed from the data file.
     */
    private List<Encounter> encounters;
    /**
     * The list of CPT codes parsed from the data file.
     */
    private List<CPT> cpts;
    /**
     * The list of ICD9 Diagnostic codes parsed from the data file.
     */
    private List<Icd9Diagnosis> icd9Diagnoses;
    /**
     * The list of ICD9 Procedure codes parsed from the data file.
     */
    private List<Icd9Procedure> icd9Procedures;
    /**
     * The list of medications parsed from the data file.
     */
    private List<Medication> medications;
    /**
     * The list of labs parsed from the data file.
     */
    private List<Lab> labs;
    /**
     * The list of vitals parsed from the data file.
     */
    private List<Vital> vitals;

    /**
     * Create the data provider from the given data file.
     *
     * @param inDataFile The Excel workbook file to use as the data store.
     * @throws DataProviderException Thrown when the workbook can not be
     * accessed, or parsed correctly.
     */
    public XlsxDataProvider(File inDataFile) throws DataProviderException {
        this.dataFile = inDataFile;
        try {
            this.workbook = new XSSFWorkbook(new FileInputStream(this.dataFile));
        } catch (IOException ioe) {
            throw new DataProviderException(ioe);
        }
    }

    @Override
    public List<Patient> getPatients() {
        if (this.patients == null) {
            this.patients = this.readPatients();
        }
        return this.patients;
    }

    @Override
    public List<Provider> getProviders() {
        if (this.providers == null) {
            this.providers = this.readProviders();
        }
        return this.providers;
    }

    @Override
    public List<Encounter> getEncounters() {
        if (this.encounters == null) {
            this.encounters = this.readEncounters();
        }
        return this.encounters;
    }

    @Override
    public List<CPT> getCptCodes() {
        if (this.cpts == null) {
            this.cpts = this.readCpts();
        }
        return this.cpts;
    }

    @Override
    public List<Icd9Diagnosis> getIcd9Diagnoses() {
        if (this.icd9Diagnoses == null) {
            this.icd9Diagnoses = this.readIcd9Diagnoses();
        }
        return this.icd9Diagnoses;
    }

    @Override
    public List<Icd9Procedure> getIcd9Procedures() {
        if (this.icd9Procedures == null) {
            this.icd9Procedures = this.readIcd9Procedures();
        }
        return this.icd9Procedures;
    }

    @Override
    public List<Medication> getMedications() {
        if (this.medications == null) {
            this.medications = this.readMedications();
        }
        return this.medications;
    }

    @Override
    public List<Lab> getLabs() {
        if (this.labs == null) {
            this.labs = this.readLabs();
        }
        return this.labs;
    }

    @Override
    public List<Vital> getVitals() {
        if (this.vitals == null) {
            this.vitals = this.readVitals();
        }
        return this.vitals;
    }

    /**
     * Parse the list of patients from the workbook.
     *
     * @return A list of {@link Patient} objects.
     */
    private List<Patient> readPatients() {
        XSSFSheet sheet = this.workbook.getSheet("patient");
        List<Patient> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Patient patient = new Patient();
            patient.setId(XlsxDataProvider.readLongValue(row.getCell(0)));
            patient.setFirstName(XlsxDataProvider.readStringValue(row
                    .getCell(1)));
            patient.setLastName(XlsxDataProvider.readStringValue(row.getCell(2)));
            patient.setDateOfBirth(XlsxDataProvider.readDateValue(row
                    .getCell(3)));
            patient.setLanguage(XlsxDataProvider.readStringValue(row.getCell(4)));
            patient.setMaritalStatus(XlsxDataProvider.readStringValue(row
                    .getCell(5)));
            patient.setRace(XlsxDataProvider.readStringValue(row.getCell(6)));
            patient.setGender(XlsxDataProvider.readStringValue(row.getCell(7)));
            patient.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(8)));
            patient.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(9)));
            patient.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(10)));
            result.add(patient);
        }
        return result;
    }

    /**
     * Parse the list of providers in the workbook.
     *
     * @return A list of {@link Provider} objects.
     */
    private List<Provider> readProviders() {
        XSSFSheet sheet = this.workbook.getSheet("provider");
        List<Provider> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Provider provider = new Provider();
            provider.setId(XlsxDataProvider.readLongValue(row.getCell(0)));
            provider.setFirstName(XlsxDataProvider.readStringValue(row
                    .getCell(1)));
            provider.setLastName(XlsxDataProvider.readStringValue(row
                    .getCell(2)));
            provider.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(3)));
            provider.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            provider.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            result.add(provider);
        }
        return result;
    }

    /**
     * Parse the list of encounters in the workbook.
     *
     * @return A list of {@link Encounter} objects.
     */
    private List<Encounter> readEncounters() {
        XSSFSheet sheet = this.workbook.getSheet("encounter");
        List<Encounter> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Encounter encounter = new Encounter();
            encounter.setId(XlsxDataProvider.readLongValue(row.getCell(0)));
            encounter.setPatientId(XlsxDataProvider.readLongValue(row
                    .getCell(1)));
            encounter.setProviderId(XlsxDataProvider.readLongValue(row
                    .getCell(2)));
            encounter.setStart(XlsxDataProvider.readDateValue(row.getCell(3)));
            encounter.setEnd(XlsxDataProvider.readDateValue(row.getCell(4)));
            encounter.setType(XlsxDataProvider.readStringValue(row.getCell(5)));
            encounter.setDischargeDisposition(XlsxDataProvider
                    .readStringValue(row.getCell(6)));
            encounter.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(7)));
            encounter.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(8)));
            encounter.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(9)));
            result.add(encounter);
        }
        return result;
    }

    /**
     * Parse the list of CPT codes in the workbook.
     *
     * @return A list of {@link CPT} objects.
     */
    private List<CPT> readCpts() {
        XSSFSheet sheet = this.workbook.getSheet("eCPT");
        List<CPT> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            CPT cpt = new CPT();
            cpt.setId(XlsxDataProvider.readStringValue(row.getCell(0)));
            cpt.setEncounterId(XlsxDataProvider.readLongValue(row.getCell(1)));
            cpt.setTimestamp(XlsxDataProvider.readDateValue(row.getCell(2)));
            cpt.setEntityId(XlsxDataProvider.readStringValue(row.getCell(3)));
            cpt.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            cpt.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            cpt.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(6)));
            result.add(cpt);
        }
        return result;
    }

    /**
     * Parse the list of ICD9 Diagnostic codes present in the workbook.
     *
     * @return A list of {@link Icd9Diagnosis} objects.
     */
    private List<Icd9Diagnosis> readIcd9Diagnoses() {
        XSSFSheet sheet = this.workbook.getSheet("eICD9D");
        List<Icd9Diagnosis> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Icd9Diagnosis diagnosis = new Icd9Diagnosis();
            diagnosis.setId(XlsxDataProvider.readStringValue(row.getCell(0)));
            diagnosis.setEncounterId(XlsxDataProvider.readLongValue(row
                    .getCell(1)));
            diagnosis.setTimestamp(XlsxDataProvider.readDateValue(row
                    .getCell(2)));
            diagnosis.setEntityId(XlsxDataProvider.readStringValue(row
                    .getCell(3)));
            diagnosis.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            diagnosis.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            diagnosis.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(6)));
            result.add(diagnosis);
        }
        return result;
    }

    /**
     * Parse the list of ICD9 Procedure codes present in the workbook.
     *
     * @return A list of {@link Icd9Procedure} objects.
     */
    private List<Icd9Procedure> readIcd9Procedures() {
        XSSFSheet sheet = this.workbook.getSheet("eICD9P");
        List<Icd9Procedure> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Icd9Procedure procedure = new Icd9Procedure();
            procedure.setId(XlsxDataProvider.readStringValue(row.getCell(0)));
            procedure.setEncounterId(XlsxDataProvider.readLongValue(row
                    .getCell(1)));
            procedure.setTimestamp(XlsxDataProvider.readDateValue(row
                    .getCell(2)));
            procedure.setEntityId(XlsxDataProvider.readStringValue(row
                    .getCell(3)));
            procedure.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            procedure.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            procedure.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(6)));
            result.add(procedure);
        }
        return result;
    }

    /**
     * Parse the list of medications present in the workbook.
     *
     * @return A list of {@link Medication} objects.
     */
    private List<Medication> readMedications() {
        XSSFSheet sheet = this.workbook.getSheet("eMEDS");
        List<Medication> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Medication medication = new Medication();
            medication.setId(XlsxDataProvider.readStringValue(row.getCell(0)));
            medication.setEncounterId(XlsxDataProvider.readLongValue(row
                    .getCell(1)));
            medication.setTimestamp(XlsxDataProvider.readDateValue(row
                    .getCell(2)));
            medication.setEntityId(XlsxDataProvider.readStringValue(row
                    .getCell(3)));
            medication.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            medication.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            medication.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(6)));
            result.add(medication);
        }
        return result;
    }

    /**
     * Parse the list of labs present in the workbook's "eLABS" worksheet.
     *
     * @return A list of {@link Lab} objects.
     */
    private List<Lab> readLabs() {
        XSSFSheet sheet = this.workbook.getSheet("eLABS");
        List<Lab> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Lab lab = new Lab();
            lab.setId(XlsxDataProvider.readStringValue(row.getCell(0)));
            lab.setEncounterId(XlsxDataProvider.readLongValue(row.getCell(1)));
            lab.setTimestamp(XlsxDataProvider.readDateValue(row.getCell(2)));
            lab.setEntityId(XlsxDataProvider.readStringValue(row.getCell(3)));
            lab.setResultAsStr(XlsxDataProvider.readStringValue(row.getCell(4)));
            lab.setResultAsNum(XlsxDataProvider.readDoubleValue(row.getCell(5)));
            lab.setUnits(XlsxDataProvider.readStringValue(row.getCell(6)));
            lab.setFlag(XlsxDataProvider.readStringValue(row.getCell(7)));
            lab.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(8)));
            lab.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(9)));
            lab.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(10)));
            result.add(lab);
        }
        return result;
    }

    /**
     * Parse the list of vitals present in the workbook's "eVITALS" worksheet.
     *
     * @return A list of {@link Vital} objects.
     */
    private List<Vital> readVitals() {
        XSSFSheet sheet = this.workbook.getSheet("eVITALS");
        List<Vital> result = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        while (rows.hasNext()) {
            Row row = rows.next();
            Vital vital = new Vital();
            vital.setId(readStringValue(row.getCell(0)));
            vital.setEncounterId(readLongValue(row.getCell(1)));
            vital.setTimestamp(readDateValue(row.getCell(2)));
            vital.setEntityId(readStringValue(row.getCell(3)));
            vital.setResultAsStr(readStringValue(row.getCell(4)));
            vital.setResultAsNum(readDoubleValue(row.getCell(5)));
            vital.setUnits(readStringValue(row.getCell(6)));
            vital.setFlag(readStringValue(row.getCell(7)));
            vital.setCreateDate(readDateValue(row.getCell(8)));
            vital.setUpdateDate(readDateValue(row.getCell(9)));
            vital.setDeleteDate(readDateValue(row.getCell(10)));
            result.add(vital);
        }
        return result;
    }

    /**
     * Read a date value from the given spreadsheet cell.
     *
     * @param cell The cell to read the value from.
     * @return The date in the cell, if valid, null otherwise.
     */
    private static Date readDateValue(Cell cell) {
        if (cell != null) {
            Date result;
            try {
                result = cell.getDateCellValue();
            } catch (IllegalStateException ex) {
                String value = XlsxDataProvider.readStringValue(cell);
                if (value == null) {
                    result = null;
                } else {
                    try {
                        result = SDF.parse(value);
                    } catch (ParseException e) {
                        result = null;
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Read a string value from the given cell.
     *
     * @param cell The cell to read value from.
     * @return A String containing the cell value, if valid, null otherwise.
     */
    private static String readStringValue(Cell cell) {
        String result;
        if (cell == null) {
            result = null;
        } else if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                result = String.valueOf(cell.getNumericCellValue());
            } else {
                result = null;
            }
        } else {
            result = cell.getStringCellValue();
        }
        return result;
    }

    /**
     * Read a numerical value as a Long type from the given cell.
     *
     * @param cell The cell to read the value from.
     * @return A Long containing the cell's value, if valid, null otherwise.
     */
    private static Long readLongValue(Cell cell) {
        Long result;
        if (cell == null) {
            result = null;
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            Double value = new Double(cell.getNumericCellValue());
            result = new Long(value.longValue());
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Read the give cell's value as a Double type.
     *
     * @param cell The cell to read the value from.
     * @return A Double containing the cell value, if valid, null otherwise.
     */
    private static Double readDoubleValue(Cell cell) {
        Double result;
        if (cell == null) {
            result = null;
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            double value = cell.getNumericCellValue();
            result = new Double(value);
        } else {
            result = null;
        }
        return result;
    }
}
