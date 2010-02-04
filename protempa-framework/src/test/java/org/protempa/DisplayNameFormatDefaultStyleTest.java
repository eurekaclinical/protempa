package org.protempa;

import java.text.Format;

import org.protempa.DisplayNameFormat;

/**
 * Tests {@link DisplayNameFormat} with the default settings, which will use the
 * "full" style.
 * 
 * @author Andrew Post
 * 
 */
public final class DisplayNameFormatDefaultStyleTest extends
		AbstractDisplayNameFormatFullStyleTest {

	@Override
	protected Format displayFormatInstance() {
		return DisplayNameFormat.getInstance();
	}

}
