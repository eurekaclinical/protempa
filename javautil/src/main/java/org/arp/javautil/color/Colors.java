package org.arp.javautil.color;

import java.awt.Color;

/**
 * @author Andrew Post
 */
public class Colors {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private Colors() {

	}

	public static String colorToHexCode(Color color) {
		if (color != null) {
			int rgb = color.getRGB();

			char[] cdata = new char[7];

			cdata[0] = '#';

			for (int i = 1; i < 7; i++) {
				cdata[7 - i] = HEX_DIGITS[rgb & 0xF];
				rgb = rgb >> 4;
			}
			return new String(cdata);
		} else {
			return null;
		}

	}
}
