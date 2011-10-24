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
        assertEquals(1, AbsoluteTimeGranularity.DAY.distance(d1.getTime(), d2.getTime(),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetweenOneAndAHalfMonths() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/08");
        Date d2 = format.parse("2/15/08");
        assertEquals(1, AbsoluteTimeGranularity.DAY.distance(d1.getTime(), d2.getTime(),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetweenOneAndAHalfYears() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/07");
        Date d2 = format.parse("7/1/08");
        assertEquals(18, AbsoluteTimeGranularity.DAY.distance(d1.getTime(), d2.getTime(),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.MONTH));
    }

    public void testDistanceBetween14Days() throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.US);
        Date d1 = format.parse("1/1/07");
        Date d2 = format.parse("1/15/07");
        assertEquals(14, AbsoluteTimeGranularity.DAY.distance(d1.getTime(), d2.getTime(), AbsoluteTimeGranularity.DAY, AbsoluteTimeUnit.DAY));
    }
}
