/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
