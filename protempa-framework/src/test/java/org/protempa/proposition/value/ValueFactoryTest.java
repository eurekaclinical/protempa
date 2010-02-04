package org.protempa.proposition.value;

import org.protempa.proposition.value.ValueFactory;

import junit.framework.TestCase;

public class ValueFactoryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testValueFactory() {
		ValueFactory.VALUE.parseRepr("NOMINALVALUE:foo");
		ValueFactory.VALUE.parseRepr("NUMBERVALUE:2");
		ValueFactory.VALUE.parseRepr("BOOLEANVALUE:true");
		ValueFactory.VALUE.parseRepr("INEQUALITYNUMBERVALUE:< 2");
	}

}
