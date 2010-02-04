package org.protempa.ksb.protege;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RemoteLowLevelAbstractionsTest
        extends AbstractLowLevelAbstractionsTest {

	@BeforeClass
    public static void runBeforeClass() {
        initProtempa("HELLP_remote");
    }

    @AfterClass
    public static void runAfterClass() {
        shutdownProtempa();
    }
	
	@Test
	public void emptyTest() {
		
	}

}
