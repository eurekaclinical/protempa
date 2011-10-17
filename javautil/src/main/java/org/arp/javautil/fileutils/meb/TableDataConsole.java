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
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TableDataConsole extends TableData {

	public TableDataConsole(String rootname, String tablename) {
		super(rootname, tablename);
	}

	@Override
	public ArrayList<ArrayList> read() {
		System.out.println("read() is not implemented for TableDataConsole");
		return null;
	}

	public void write(ArrayList<ArrayList> data, ArrayList<String> header) {

		HashMap<Integer, Integer> widths = new HashMap<Integer, Integer>();
		ArrayList<String> row0 = data.get(0);

		int col = 0;

		Integer prevWidth = 0;
		Integer newWidth = 0;
		for (ArrayList<String> row : data) {
			col = 0;
			for (String value : row) {
				if (value != null) {
					prevWidth = widths.get(col);
					if (prevWidth == null)
						newWidth = value.length();
					else
						newWidth = Math.max(value.length(), prevWidth);
					widths.put(col, newWidth);
					// System.out.format("col %2d, width %2d  |  ", col,
					// newWidth);
				}
				col++;
			}
			// System.out.println("");
		}

		HashMap<Integer, String> formats = new HashMap<Integer, String>();
		Iterator<Integer> it = widths.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			Integer width = widths.get(key);
			formats.put(key, "%-" + width + "s");
		}

		for (ArrayList<String> row : data) {
			col = 0;
			for (String value : row) {
				System.out.format(formats.get(col) + "  ", value);
				col++;
			}
			System.out.println("");
		}

	}

}
