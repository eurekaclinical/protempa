package org.protempa.ksb.protege;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { RemoteLowLevelAbstractionsTest.class,
		RemoteHighLevelAbstractionsTest.class, RemoteEventsTest.class,
		RemoteHELLPOneKeyTest.class })
public class AllRemoteTests {
    
}
