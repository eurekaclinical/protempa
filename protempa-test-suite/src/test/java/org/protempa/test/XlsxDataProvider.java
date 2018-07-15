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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public Stream<Patient> getPatients() {
        return readPatients();
    }

    @Override
    public Stream<Provider> getProviders() {
        return readProviders();
    }

    @Override
    public Stream<Encounter> getEncounters() {
        return readEncounters();
    }

    @Override
    public Stream<Icd9Diagnosis> getIcd9Diagnoses() {
        return readIcd9Diagnoses();
    }

    @Override
    public Stream<Icd9Procedure> getIcd9Procedures() {
        return readIcd9Procedures();
    }

    @Override
    public Stream<Medication> getMedications() {
        return readMedications();
    }

    @Override
    public Stream<Lab> getLabs() {
        return readLabs();
    }

    @Override
    public Stream<Vital> getVitals() {
        return this.readVitals();
    }

    /**
     * Parse the stream of patients from the workbook.
     *
     * @return A stream of {@link Patient} objects.
     */
    private Stream<Patient> readPatients() {
        XSSFSheet sheet = this.workbook.getSheet("patient");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return patient;
        });
    }

    /**
     * Parse the stream of providers in the workbook.
     *
     * @return A stream of {@link Provider} objects.
     */
    private Stream<Provider> readProviders() {
        XSSFSheet sheet = this.workbook.getSheet("provider");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
            Provider provider = new Provider();
            provider.setId(XlsxDataProvider.readLongValue(row.getCell(0)));
            provider.setFirstName(XlsxDataProvider.readStringValue(row
                    .getCell(1)));
            provider.setLastName(XlsxDataProvider.readStringValue(row
                    .getCell(2)));
            provider.setCreateDate(XlsxDataProvider.readDateValue(row.getCell(3)));
            provider.setUpdateDate(XlsxDataProvider.readDateValue(row.getCell(4)));
            provider.setDeleteDate(XlsxDataProvider.readDateValue(row.getCell(5)));
            return provider;
        });
    }

    /**
     * Parse the stream of encounters in the workbook.
     *
     * @return A stream of {@link Encounter} objects.
     */
    private Stream<Encounter> readEncounters() {
        XSSFSheet sheet = this.workbook.getSheet("encounter");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return encounter;
        });
    }

    /**
     * Parse the stream of ICD9 Diagnostic codes present in the workbook.
     *
     * @return A stream of {@link Icd9Diagnosis} objects.
     */
    private Stream<Icd9Diagnosis> readIcd9Diagnoses() {
        XSSFSheet sheet = this.workbook.getSheet("eICD9D");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return diagnosis;
        });
    }

    /**
     * Parse the stream of ICD9 Procedure codes present in the workbook.
     *
     * @return A stream of {@link Icd9Procedure} objects.
     */
    private Stream<Icd9Procedure> readIcd9Procedures() {
        XSSFSheet sheet = this.workbook.getSheet("eICD9P");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return procedure;
        });
    }

    /**
     * Parse the stream of medications present in the workbook.
     *
     * @return A stream of {@link Medication} objects.
     */
    private Stream<Medication> readMedications() {
        XSSFSheet sheet = this.workbook.getSheet("eMEDS");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return medication;
        });
    }

    /**
     * Parse the stream of labs present in the workbook's "eLABS" worksheet.
     *
     * @return A stream of {@link Lab} objects.
     */
    private Stream<Lab> readLabs() {
        XSSFSheet sheet = this.workbook.getSheet("eLABS");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return lab;
        });
    }

    /**
     * Parse the stream of vitals present in the workbook's "eVITALS" worksheet.
     *
     * @return A stream of {@link Vital} objects.
     */
    private Stream<Vital> readVitals() {
        XSSFSheet sheet = this.workbook.getSheet("eVITALS");
        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row
        Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false);
        return stream.map(row -> {
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
            return vital;
        });
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
            Double value = Double.valueOf(cell.getNumericCellValue());
            result = Long.valueOf(value.longValue());
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
            result = Double.valueOf(value);
        } else {
            result = null;
        }
        return result;
    }
}
