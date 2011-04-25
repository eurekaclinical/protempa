package org.protempa.proposition.value;

import junit.framework.TestCase;

public class ValueFactoryTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValueFactory() {
        ValueFactory.parseRepr("NOMINALVALUE:foo");
        ValueFactory.parseRepr("NUMBERVALUE:2");
        ValueFactory.parseRepr("BOOLEANVALUE:true");
        ValueFactory.parseRepr("INEQUALITYNUMBERVALUE:< 2");
    }
}
