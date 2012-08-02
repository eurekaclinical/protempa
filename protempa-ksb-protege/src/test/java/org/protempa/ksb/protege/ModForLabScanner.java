/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.ksb.protege;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
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
                        out.println(StringUtils.join(new String[]{"Case " +
                                cols[0], cols[0], cols[1], cols[2],
                                    dateFormat.format(cal.getTime()),
                                    timeFormat.format(cal.getTime())},
                                "\t"));
                    }
                }
            }.execute();
        } finally {
            out.close();
        }
    }
}
