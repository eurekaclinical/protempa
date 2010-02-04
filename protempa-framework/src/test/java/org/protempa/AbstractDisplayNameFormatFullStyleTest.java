package org.protempa;

import java.text.Format;

import org.protempa.DisplayNameFormat;
import org.protempa.KnowledgeBase;
import org.protempa.PrimitiveParameterDefinition;

import junit.framework.TestCase;

/**
 * Tests {@link DisplayNameFormat} for proper formatting in the "full" style.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractDisplayNameFormatFullStyleTest extends TestCase {
	private KnowledgeBase knowledgeBase;
	private Format displayNameFormat;

	protected AbstractDisplayNameFormatFullStyleTest() {

	}

	protected void setUp() throws Exception {
		this.knowledgeBase = new KnowledgeBase();
		this.displayNameFormat = displayFormatInstance();
	}

	protected void tearDown() throws Exception {
		this.knowledgeBase = null;
	}

	public void testPrimitiveParameterDefinitionWithDisplayNameAndAbbreviatedDisplayName() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");
		d.setDisplayName("test display name");
		d.setAbbreviatedDisplayName("test abbreviated display name");

		assertEquals("test display name", this.displayNameFormat.format(d));
	}

	public void testPrimitiveParameterDefinitionWithAbbreviatedDisplayNameOnly() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");
		d.setAbbreviatedDisplayName("test abbreviated display name");

		assertEquals("test", this.displayNameFormat.format(d));
	}

	public void testPrimitiveParameterDefinitionWithNoDisplayName() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");

		assertEquals("test", this.displayNameFormat.format(d));
	}

	protected abstract Format displayFormatInstance();
}
