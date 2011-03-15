package org.protempa.proposition;

import java.util.Collections;

import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.PrimitiveParameter;

import junit.framework.TestCase;

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
		PrimitiveParameter p = new PrimitiveParameter("test");
		p.setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
		v.visit(Collections.singleton(p));
		assertTrue(v.found);
	}
}
