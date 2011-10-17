package org.arp.javautil.fileutils.meb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.codehaus.jackson.JsonGenerationException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.SerializationConfig;
//import oracle.net.aso.e;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used to retrieve and hold simple maps of key/value pairs by
 * categories. It can optionally be used to capture unknown values and persist
 * these to the original XML file.
 * 
 * @author Michael E. Brown
 * 
 *         Usage (preferred for convenience):
 * 
 *         // Read in the configuration file containing key/value pairs in
 *         categories // the configuration file, "registry-mappings.xml", exist
 *         either in: // 1) current directory // 2) home directory // Currently
 *         this file is in SVN at src/main/resources/registry-mappings.xml
 *         CategoryMaps dm = new CategoryMaps("registry-mappings.xml",true); ...
 *         // Request the mapping for 'Male' in the category of 'sex' String sex
 *         = dm.getMapping("sex", "Male"); ... // Request the mapping for 'BOS'
 *         in the category of 'language' String language =
 *         dm.getMapping("language", "BOS"); ... // This next request (with the
 *         current config file) will cause a default entry // of 'Southern
 *         Baptist/?unknown?' to be created (due to trackChanges set to // true
 *         above), and will be persisted to the configuration file if/when //
 *         persist() is called. String religion = dm.getMapping("religion",
 *         "Southern Baptist"); ... dm.persist();
 * 
 *         Usage (preferred for performance):
 * 
 *         // Read in the configuration file containing key/value pairs in
 *         categories CategoryMaps dm = new
 *         CategoryMaps("registry-mappings.xml",true); ... HashMap mapLanguage =
 *         dm.getMap("language"); ... String language = mapLanguage.get("BOS");
 */
public class CategoryMaps {

	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

	// This holds a collection of maps (one map for each category),
	// each of which contains a list of text mappings (in the form of key/value
	// pairs).
	private HashMap<String, HashMap<String, String>> maps = new HashMap<String, HashMap<String, String>>();
	// Let's keep track of the categories too
	private List<String> categorylist = new ArrayList<String>();

	private boolean trackChanges = true;
	private String unknown = "?unknown?";
	private boolean debug = false;
	private boolean parseComplete = false;

	static final private String fileSeparater = System
			.getProperty("file.separator");
	static final private String userHome = System.getProperty("user.home");

	private Document doc = null;
	private File file = null;
	
	// These two items are used by "INI" type files
	private String comment = "#"; // character string that starts comment
	private String equal = "="; // character string that separates keys from values

	enum FILETYPE {
		UNKNOWN, XML, INI
	}

	private FILETYPE filetype = FILETYPE.UNKNOWN;

	private CategoryMaps() {
	}

	public CategoryMaps(String filename) {
		this(filename, false);
	}

