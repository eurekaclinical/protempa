/**
 * Copyright 2011 Emory University
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.protempa.xml;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Manage the XML DocumentBuilder object for Protempa.
 * 
 * @author mgrand
 */
class DocumentBuilderManager {
	private static Logger myLogger = Logger.getLogger(QueryToXMLConverter.class.getName());

	private static DocumentBuilder documentBuilder = null;

	private DocumentBuilderManager() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return the document Builder that we are using.
	 * 
	 * @throws ParserConfigurationException
	 *             if there is a problem configuring the DocumentBuilder object.
	 */
	static synchronized DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if (documentBuilder == null) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				documentBuilder = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				String msg = "Error creating an xml DocumentBuilder object.";
				myLogger.log(Level.SEVERE, msg, e);
				throw e;
			}
		}
		return documentBuilder;
	}

}
