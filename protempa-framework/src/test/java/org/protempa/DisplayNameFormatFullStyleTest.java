package org.protempa;

import java.text.Format;

import org.protempa.DisplayNameFormat;

/**
 * Tests {@link DisplayNameFormat} with "full" style specified.
 * 
 * @author Andrew Post
 * 
 */
public final class DisplayNameFormatFullStyleTest extends
		AbstractDisplayNameFormatFullStyleTest {

	@Override
	protected Format displayFormatInstance() {
		return DisplayNameFormat.getInstance(DisplayNameFormat.Style.FULL);
	}

}
