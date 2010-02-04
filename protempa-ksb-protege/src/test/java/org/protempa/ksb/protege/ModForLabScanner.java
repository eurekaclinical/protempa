package org.protempa.ksb.protege;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.arp.javautil.collections.Collections;
import org.arp.javautil.io.WithLineNumberReader;

/**
 * Reformats the dates in the HELLP dataset so that LabScanner can read them.
 * 
 * @author Andrew Post
 */
public class ModForLabScanner {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		final PrintWriter out = new PrintWriter(System.out);

		try {
			new WithLineNumberReader(System.in) {
				Calendar cal = Calendar.getInstance();

				Date date = new Date();

				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

				DateFormat timeFormat = new SimpleDateFormat("HH:mm");

				@Override
				public void readLine(int lineNumber, String line) {
					if (lineNumber > 1) {
						String[] cols = line.split("\t");
						cal.setTime(date);
						cal.add(Calendar.HOUR, Integer.parseInt(cols[3]));
						out.println(Collections.join(Arrays
								.asList(new String[] { "Case " + cols[0],
										cols[0], cols[1], cols[2],
										dateFormat.format(cal.getTime()),
										timeFormat.format(cal.getTime()) }),
								"\t"));
					}
				}

			}.execute();
		} finally {
			out.close();
		}
	}

}
