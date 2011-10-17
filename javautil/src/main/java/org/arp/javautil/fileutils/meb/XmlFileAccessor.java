package org.arp.javautil.fileutils.meb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * This class is used to retrieve and hold an XML file, and provides simple to
 * use access methods.
 * 
 * @author Michael E. Brown
 * 
 */
public class XmlFileAccessor {

	private static final String fileSeparater = System
			.getProperty("file.separator");
	private static final String userHome = System.getProperty("user.home");

	private Document xmlDoc = null;
	private File file = null;
	private XPath xPath = null;

	private Logger logger = null;

	private XmlFileAccessor() {
	}

	public XmlFileAccessor(String filename, Logger logger) {
		this(filename, logger, false);
	}

	/**
	 * Constructor to create the collections of mappings.
	 * 
	 * @param filename
	 *            The name of the file containing the mappings.
	 */
	XmlFileAccessor(String filename, Logger logger, boolean trackChanges) {

		this.logger = logger;

		file = new File(filename);
		if (!file.exists())
			file = new File(userHome + fileSeparater + filename);
		if (!file.exists()) {
			logger.severe("Could not find '" + filename
					+ "' in current directory or home directory.");
		} else {
			try {

				xmlDoc = parseXmlFile(file.getAbsolutePath());

			} catch (ParserConfigurationException e) {
				logger.severe(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (SAXException e) {
				logger.severe(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.severe(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	private Document parseXmlFile(String xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Use the factory to create a builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(xmlFile);
	}

	HashMap<String, HashMap<String, String>> rtnMap = new HashMap<String, HashMap<String, String>>();

	public HashMap<String, HashMap<String, String>> getAllAttributesIntoMap(
			String... pathnodes) throws XPathExpressionException {
		rtnMap.clear();
		getAllAttributes(true, pathnodes);
		return rtnMap;
	}

	ArrayList<HashMap<String, String>> rtnList = new ArrayList<HashMap<String, String>>();

	public ArrayList<HashMap<String, String>> getAllAttributesIntoList(
			String... pathnodes) throws XPathExpressionException {
		rtnList.clear();
		getAllAttributes(false, pathnodes);
		return rtnList;
	}

	private void getAllAttributes(boolean usemap, String... pathnodes)
			throws XPathExpressionException {

//		System.out.println("-------");
		String base = buildXmlPath(pathnodes);

		String tmp = base;
		XPathExpression expr = xPath.compile(tmp);
		Object result = expr.evaluate(xmlDoc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		int nbrNodes = nodes.getLength();
//		System.out.format("There are %s %s nodes\n", nbrNodes, tmp);
		if (nbrNodes < 1)
			return;

		tmp = base + "[1]/@*";
		expr = xPath.compile(tmp);
		result = expr.evaluate(xmlDoc, XPathConstants.NODESET);
		nodes = (NodeList) result;
		int nbrAttributes = nodes.getLength();
		Set<String> attributeNames = new TreeSet<String>();
		for (int i = 0; i < nbrAttributes; i++) {
			String name = nodes.item(i).getNodeName();
			System.out.format("Name: %s\n", name);
			// if (name.equals("key"))
			// usemap = true;
			attributeNames.add(nodes.item(i).getNodeName());
		}
		System.out.format("There are %s attributes per first node\n",
				nbrAttributes);

		for (int n = 0; n < nbrNodes; n++) {
			HashMap<String, String> hashmap = new HashMap<String, String>();
			String key = "";
			for (String attributeName : attributeNames) {
				tmp = base + "[" + (n + 1) + "]" + "/@" + attributeName;
				expr = xPath.compile(tmp);
				result = expr.evaluate(xmlDoc, XPathConstants.NODESET);
				nodes = (NodeList) result;
				int count = nodes.getLength();
				if (count == 1) {
					String value = nodes.item(0).getNodeValue();
					hashmap.put(attributeName, value);
					if (key.equals(""))
						key = value;
					if (attributeName.equals("key"))
						key = value;
				} else {
					hashmap.put(attributeName, "");
				}
			}
			if (usemap)
				rtnMap.put(key, hashmap);
			else
				rtnList.add(hashmap);
		}
	}

	public ArrayList getAllAttributesNamedValue(String... pathnodes) {

		ArrayList<String> list = new ArrayList<String>();
		System.out.println("-------");
		String base = buildXmlPath(pathnodes);

		String tmp = base;
		XPathExpression expr;
		try {
			expr = xPath.compile(tmp);

			Object result = expr.evaluate(xmlDoc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			int nbrNodes = nodes.getLength();
			System.out.format("There are %s %s nodes\n", nbrNodes, tmp);
			if (nbrNodes < 1)
				return list;

			for (int n = 0; n < nbrNodes; n++) {
				tmp = base + "[" + (n + 1) + "]" + "/@value";
				expr = xPath.compile(tmp);
				result = expr.evaluate(xmlDoc, XPathConstants.NODESET);
				nodes = (NodeList) result;
				int count = nodes.getLength();
				if (count == 1) {
					String value = nodes.item(0).getNodeValue();
					list.add(value);
				} else {
					list.add("");
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}


	public String getAttribute(String attributeName, String... pathnodes) {
		String path = buildXmlPath(pathnodes);
		path += "/@" + attributeName;
		return this.getXpath(path);
	}

	
	public String getAttributeNamedValue(String... pathnodes) {
		String path = buildXmlPath(pathnodes);
		path += "/@value";
		return this.getXpath(path);
	}	
	
	public String getSafeValue( String defaultValue, String... pathnodes){
		String value = this.getAttributeNamedValue(pathnodes);
		if(( value == null ) || ( value.equals("")))
			return defaultValue;
		else
			return value;
	}
	
//	public String getAttributeNamedValue(String defaultValue, String... pathnodes) {
//	String path = buildXmlPath(pathnodes);
//	path += "/@value";
//	return this.getXpath(path, defaultValue);
//}
	
	public String getAttributeByKey(String keyValue, String rtnAttributeName,
			String... pathnodes) {
		String path = buildXmlPath(pathnodes);
		path += "[@key='" + keyValue + "']/@" + rtnAttributeName;

		System.out.println(path);
		return (String) this.getXpath(path, XPathConstants.STRING);
	}

	public String getAttributeValueByKey(String keyValue, String... pathnodes) {
		String path = buildXmlPath(pathnodes);
		path += "[@key='" + keyValue + "']/@value";
		return this.getXpath(path);
	}
	
	public String getAttributeByAttribute(String attributeName,
			String attributeValue, String rtnAttributeName, String... pathnodes) {
		String path = buildXmlPath(pathnodes);
		path += "[@" + attributeName + "='" + attributeValue + "']/@"
				+ rtnAttributeName;
		return (String) this.getXpath(path, XPathConstants.STRING);
	}

	public String buildXmlPath(String... pathnodes) {
		if (pathnodes[0].startsWith("//"))
			return pathnodes[0];
		String base = "/";
		for (String pathnode : pathnodes) {
			base += "/" + pathnode;
		}
		return base;
	}
	

	public void showAttributes(HashMap<String, HashMap<String, String>> map,
			String... pathnodes) {
		System.out.format("\n%s\n", buildXmlPath(pathnodes));
		Iterator<String> it = map.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			String key = it.next();
			System.out.format("%7d Key: %s\n", ++count, key);
			HashMap<String, String> attributes = map.get(key);
			Iterator<String> it2 = attributes.keySet().iterator();
			while (it2.hasNext()) {
				String key2 = it2.next();
				String value2 = attributes.get(key2);
				System.out.format("        %s = %s\n", key2, value2);
			}
		}
	}


	//
	// The methods getXpath that are below...
	// do not manipulate the xpath expression
	// and are for convenience and safety.
	//
	public String getXpath(String expression) {
		return (String) getXpath(expression, XPathConstants.STRING);
	}

//	public String getXpath(String expression, String defaultValue) {
//		String tmp = (String) getXpath(expression, XPathConstants.STRING);
//		if (tmp == null)
//			return defaultValue;
//		else if (tmp.equals(""))
//			return defaultValue;
//		else
//			return tmp;
//	}

	public Object getXpath(String expression, QName returnType) {
		if (xPath == null) {
			xPath = XPathFactory.newInstance().newXPath();
		}
		if (xmlDoc != null) {
			try {
				XPathExpression xPathExpression = xPath.compile(expression);
				return xPathExpression.evaluate(xmlDoc, returnType);
			} catch (XPathExpressionException ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			logger.warning("There is no xml Document");
			logger.warning("Results of readPath are null");
			return null;
		}

	}



}
