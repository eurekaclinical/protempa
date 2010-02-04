package org.protempa.ksb.protege;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	LocalLowLevelAbstractionsTest.class,
	LocalHighLevelAbstractionsTest.class,
	LocalEventsTest.class,
	LocalHELLPOneKeyTest.class
})
public class AllLocalTests {
    
}
