package org.protempa.ksb;

import org.protempa.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.protempa.proposition.value.ValueFactory;

import org.arp.javautil.io.IOUtil;
import org.protempa.backend.BackendInstanceSpec;

/**
 * A simple way of defining primitive parameters. It looks for two properties:
 * PRIMITIVE_PARAMETER_DEFINITIONS and EVENT_DEFINITIONS.
 * PRIMITIVE_PARAMETER_DEFINITIONS specifies a properties file on the classpath
 * containing the primitive parameter definitions, and EVENT_DEFINITIONS
 * specifies a properties file on the classpath containing the event
 * definitions. The properties files are loaded using this class' class loader,
 * and relative classpaths are resolved relative to this class.
 * 
 * The primitive parameter definitions properties file specifies one line per
 * primitive parameter in the format: id =
 * displayName|abbrevDisplayName|valueFactoryStr
 * 
 * The event definitions properties file specifies one line per event in the
 * format: id = displayName|abbrevDisplayName
 * 
 * @author Andrew Post
 */
public abstract class AbstractPropertiesFileKnowledgeSourceBackend extends
		AbstractKnowledgeSourceBackend {

	private Properties primParams;

	private Properties events;

	private Map<String, String[]> primParamsMap;

	private Map<String, String[]> eventsMap;

    protected abstract String getEventDefinitionsPropertiesResourceName();

    protected abstract String
            getPrimitiveParameterDefinitionsPropertiesResourceName();

	public final void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
		try {
            this.primParams = IOUtil.createPropertiesFromResource(this
					.getClass(), 
                    getPrimitiveParameterDefinitionsPropertiesResourceName());
			this.events = IOUtil.createPropertiesFromResource(this.getClass(),
					getEventDefinitionsPropertiesResourceName());
			if (getPrimitiveParameterDefinitionsPropertiesResourceName() == null
                    && getEventDefinitionsPropertiesResourceName() == null) {
				KSUtil
						.logger()
						.warning(
								getClass().getName()
										+ " does not define PRIMITIVE_PARAMETER_DEFINITIONS or EVENT_DEFINITIONS in its properties file.");
			}
			KSUtil.logger().fine(getClass().getName() + " initialized");
		} catch (IOException e) {
			throw new KnowledgeSourceBackendInitializationException(
					"Could not initialize " + getClass().getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.AbstractKnowledgeSourceBackend#readEventDefinition(java.lang.String,
	 *      org.protempa.KnowledgeBase)
	 */
	@Override
	public final EventDefinition readEventDefinition(String id,
			KnowledgeBase protempaKnowledgeBase) 
            throws KnowledgeSourceReadException {
		readEventDefinitions(protempaKnowledgeBase);
		String[] vals = this.eventsMap.get(id);
		if (vals == null) {
			return null;
		}
		EventDefinition def = new EventDefinition(protempaKnowledgeBase, id);
		def.setDisplayName(vals[0]);
		def.setAbbreviatedDisplayName(vals[1]);
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.AbstractKnowledgeSourceBackend#readEventDefinitions(org.protempa.KnowledgeBase)
	 */
	private void readEventDefinitions(KnowledgeBase protempaKnowledgeBase)
        throws KnowledgeSourceReadException {
		if (this.eventsMap == null) {
			this.eventsMap = new HashMap<String, String[]>();
			extractDefinitions(this.events, this.eventsMap, 2, 
                    getEventDefinitionsPropertiesResourceName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.AbstractKnowledgeSourceBackend#readPrimitiveParameterDefinition(java.lang.String,
	 *      org.protempa.KnowledgeBase)
	 */
	@Override
	public final PrimitiveParameterDefinition readPrimitiveParameterDefinition(
			String id, KnowledgeBase protempaKnowledgeBase)
        throws KnowledgeSourceReadException {
		if (this.primParamsMap == null) {
			this.primParamsMap = new HashMap<String, String[]>();
			extractDefinitions(this.primParams, this.primParamsMap, 3,
					getPrimitiveParameterDefinitionsPropertiesResourceName());
		}
		String[] vals = this.primParamsMap.get(id);
		if (vals == null) {
			return null;
		}
		PrimitiveParameterDefinition def = new PrimitiveParameterDefinition(
				protempaKnowledgeBase, id);
		String displayName = vals[0];
		def.setDisplayName(displayName.length() > 0 ? displayName : null);
		String abbrevDisplayName = vals[1];
		def
				.setAbbreviatedDisplayName(abbrevDisplayName.length() > 0 
                ? abbrevDisplayName : null);
		def.setValueFactory(ValueFactory.toValueFactory(vals[2]));
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.AbstractKnowledgeSourceBackend#close()
	 */
	@Override
	public final void close() {
		this.eventsMap = null;
		this.primParamsMap = null;
	}

	private boolean extractDefinitions(Properties properties,
			Map<String, String[]> defsMap, int expectedLength, String name) {
		try {
			for (Enumeration<?> enu = properties.propertyNames(); enu
					.hasMoreElements();) {
				String id = (String) enu.nextElement();
				String val = properties.getProperty(id);
				String[] elts = val.split("\\|");
				if (elts.length != expectedLength) {
					throw new IOException("invalid definition in " + name
							+ ": " + id + ": " + val + "(expected length="
							+ expectedLength + ", but actual length="
							+ elts.length + ")");
				}
				KSUtil.logger().fine(
						"New definition: " + id + "; " + Arrays.asList(elts));
				defsMap.put(id, elts);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