	/**
	 * Constructor to create the collections of mappings.
	 * 
	 * @param filename
	 *            The name of the file containing the mappings.
	 */
	public CategoryMaps(String filename, boolean trackChanges) {

		file = new File(filename);
		if (!file.exists())
			file = new File(userHome + fileSeparater + filename);
		if (!file.exists())
			file = new File(userHome + fileSeparater + ".protempa-configs"
					+ fileSeparater + filename);
		if (!file.exists()) {
			System.err.println("Could not find '" + filename
					+ "' in current directory or home directory.");
		}

		if (filename.endsWith(".xml")) {
			filetype = FILETYPE.UNKNOWN;
			try {
				doc = parseXmlFile(file.getAbsolutePath());
				collectItems(doc);
				parseComplete = true;
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			filetype = FILETYPE.INI;
			parseIniTypeFile(file.getAbsolutePath());
		}

		this.trackChanges = trackChanges;
	}

	private void parseIniTypeFile(String filename) {
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String str;
			String categoryId = "";
			int n = 0;
			
			while ((str = in.readLine()) != null) {

				// Remove any comment and trim
				n = str.indexOf(comment);
				if( n > 0 )
					str = str.substring(0,n);
				str = str.trim();
				
				if (str.startsWith("[")) {
					categoryId = str.replace("[", "").replace("]", "");
					HashMap<String, String> HashMap = new HashMap<String, String>();
					maps.put(categoryId, HashMap);
					categorylist.add(categoryId);
				}else {
					String[] fields = str.split(equal);
					if (fields.length == 2) {
						String key = fields[0].trim();
						String value = fields[1].trim();
						putMapping(categoryId, key, value);
					}
				}
			}
			in.close();
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	private Document parseXmlFile(String xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Use the factory to create a builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(xmlFile);
	}

	/**
	 * Experimental
	 */
	// public void persistAsJSON() {
	// ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	// mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
	//
	// try {
	// mapper.writeValue(new File("registry-mapping.json"), maps);
	// } catch (JsonGenerationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JsonMappingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * Call this method to persist any updates to the category maps.
	 */
	public void persist() {
		String pathname = file.getAbsolutePath();
		persist(pathname);
	}

	
	public void persist(String pathname) {
		if (filetype == FILETYPE.XML)
			persistXml(pathname);
		else if (filetype == FILETYPE.INI)
			persistIni(pathname);
	}

	private void persistIni(String pathname) {

		String key = "";
		String value = "";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pathname));

			for (String category : this.categorylist) {

				writer.append("[");
				writer.append(category);
				writer.append("]");
				writer.newLine();

				HashMap<String, String> map = maps.get(category);
				Iterator<String> it = map.keySet().iterator();

				while (it.hasNext()) {

					key = it.next();
					value = map.get(key);

					writer.append(key);
					writer.append(" = ");
					writer.append(value);
					writer.newLine();
				}
				writer.newLine();

			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void persistXml(String pathname) {

		if (trackChanges) {
			PrintStream ps = null;
			if (debug) {
				ps = System.out;
			} else {
				try {
					ps = new PrintStream(pathname);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(ps);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = tFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");

				transformer.transform(source, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method can be used to retrieve the value of a key/value pair in a
	 * particular category. If trackChanges is true, and a key is not found,
	 * then an entry is made for that key with the value of the variable
	 * unknown.
	 * 
	 * However, better performance can be achieved if the particular map is
	 * initially retrieved, and then updated directlry.
	 * 
	 * @param categoryName
	 *            name of the category
	 * @param key
	 *            the key of the key/value pair
	 * @return returns the value of the key/value pair, or
	 */
	public String getMapping(String categoryName, String key) {
		if (trackChanges) {
			if (!maps.get(categoryName).containsKey(key))
				putMapping(categoryName, key, unknown);
		}
		return (String) maps.get(categoryName).get(key);
	}

	/**
	 * Returns the list of categories found in the configuration file.
	 * 
	 * @return Returns a string array.
	 */
	public List<String> getCategories() {
		return categorylist;
	}

	/**
	 * Used to put a new mapping (key/value pair) to a specific category. if
	 * trackChanges is true, then this is called internally when requesting a
	 * key and it is not found.
	 * 
	 * @param categoryName
	 *            the category name
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putMapping(String categoryName, String key, String value) {
		maps.get(categoryName).put(key, value);
		if (trackChanges && parseComplete) {
			updateDoc(categoryName, key, value);
		}
	}

	private void updateDoc(String categoryName, String key, String value) {
		NodeList list = doc.getElementsByTagName("category");
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			NamedNodeMap namedNodeMap = element.getAttributes();
			String catName = namedNodeMap.getNamedItem("id").getNodeValue();
			if (catName.equals(categoryName)) {
				if (debug)
					System.out.format("Found: %s\n", catName);
				Element newChild = doc.createElement("entry");
				newChild.setAttribute("key", key);
				newChild.setAttribute("value", value);
				// element.insertBefore(newChild, element.getFirstChild());
				element.appendChild(newChild);
				element.normalize();
			}
		}

	}

	/**
	 * Use to put a key/value pair to a category.
	 * 
	 * @param categoryName
	 *            the category name
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void removeMapping(String categoryName, String key, String value) {
		maps.get(categoryName).remove(key);
	}

	/*
	 * Returns a map for a specific category. If the map is used directly then
	 * the use of trackChanges is somewhat obviated. However, this direct access
	 * may be needed for better performance.
	 */
	public Map<?, ?> getMap(String categorySortOf) {
		// Try for exact match first
		if( maps.containsKey(categorySortOf))
			return maps.get(categorySortOf);
		else{
			// search for a contained string
			String key = "";
			Iterator<String> it = maps.keySet().iterator();
			while( it.hasNext()){
				key = it.next();
				if( key.contains(categorySortOf)){
					return maps.get(key);
				}
			}
		}
		return null;
	}

	void collectItems(Document doc) {
		// Get a list of all elements in the document
		NodeList list = doc.getElementsByTagName("*");
		String categoryId = "";
		String categoryPrefix = "";
		for (int i = 0; i < list.getLength(); i++) {
			// Get element
			Element element = (Element) list.item(i);
			NamedNodeMap namedNodeMap = element.getAttributes();
			String nodename = element.getNodeName();
			if (nodename.equals("category")) {
				categoryId = namedNodeMap.getNamedItem("id").getNodeValue();
				categoryPrefix = namedNodeMap.getNamedItem("prefix")
						.getNodeValue();

				HashMap<String, String> HashMap = new HashMap<String, String>();
				maps.put(categoryId, HashMap);
				categorylist.add(categoryId);
				if (debug)
					System.out.format("%s: %s %s\n", new String[] { nodename,
							categoryId, categoryPrefix });

			} else if (nodename.equals("entry")) {
				String key = namedNodeMap.getNamedItem("key").getNodeValue();
				String value = namedNodeMap.getNamedItem("value")
						.getNodeValue();
				putMapping(categoryId, key, value);
				if (debug)
					System.out.format("%s %s %s\n", new String[] { categoryId,
							key, value });

			} else {
				if (debug)
					System.out.format("%s (unhandled)\n", nodename);
			}
		}
	}

	/**
	 * @return the trackChanges
	 */
	public boolean isTrackChanges() {
		return trackChanges;
	}

	/**
	 * If set, and key is not found, then key is added with value of variable,
	 * 'unknown'.
	 * 
	 * @param trackChanges
	 *            the trackChanges to set
	 */
	public void setTrackChanges(boolean trackChanges) {
		this.trackChanges = trackChanges;
	}

	/**
	 * @return the unknown
	 */
	public String getUnknown() {
		return unknown;
	}

	/**
	 * The value of this variable is used when adding an entry for key values
	 * that were not found.
	 * 
	 * @param unknown
	 *            the unknown to set
	 */
	public void setUnknown(String unknown) {
		this.unknown = unknown;
	}

}
