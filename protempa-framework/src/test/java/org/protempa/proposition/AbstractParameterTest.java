package org.protempa.proposition;

import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.value.RelativeHourGranularity;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 * 
 */
public class AbstractParameterTest extends TestCase {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private AbstractParameter p;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        p = new AbstractParameter("TEST");
        p.setDataSourceType(new DerivedDataSourceType());
        p.setInterval(intervalFactory.getInstance(0L,
                RelativeHourGranularity.HOUR, 12L,
                RelativeHourGranularity.HOUR));

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        p = null;
    }

    /**
     * Having the method here stops JUnit from complaining that there are no
     * tests...
     */
    public void testEmptyTest() {
    }
}
