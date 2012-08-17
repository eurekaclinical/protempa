/*
 * #%L
 * Protempa Framework
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
package org.protempa.proposition.value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import static
        org.protempa.proposition.value.AbsoluteTimeGranularityUtil.asPosition;


import junit.framework.TestCase;

/**
 * Test cases for AbsoluteTimeUnit.
 * 
 * @author Andrew Post.
 */
public class AbsoluteTimeUnitTest extends TestCase {

//    private static final DateFormat DATE_TIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
//    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(
//            DateFormat.SHORT, Locale.US);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSerializable() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(AbsoluteTimeUnit.DAY);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
                bytes.toByteArray()));
        Unit unit = (Unit) in.readObject();
        assertEquals(AbsoluteTimeUnit.DAY, unit);
        in.close();
    }

    public void testDistanceBetweenOneMonth() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/08");
        Date d2 = format.parse("2/1/08");
        assertEquals(1, AbsoluteTimeGranularity.DAY.distance(asPosition(d1), asPosition(d2),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetweenOneAndAHalfMonths() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/08");
        Date d2 = format.parse("2/15/08");
        assertEquals(1, AbsoluteTimeGranularity.DAY.distance(asPosition(d1), asPosition(d2),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetweenOneAndAHalfYears() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/07");
        Date d2 = format.parse("7/1/08");
        assertEquals(18, AbsoluteTimeGranularity.DAY.distance(asPosition(d1), asPosition(d2),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetween14Days() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/07");
        Date d2 = format.parse("1/15/07");
        assertEquals(14, AbsoluteTimeGranularity.DAY.distance(asPosition(d1), asPosition(d2), AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.DAY));
    }
}
