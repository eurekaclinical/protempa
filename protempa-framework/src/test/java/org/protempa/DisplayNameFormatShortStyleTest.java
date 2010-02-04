package org.protempa;

import java.text.Format;

import org.protempa.DisplayNameFormat;
import org.protempa.KnowledgeBase;
import org.protempa.PrimitiveParameterDefinition;

import junit.framework.TestCase;

/**
 * Tests {@link DisplayNameFormat} for proper formatting in the "short" style.
 * 
 * @author Andrew Post
 * 
 */
public class DisplayNameFormatShortStyleTest extends TestCase {
	private KnowledgeBase knowledgeBase;
	private Format displayNameFormat;

	protected void setUp() throws Exception {
		this.knowledgeBase = new KnowledgeBase();
		this.displayNameFormat = DisplayNameFormat
				.getInstance(DisplayNameFormat.Style.SHORT);
	}

	protected void tearDown() throws Exception {
		this.knowledgeBase = null;
	}

	public void testPrimitiveParameterDefinitionWithDisplayNameAndAbbreviatedDisplayName() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");
		d.setDisplayName("test display name");
		d.setAbbreviatedDisplayName("test abbreviated display name");

		assertEquals("test abbreviated display name", this.displayNameFormat.format(d));
	}

	public void testPrimitiveParameterDefinitionWithDisplayNameOnly() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");
		d.setDisplayName("test display name");

		assertEquals("test", this.displayNameFormat.format(d));
	}

	public void testPrimitiveParameterDefinitionWithNoDisplayName() {
		PrimitiveParameterDefinition d = new PrimitiveParameterDefinition(
				this.knowledgeBase, "test");

		assertEquals("test", this.displayNameFormat.format(d));
	}
}
