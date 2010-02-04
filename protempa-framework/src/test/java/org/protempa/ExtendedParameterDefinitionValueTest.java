package org.protempa;

import org.protempa.ExtendedParameterDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.value.NumberValue;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class ExtendedParameterDefinitionValueTest extends TestCase {

	private LowLevelAbstractionDefinition llad;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		KnowledgeBase kb = new KnowledgeBase();
		this.llad = new LowLevelAbstractionDefinition(kb, "TEST");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		llad = null;
	}

	public void testMatches() {
		ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
				llad.getId());
		completeDef.setAbbreviatedDisplayName("t");
		completeDef.setDisplayName("test");
		completeDef.setValue(new NumberValue(13));

		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		param.setInterval(new DefaultInterval());

		assertTrue(completeDef.getMatches(param));
	}

	public void testDoesMatchValue() {
		ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
				llad.getId());
		completeDef.setAbbreviatedDisplayName("t");
		completeDef.setDisplayName("test");
		completeDef.setValue(new NumberValue(13));

		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		param.setInterval(new DefaultInterval());

		assertTrue(completeDef.getMatches(param));
	}

	public void testDoesMatchNullValue() {
		ExtendedParameterDefinition nullValueDef = new ExtendedParameterDefinition(
				llad.getId());
		nullValueDef.setAbbreviatedDisplayName("t");
		nullValueDef.setDisplayName("test");

		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		param.setInterval(new DefaultInterval());

		assertTrue(nullValueDef.getMatches(param));
	}

	public void testDoesNotMatchOnValue() {
		ExtendedParameterDefinition def1 = new ExtendedParameterDefinition(llad
				.getId());
		def1.setAbbreviatedDisplayName("t");
		def1.setDisplayName("test");
		def1.setValue(new NumberValue(13));

		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(12));
		param.setInterval(new DefaultInterval());

		assertFalse(def1.getMatches(param));
	}

}
