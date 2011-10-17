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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TableDataDelimited extends TableData {

	private String extension = "txt";
	private boolean ignoreBlankLines = true;
	private String commentString = "";
	private char delimiter = ',';

	public TableDataDelimited(String rootname, String tablename, String extension) {
		super(rootname, tablename);
		this.extension = extension;
	}

	public String getFilename() {
		return this.getRootname() + "-" + this.getTablename() + "."
				+ this.getExtension();
	}

	public void setFilename(String filename) {
		String[] fileparts = filename.split("[-\\.]");
		if (fileparts.length == 3) {
			this.setRootname(fileparts[0]);
			this.setTablename(fileparts[1]);
			this.setExtension(fileparts[2]);
		} else {
			System.err.println("Invalid argument for setFilename: " + filename);
		}
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public ArrayList<ArrayList> read() {

		String filename = this.getFilename();

		String worksheetName = this.getTablename();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			int lineNbr = 0;
			int nbrFields = 0;

			while ((line = in.readLine()) != null) {

				line = line.trim();
				lineNbr++;

				if ((this.skipHeaderInData) && (lineNbr == 1))
					continue;

				if (ignoreBlankLines && line.equals(""))
					continue;

				if ((!commentString.equals(""))
						&& line.startsWith(commentString))
					continue;

				ArrayList<String> row = new ArrayList<String>();
				String[] fields = line.split(String.valueOf(delimiter));

				if (lineNbr == 1)
					nbrFields = fields.length;
				else if ((lineNbr == 2) && (nbrFields == 0))
					nbrFields = fields.length;
				else if (fields.length != nbrFields) {
					String msg = String
							.format(
									"File: %s, Line: %s, Number of fields (%s) does not match those in first line (%s)",
									filename, lineNbr, fields.length, nbrFields);
					System.out.println(msg);
					continue;
				}
				if (lineNbr == 1)
					header = row;

				Collections.addAll(row, fields);
				data.add(row);

			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;

	}

	@Override
	public void write(ArrayList<ArrayList> data, ArrayList<String> header) {

		String filename = this.getFilename();

		String delim = String.valueOf(delimiter);
		int col = 0;

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename), false));
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

			int line = 0;
			for (ArrayList<String> row : data) {
				line++;
				if ((line == 1 ) && (this.skipHeaderInData))
					continue;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isIgnoreBlankLines() {
		return ignoreBlankLines;
	}

	public void setIgnoreBlankLines(boolean ignoreBlankLines) {
		this.ignoreBlankLines = ignoreBlankLines;
	}

	public String getCommentString() {
		return commentString;
	}

	public void setCommentString(String commentString) {
		this.commentString = commentString;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}
}
