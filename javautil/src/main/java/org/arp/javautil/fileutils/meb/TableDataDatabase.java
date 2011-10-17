package org.arp.javautil.fileutils.meb;

import java.util.ArrayList;

public class TableDataDatabase extends TableData {

	private ExtractDatabaseTable et = null;
	private String query = "";

	TableDataDatabase(String serverName, String portNumber, String sid,
			String schema) {
		super(schema, "");
		et = new ExtractDatabaseTable();
		et.setDatabase(serverName, portNumber, sid, schema);
	}

	public void setUser(String username, String password) {
		et.setUser(username, password);
	}

	public void setQuery(String query) {
		this.query = query;
		String[] fields = query.split(" ");
		String tablename = "";
		for (int n = 0; n < fields.length; n++) {
			String token = fields[n];
			if (token.equalsIgnoreCase("from"))
				tablename = fields[++n];
		}
		tablename = tablename.replace("\"", "");
		int n = tablename.lastIndexOf('.');
		if (n > 0)
			this.setTablename(tablename.substring(n + 1));
		else
			this.setTablename(tablename);
	}

	@Override
	public ArrayList<ArrayList> read() {
		return et.read(query);
	}

}
