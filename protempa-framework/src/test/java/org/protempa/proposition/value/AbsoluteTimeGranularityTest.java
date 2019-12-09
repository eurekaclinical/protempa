/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.proposition.value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import static org.protempa.proposition.value.AbsoluteTimeGranularityUtil.asPosition;

import junit.framework.TestCase;

public class AbsoluteTimeGranularityTest extends TestCase {

//    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(
//            DateFormat.SHORT, Locale.US);
	private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("dd-MMM-yy");

    public void testLatestMonthApril() throws ParseException {
        long april1 = asPosition(DATE_FORMAT.parse("01-Apr-2007"));
        assertEquals(april1 + 30 * 24 * 60 * 60 * 1000L - 1,
                AbsoluteTimeGranularity.MONTH.latest(april1));
    }

    public void testLatestMonthFebruary2007() throws ParseException {
        long feb1 = asPosition(DATE_FORMAT.parse("01-Feb-2007"));
        assertEquals(feb1 + 28 * 24 * 60 * 60 * 1000L - 1,
                AbsoluteTimeGranularity.MONTH.latest(feb1));
    }

    public void testEarliestMonth() throws ParseException {
        long april1 = asPosition(DATE_FORMAT.parse("01-Apr-2007"));
        assertEquals(april1, AbsoluteTimeGranularity.MONTH.earliest(april1));
    }
}
