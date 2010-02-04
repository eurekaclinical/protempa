package org.protempa.ksb.protege;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.arp.javautil.io.IOUtil;
import org.arp.javautil.io.WithLineNumberReader;

/**
 * Imports HELLP data file into database.
 * 
 * @author Andrew Post
 * 
 */
public final class HELLPDataImporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: HELLPDataImporter dxfile labsfile");
			System.exit(1);
		}

		Properties config = IOUtil
				.createPropertiesFromResource(Class.class,
						"/edu/virginia/pbhs/protempa/protege/hellpDataSource.properties");

		Class.forName(config.getProperty("DATABASE_DRIVER")).newInstance();

		String db = config.getProperty("DATABASE_URL");
		String username = config.getProperty("USERNAME");
		String password = config.getProperty("PASSWORD");

		Connection con = null;
		try {
			con = DriverManager.getConnection(db, username, password);
			System.out.println("Deleting old data.");
			PreparedStatement stmt = con.prepareStatement("DELETE FROM data");
			try {
				stmt = con.prepareStatement("DELETE FROM data");
				stmt.execute();
			} finally {
				stmt.close();
			}

			final PreparedStatement stmt2 = con
					.prepareStatement("INSERT INTO data (keyId,paramId,value,valueType,hrsOffset) VALUES (?,?,?,?,?)");
			try {
				// Process dxfile.
				System.out.println("Inserting diagnosis codes.");
				new WithLineNumberReader(args[0]) {

					@Override
					public void readLine(int lineNumber, String line)
							throws SQLException {
						if (lineNumber > 1) {
							String[] cols = line.split("\t");
							if (cols.length == 4) {
								stmt2.setString(1, cols[0]);
								stmt2.setString(2, "ICD9");
								stmt2.setString(3, cols[1]);
								stmt2.setString(4, "text");
								stmt2.setInt(5, Integer.parseInt(cols[3]));

								stmt2.execute();
							}
						}
					}

				}.execute();

				// Process labsfile.
				System.out.println("Inserting labs.");
				new WithLineNumberReader(args[1]) {

					@Override
					public void readLine(int lineNumber, String line)
							throws SQLException {
						if (lineNumber > 1) {
							String[] cols = line.split("\t");
							if (cols.length == 4) {
								stmt2.setString(1, cols[0]);
								stmt2.setString(2, cols[1]);
								stmt2.setString(3, cols[2]);
								stmt2.setString(4, "numeric");
								stmt2.setInt(5, Integer.parseInt(cols[3]));

								stmt2.execute();
							}
						}
					}

				}.execute();

				// Admit and discharge data.
				System.out.println("Inserting admit and discharge data.");
				PreparedStatement stmt3 = null;
				try {
					stmt3 = con
							.prepareStatement("SELECT keyId,max(hrsOffset) FROM data GROUP BY keyId");
					ResultSet resultSet = null;
					try {
						resultSet = stmt3.executeQuery();
						while (resultSet.next()) {
							stmt2.setString(1, resultSet.getString(1));
							stmt2.setString(2, "ADMIT");
							stmt2.setString(3, null);
							stmt2.setString(4, null);
							stmt2.setInt(5, 0);
							stmt2.execute();

							stmt2.setString(1, resultSet.getString(1));
							stmt2.setString(2, "DISCHA");
							stmt2.setString(3, null);
							stmt2.setString(4, null);
							stmt2.setInt(5, resultSet.getInt(2));
							stmt2.execute();

							PreparedStatement stmt4 = null;
							try {
								stmt4 = con
										.prepareStatement("UPDATE data SET paramId='ICD9DC' WHERE keyId=? AND paramId='ICD9' AND hrsOffset=?");
								stmt4.setString(1, resultSet.getString(1));
								stmt4.setInt(2, resultSet.getInt(2));
								stmt4.execute();
							} finally {
								if (stmt4 != null) {
									stmt4.close();
								}
							}
						}
					} finally {
						if (resultSet != null) {
							resultSet.close();
						}
					}
				} finally {
					if (stmt3 != null) {
						stmt3.close();
					}
				}
			} finally {
				if (stmt2 != null) {
					stmt2.close();
				}
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

}
