package org.arp.javautil.fileutils.meb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;

public class SimpleDataUtils {

	private String comment = "";
	private boolean ignoreBlankLines = false;

	private ArrayList<String> header = null;

	public SimpleDataUtils(String[] args) throws IOException,
			InvalidFormatException {
		ArrayList<ArrayList> data = new ArrayList<ArrayList>();
		for (int rownbr = 0; rownbr < 14; rownbr++) {
			ArrayList<String> rowlist = new ArrayList();
			for (int colnbr = 0; colnbr < 10; colnbr++) {
				String value = String.format("Row %2d Col %2d", rownbr + 1,
						colnbr + 1);
				rowlist.add(value);
			}
			data.add(rowlist);
		}
		ArrayList<String> header = new ArrayList<String>();
		header.add("Col-A");
		header.add("Col-B");
		header.add("Col-C");
		header.add("Col-D");
		header.add("Col-E");
		header.add("Col-F");
		header.add("Col-G");
		header.add("Col-H");
		header.add("Col-I");
		header.add("Col-J");

		String filename = "testme";
		String sheetname = "sheet1";
		TableDataExcel tde1 = new TableDataExcel(filename, sheetname);
		tde1.write(data, header);

		TableDataExcel tde2 = new TableDataExcel(filename, sheetname);

		// ArrayList<ArrayList> data2 = readExcelFile(filename, sheetname,
		// false);

		TableDataDelimited tdd1 = new TableDataDelimited(filename, sheetname,
				"txt");
		tdd1.setDelimiter(':');
		tdd1.skipHeaderInData = false;
		// tdd1.write(data, header);
		tdd1.write(data, null);

		TableDataDelimited tdd2 = new TableDataDelimited(filename, sheetname,
				"txt");
		tdd2.setDelimiter(':');
		tdd1.skipHeaderInData = false;
		ArrayList<ArrayList> data2 = tdd2.read();

		TableData.dumpData(data2);

		ExtractDatabaseTable et = new ExtractDatabaseTable();

		String serverName = "aiwdev01.eushc.org";
		String sid = "AIWD";
		String username = "I2B2_LP01_2";
		String password = "aBaSefun83";
		String tablename = "CONCEPT_DIMENSION";
		et.setDatabase(serverName, sid);
		et.setUser(username, password);

		String query = "select concept_cd, concept_path from I2B2_LP01_2." + tablename + " where concept_path like 'LungP01 '";
		ArrayList<ArrayList> datadb = et.read(query);
		TableData.dumpData(datadb);
		
		filename = "i2b2";
		sheetname = tablename;
		TableDataExcel tde3 = new TableDataExcel(filename, sheetname);
		tde3.write(data, header);
		
	}

	public SimpleDataUtils() {
	}

	public String replaceExtension(String filename, String extension) {
		int n = filename.lastIndexOf('.');
		if (n > 0)
			return filename.substring(0, n) + "." + extension;
		else
			return filename + "." + extension;
	}

	public String insertTabname(String filename, String tabname) {
		int n = filename.lastIndexOf('.');
		if (n > 0)
			return filename.substring(0, n) + "-" + tabname
					+ filename.substring(n + 1);
		else
			return filename + "-" + tabname;
	}

	public ArrayList<ArrayList> readExcelFile(String filename,
			String worksheetName, boolean skipheader)
			throws InvalidFormatException, IOException {

		ArrayList<ArrayList> data = new ArrayList<ArrayList>();
		File workbookFile = new File(filename);

		FileInputStream fis = new FileInputStream(workbookFile);
		Workbook workbook = WorkbookFactory.create(fis);
		Sheet sheet = workbook.getSheet(worksheetName);
		if (sheet != null) {

			for (Row row : sheet) {
				ArrayList<Object> rowlist = new ArrayList<Object>();
				for (Cell cell : row) {
					rowlist.add(cell.getStringCellValue());
				}
				data.add(rowlist);
			}

		}

		fis.close();
		return data;

	}

