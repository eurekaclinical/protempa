package org.protempa.proposition.value;

import java.math.BigDecimal;



import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class ValueFactoryTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPointZero() {
        assertEquals(new NumberValue(6.0), ValueFactory.VALUE.parse("6.0"));
        assertEquals("6.0", ValueFactory.VALUE.parse("6.0").getFormatted());
        assertEquals(new NumberValue(6.0), ValueType.VALUE.getValueFactory().parse("6.0"));
        assertEquals("6.0", ValueFactory.VALUE.parse("6.0").getFormatted());
    }

    public void testValueParseInequalityValue() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.1")),
                ValueFactory.VALUE.parse("<0.1"));
    }

    public void testInequalityDoubleParseValue() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.1")),
                ValueFactory.INEQUALITY.parse("<0.1"));
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueFactory.INEQUALITY.parse("<0.4"));
    }

    public void testNumberValueParserLeadingWhitespace() {
        assertEquals(new NumberValue(new BigDecimal(3)),
                ValueFactory.NUMBER.parse("    3"));
    }

    public void testNumberFormatParserTrailingWhitespace() {
        assertEquals(new NumberValue(new BigDecimal(3)),
                ValueFactory.NUMBER.parse("3    "));
    }

    public void testNumberFormatParserWhitespace() {
        assertEquals(new NumberValue(new BigDecimal(3)),
                ValueFactory.NUMBER.parse("  3    "));
    }

    public void testPrecedingWhitespace() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueFactory.INEQUALITY.parse("       <0.4"));
    }

    public void testInterveningWhitespace() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueFactory.INEQUALITY.parse("<    0.4"));
    }

    public void testWhitespaceAfter() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueFactory.INEQUALITY.parse("<0.4     "));
    }

    public void testWhitespaceAllOver() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueFactory.INEQUALITY.parse("      <   0.4     "));
    }
}
