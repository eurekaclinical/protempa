package org.protempa.proposition.value;

import java.math.BigDecimal;
import java.text.NumberFormat;



import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class NumberValueTest extends TestCase {

    private NumberValue val;

    /**
     * Constructor for GreaterThanDoubleValueTest.
     *
     * @param arg0
     */
    public NumberValueTest(String arg0) {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        val = NumberValue.getInstance(20);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        val = null;
    }

    public void testGreaterThanTrueCompareTo() {
        assertTrue(val.compareTo(NumberValue.getInstance(10)) > 0);
    }

    public void testGreaterThanTrueCompare() {
        assertTrue(val.compareNumberValue(NumberValue.getInstance(10)) == ValueComparator.GREATER_THAN);
    }

    public void testGreaterThanFalseCompareTo() {
        assertFalse(val.compareTo(NumberValue.getInstance(30)) > 0);
    }

    public void testGreaterThanFalseCompare() {
        assertFalse(val.compareNumberValue(NumberValue.getInstance(30)) == ValueComparator.GREATER_THAN);
    }

    public void testLessThanTrueCompareTo() {
        assertTrue(val.compareTo(NumberValue.getInstance(30)) < 0);
    }

    public void testLessThanTrueCompare() {
        assertTrue(val.compareNumberValue(NumberValue.getInstance(30)) == ValueComparator.LESS_THAN);
    }

    public void testEqualToTrue() {
        assertTrue(val.equals(NumberValue.getInstance(20)));
    }

    public void testEqualToFalse() {
        assertFalse(val.equals(NumberValue.getInstance(30)));
    }

    public void testGreaterThanInequalityTrue() {
        assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 30)) == ValueComparator.LESS_THAN);
    }

    public void testLessThanInequalityGreaterThan() {
        assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 10)) == ValueComparator.GREATER_THAN);
    }

    public void testLessThanInequalityUnknown() {
        assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 30)) == ValueComparator.UNKNOWN);
    }

    public void testLessThanInequalitySameDoubleGreaterThan() {
        assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 20)) == ValueComparator.GREATER_THAN);
    }

    public void testDecimalPlaces1() {
        assertEquals("30", ValueFactory.NUMBER.parseValue("30").getFormatted());
    }

    public void testDecimalPlaces2() {
        assertEquals("30.0", ValueFactory.NUMBER.parseValue("30.0").getFormatted());
    }

    public void testDecimalPlaces3() {
        assertEquals("30.00", ValueFactory.NUMBER.parseValue("30.00").getFormatted());
    }

    public void testFormattingBigInteger() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        assertEquals("1000", format.format(new BigDecimal("1000")));
    }
}
