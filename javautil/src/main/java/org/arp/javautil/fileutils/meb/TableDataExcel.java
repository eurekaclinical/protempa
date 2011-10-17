package org.arp.javautil.fileutils.meb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TableDataExcel extends TableData {

	private String extension = "xls";
	private String[] columnNamesHighlighted = { "" };
	private String[] columnNamesHidden = { "" };
	private boolean freezeTopRow = false;

	public TableDataExcel(String fileName, String worksheetName) {
		super(fileName, worksheetName);
	}

	public TableDataExcel() {
	}

	public String getFilename() {
		return this.getRootname() + "." + this.getExtension();
	}

	public void setSheetname(String sheetname) {
		setTablename(sheetname);
	}

	public void setFilename(String filename) {
		String[] fileparts = filename.split("[-\\.]");
		// In an excel filename, only the rootname is used
		// the tablename is used for the sheet name
		if (fileparts.length == 2) {
			this.setRootname(fileparts[0]);
			this.setExtension(fileparts[1]);
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
		File workbookFile = new File(filename);

		FileInputStream fis;
		try {
			fis = new FileInputStream(workbookFile);
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheet(this.getTablename());
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
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}

	private void newWorkbook(String filename) {
		Workbook wb = new HSSFWorkbook();
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filename);
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private void setCustomColor(Workbook wb) {
	// // creating a custom palette for the workbook
	// HSSFPalette palette = wb.getCustomPalette();
	//
	// // replacing the standard red with freebsd.org red
	// palette.setColorAtIndex(HSSFColor.RED.index, (byte) 153, // RGB red
	// // (0-255)
	// (byte) 0, // RGB green
	// (byte) 0 // RGB blue
	// );
	// // replacing lime with freebsd.org gold
	// palette.setColorAtIndex(HSSFColor.LIME.index, (byte) 255, (byte) 204,
	// (byte) 102);
	// }

	private CellStyle createStyleHeader(Workbook workbook) {

		CellStyle style = workbook.createCellStyle();

		style.setFillPattern(CellStyle.LEAST_DOTS);
		// short color = IndexedColors.LIGHT_BLUE.getIndex();
		short color = HSSFColor.PALE_BLUE.index;
		style.setFillForegroundColor(color);
		style.setFillBackgroundColor(color);

		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(font);

		return style;
	}

	private CellStyle createStyleHighlight(Workbook workbook) {

		CellStyle style = workbook.createCellStyle();

		style.setFillPattern(CellStyle.LEAST_DOTS);
		// short color = IndexedColors.PALE_BLUE.getIndex();
		short color = HSSFColor.LIGHT_YELLOW.index;
		style.setFillForegroundColor(color);
		style.setFillBackgroundColor(color);

		return style;
	}

	private boolean inArray(String[] list, String key) {
		for (String string : list) {
			if (string.equals(key))
				return true;
		}
		return false;
	}

	@Override
	public void write(ArrayList<ArrayList> data, ArrayList<String> header) {

		String filename = this.safeExcelFilename(this.getFilename());
		File file = new File(filename);
		if (!file.exists()) {
			newWorkbook(filename);
		}
		// Workbook workbook = new HSSFWorkbook();
		Workbook workbook = new HSSFWorkbook();

		InputStream inp;
		try {
			inp = new FileInputStream(filename);
			workbook = WorkbookFactory.create(inp);
			inp.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sheetname = this.getTablename();

		Sheet worksheet = workbook.getSheet(sheetname);
		CellStyle hdrstyle = createStyleHeader(workbook);
		CellStyle bgstyle = createStyleHighlight(workbook);

		if (worksheet != null) {

			// Remove previous rows from the existing worksheet
			int lastrow = worksheet.getLastRowNum();
			while (lastrow >= 0) {
				Row row = worksheet.getRow(lastrow);
				if (row != null)
					worksheet.removeRow(row);
				lastrow--;
			}

		} else
			worksheet = workbook.createSheet(this.getTablename());

		int rownbr = 0;
		int maxcell = 0;
		// Insert header if provided
		if ((header != null) && (header.size() > 0)) {
			Row row = worksheet.createRow(rownbr);
			int cellnbr = 0;
			for (String value : header) {
				Cell cell = row.createCell(cellnbr);
				cell.setCellValue(value);
				worksheet.autoSizeColumn(cellnbr);
				if (rownbr == 0)
					cell.setCellStyle(hdrstyle);
				maxcell = Math.max(maxcell, cellnbr);
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
				if (rownbr == 0)
					cell.setCellStyle(hdrstyle);
				maxcell = Math.max(maxcell, cellnbr);
				cellnbr++;
			}
			rownbr++;
		}

		// Freeze top row if requested
		if (freezeTopRow)
			worksheet.createFreezePane(0, 1, 0, 1);
		
		//Highlight and hide requested columns
		int lastrow = worksheet.getLastRowNum();
		Row hdr = worksheet.getRow(worksheet.getFirstRowNum());
		String hdrtext;
		Cell hdrcell;
		Cell cell;

		while (lastrow > 0) {

			Row row = worksheet.getRow(lastrow);
			int firstcol = row.getFirstCellNum();
			int lastcol = row.getLastCellNum();

			for (int c = firstcol; c <= lastcol; c++) {
				hdrcell = hdr.getCell(c);

				if (hdrcell != null) {
					hdrtext = hdrcell.getStringCellValue();

					if (inArray(this.columnNamesHighlighted, hdrtext)) {
						cell = row.getCell(c);
						cell.setCellStyle(bgstyle);
					}

					if (inArray(this.columnNamesHidden, hdrtext)) {
						worksheet.setColumnHidden(c, true);
					}

				}
			}
			lastrow--;
		}

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(filename);
			workbook.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String safeExcelFilename(String filename) {
		String suffix = ".xls";
		int n = filename.indexOf('.');
		if (n > 0)
			return filename.substring(0, n) + suffix;
		else
			return filename + suffix;
	}

	/**
	 * @param columnNamesHighlighted
	 *            the columnNamesHighlighted to set
	 */
	public void setColumnNamesHighlighted(String[] columnNamesHighlighted) {
		this.columnNamesHighlighted = columnNamesHighlighted;
	}

	/**
	 * @param columnNamesHidden
	 *            the columnNamesHidden to set
	 */
	public void setColumnNamesHidden(String[] columnNamesHidden) {
		this.columnNamesHidden = columnNamesHidden;
	}

	/**
	 * @return the freezeTopRow
	 */
	public boolean isFreezeTopRow() {
		return freezeTopRow;
	}

	/**
	 * @param freezeTopRow
	 *            the freezeTopRow to set
	 */
	public void setFreezeTopRow(boolean freezeTopRow) {
		this.freezeTopRow = freezeTopRow;
	}
}
