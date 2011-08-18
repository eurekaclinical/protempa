package org.protempa.proposition;

import java.util.Collections;
import java.util.UUID;

import junit.framework.TestCase;

import org.protempa.DataSourceBackendDataSourceType;

public class PropositionVisitorTest extends TestCase {
	private static class PrimitiveParameterVisitor extends AbstractPropositionVisitor {
		boolean found;
		
		@Override
		public void visit(PrimitiveParameter primitiveParameter) {
			this.found = true;
		}
		
	}
	public void testPrimitiveParameter() throws Exception {
		PrimitiveParameterVisitor v = new PrimitiveParameterVisitor();
		PrimitiveParameter p = new PrimitiveParameter("test", uid());
		p.setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
		v.visit(Collections.singleton(p));
		assertTrue(v.found);
	}
	
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
