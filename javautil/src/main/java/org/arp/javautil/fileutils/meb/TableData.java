package org.arp.javautil.fileutils.meb;

import java.util.ArrayList;

public class TableData {

	private String rootname = "";
	private String tablename = "";

	protected ArrayList<ArrayList> data = new ArrayList<ArrayList>();
	protected ArrayList<String> header = new ArrayList<String>();

	boolean skipHeaderInData = false;

	TableData(){
	}
	
	TableData(String rootname, String tablename) {
		this.rootname = rootname;
		this.tablename = tablename;
	}

	static public void dumpData(ArrayList<ArrayList> data) {
		for (ArrayList<String> row : data) {
			for (String cellvalue : row) {
				System.out.format("%-14s  ", cellvalue);
			}
			System.out.println("");
		}
	}
	
	public void write(ArrayList<ArrayList> data, ArrayList<String> header) {
	}

	public void write(ArrayList<ArrayList> data) {
		write(data, null);
	}

	public ArrayList<ArrayList> read() {
		return data;
	}

	public String getRootname() {
		return rootname;
	}

	public void setRootname(String rootname) {
		this.rootname = rootname;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public void setHeader(ArrayList<String> header) {
		this.header = header;
	}

}