	public void writeExcelFile(String filename, String sheetname,
			ArrayList<ArrayList> data) throws IOException {
		writeExcelFile(filename, sheetname, data, null);
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public ArrayList<ArrayList> readDelimitedFile(String filename,
			String worksheetName, char delimiter, boolean skipheader) {

		ArrayList<ArrayList> data = new ArrayList<ArrayList>();
		String pathname = filename;
		if ((worksheetName != null) || (!worksheetName.equals("")))
			filename = insertTabname(filename, worksheetName);

		try {
			BufferedReader in = new BufferedReader(new FileReader(pathname));
			String line;
			int lineNbr = 0;
			int nbrFields = 0;

			while ((line = in.readLine()) != null) {

				line = line.trim();
				lineNbr++;

				if (ignoreBlankLines && line.equals(""))
					continue;

				if ((!comment.equals("")) && line.startsWith(comment))
					continue;

				ArrayList<String> row = new ArrayList<String>();
				String[] fields = line.split("\b" + delimiter + "\b");

				if (lineNbr == 1)
					nbrFields = fields.length;
				else if (fields.length != nbrFields) {
					String msg = String
							.format(
									"File: %s, Line: %s, Number of fields (%s) does not match those in first line (%s)",
									pathname, lineNbr, fields.length, nbrFields);
					System.out.println(msg);
					continue;
				}

				if (lineNbr == 1)
					header = row;

				if ((lineNbr == 1) && (skipheader))
					continue;

				Collections.addAll(row, fields);
				data.add(row);

			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void writeDelimitedFile(String filename, ArrayList<ArrayList> data,
			char delimiter) throws IOException {

		writeDelimitedFile(filename, data, null, delimiter);
	}

	public void writeDelimitedFile(String filename, ArrayList<ArrayList> data,
			ArrayList<String> header, char delimiter) throws IOException {

		String delim = String.valueOf(delimiter);
		int col = 0;

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(filename), false));
		col = 0;
		if ((header != null) && (header.size() > 0)) {
			for (String value : header) {
				if (col == 0)
					bw.write(value.replaceAll(delim, " "));
				else {
					bw.write(delimiter);
					bw.write(value.replaceAll(delim, " "));
				}
				col++;
			}
			bw.newLine();
		}

		for (ArrayList<String> row : data) {
			col = 0;
			for (String value : row) {
				if (col == 0)
					bw.write(value.replaceAll(delim, " "));
				else {
					bw.write(delimiter);
					bw.write(value.replaceAll(delim, " "));
				}
				col++;
			}
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}

	public void writeExcelFile(String filename, String worksheetName,
			ArrayList<ArrayList> data, ArrayList<String> header)
			throws IOException {

		if (worksheetName == null)
			worksheetName = "worksheet1";

		Workbook workbook = new HSSFWorkbook();
		Sheet worksheet = workbook.createSheet(worksheetName);

		int rownbr = 0;
		// Insert header if provided
		if ((header != null) && (header.size() > 0)) {
			Row row = worksheet.createRow(rownbr);
			int cellnbr = 0;
			for (String value : header) {
				Cell cell = row.createCell(cellnbr);
				cell.setCellValue(value);
				worksheet.autoSizeColumn(cellnbr);
				cellnbr++;
			}
			rownbr++;
		}
		// Insert all rows
		for (ArrayList<String> rowlist : data) {
			Row row = worksheet.createRow(rownbr);
			int cellnbr = 0;
			for (String value : rowlist) {
				Cell cell = row.createCell(cellnbr);
				cell.setCellValue(value);
				worksheet.autoSizeColumn(cellnbr);
				cellnbr++;

			}
			rownbr++;
		}

		FileOutputStream outputStream = new FileOutputStream(filename);

		workbook.write(outputStream);
		outputStream.close();
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static void main(String[] args) throws IOException,
			InvalidFormatException {
		new SimpleDataUtils(args);

	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isIgnoreBlankLines() {
		return ignoreBlankLines;
	}

	public void setIgnoreBlankLines(boolean ignoreBlankLines) {
		this.ignoreBlankLines = ignoreBlankLines;
	}
}
