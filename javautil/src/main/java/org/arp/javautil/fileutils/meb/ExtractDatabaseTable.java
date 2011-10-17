package org.arp.javautil.fileutils.meb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtractDatabaseTable {

	boolean debug = false;
	private final String newline = System.getProperty("line.separator");

	private Logger logger = null;
	private String username = "";
	private String password = "";
	private String sid = "";
	private String schema = "";
	private String serverName = "";
	private String portNumber = "";
	private String delimiter = "\t";

	ExtractDatabaseTable() {
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setDatabase(String serverName, String sid){
		setDatabase(serverName, null, sid, null );
	}
	
	public void setDatabase(String serverName, String portNumber, String sid, String schema) {

		if ((serverName == null) || (serverName == ""))
			this.serverName = "localhost";
		else
			this.serverName = serverName;
	
		if ((portNumber == null) || (portNumber == ""))
			this.portNumber = "1521";
		else
			this.portNumber = portNumber;

		if ((schema != null) && (!schema.equals("")))
			this.schema = schema;

		this.sid = sid;
	}

	public void setUser(String username, String password) {
		this.username = username;
		this.password = password;
		if( schema.equals(""))
			schema = username.toUpperCase();
	}

	protected void extractToFile(String query, String filename) {

		StringBuilder sb = new StringBuilder();

		
		System.out.println(query);
		
		Connection conn;
		try {
			
			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			
			conn = getConnection();

			Statement stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			String value = "";
			
			int numCols = rsmd.getColumnCount();

			int rowcount = 0;
			while (resultSet.next()) {
				rowcount++;
				if ((debug) && (rowcount > 10))
					break;
				if( (rowcount % 5000) == 0  )
					logger.info(rowcount + " rows extracted");
				sb.setLength(0);
				for (int col = 1; col <= numCols; col++) {
					value = resultSet.getString(col);
					if( value != null )
						value = value.replace(delimiter, " ");
					sb.append(value);
					if (col != numCols)
						sb.append(delimiter);
				}
				sb.append(newline);
//				System.out.print(sb.toString());
				pw.write(sb.toString());
			}
			pw.flush();
			pw.close();
		} catch (ClassNotFoundException e) {
			logException(e);
		} catch (SQLException e) {
			logException(e);
		} catch (IOException e) {
			logException(e);
		}
	}
	
	protected ArrayList<ArrayList> read(String query) {

		ArrayList<ArrayList> data = new ArrayList<ArrayList>();
		
		Connection conn;
		try {
			
			String value = "";
			String columnName = "";
			
			conn = getConnection();
			Statement stmt = conn.createStatement();
			
			ResultSet resultSet = stmt.executeQuery(query);
			ResultSetMetaData rsmd = resultSet.getMetaData();

			// Get header info 
			ArrayList<String> header = new ArrayList<String>();
			int numCols = rsmd.getColumnCount();
			for (int col = 1; col <= numCols; col++) {
				columnName = rsmd.getColumnName(col);
				header.add(columnName);
			}
			data.add(header);

			// Now get data rows
			int rowcount = 0;
			while (resultSet.next()) {
				rowcount++;
				if ((debug) && (rowcount > 10))
					break;
				if( (rowcount % 5000) == 0  )
					logger.info(rowcount + " rows extracted");
				ArrayList<String> row = new ArrayList<String>();
				for (int col = 1; col <= numCols; col++) {
					value = resultSet.getString(col);
					row.add(value);
				}
				data.add(row);
			}
		} catch (SQLException e) {
			logException(e);
		} catch (ClassNotFoundException e) {
			logException(e);
		}
		return data;
	}

	public String getURL() {
		return "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
	}

	public Connection getConnection() throws ClassNotFoundException,
			SQLException {

		Connection connection = null;
		try {
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);
			String url = getURL();
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			logException(e);
		} catch (SQLException e) {
			logException(e);
		}
		return connection;

	}

	private void logException(Exception e) {
		String method = e.getStackTrace()[1].getMethodName();
		String classname = e.getStackTrace()[1].getClassName();
		int linenumber = e.getStackTrace()[1].getLineNumber();
		String message = String.format("%s, %s, Line %d", classname, method, linenumber);
		if (logger != null){
			logger.severe(e.getLocalizedMessage());
			logger.severe(method + ", " + this.getURL());
			logger.severe(message);
		}else{
			System.err.println(e.getLocalizedMessage());
			System.err.println(this.getURL());
			System.err.println(message);
		}
		e.printStackTrace();
	}

}
