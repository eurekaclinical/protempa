/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend.ksb;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.arp.javautil.io.IOUtil;
import org.drools.util.StringUtils;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.value.ValueType;

/**
 * A simple way of defining primitive parameters. It looks for three properties:
 * PRIMITIVE_PARAMETER_DEFINITIONS, EVENT_DEFINITIONS and CONSTANT_DEFINITIONS.
 * PRIMITIVE_PARAMETER_DEFINITIONS specifies a properties file on the classpath
 * containing the primitive parameter definitions, and EVENT_DEFINITIONS
 * specifies a properties file on the classpath containing the event
 * definitions. CONSTANT_DEFINITIONS specifies a properties file on the
 * classpath containing the constant definitions. The properties files are
 * loaded using this class' class loader, and relative classpaths are resolved
 * relative to this class.
 *
 * The primitive parameter definitions properties file specifies one line per
 * primitive parameter definition in the format: id =
 * displayName|abbrevDisplayName|valueFactoryStr
 *
 * The event definitions properties file specifies one line per event definition
 * in the format: id = displayName|abbrevDisplayName
 *
 * The constant definitions properties file specifies one line per constant
 * definition in the format: id = displayName|abbrevDisplayName
 *
 * @author Andrew Post
 */
public abstract class AbstractPropertiesFileKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {

    private Properties primParams;
    private Properties events;
    private Properties constants;
    private Map<String, String[]> primParamsMap;
    private Map<String, String[]> eventsMap;
    private Map<String, String[]> constantsMap;

    protected abstract String getEventDefinitionsPropertiesResourceName();

    protected abstract String getPrimitiveParameterDefinitionsPropertiesResourceName();

    protected abstract String getConstantDefinitionsPropertiesResourceName();

    @Override
    public final void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        try {
            this.primParams = IOUtil.createPropertiesFromResource(this.getClass(),
                    getPrimitiveParameterDefinitionsPropertiesResourceName());
            this.events = IOUtil.createPropertiesFromResource(this.getClass(),
                    getEventDefinitionsPropertiesResourceName());
            this.constants = IOUtil.createPropertiesFromResource(this.getClass(),
                    getConstantDefinitionsPropertiesResourceName());
            if (getPrimitiveParameterDefinitionsPropertiesResourceName() == null
                    && getEventDefinitionsPropertiesResourceName() == null
                    && getConstantDefinitionsPropertiesResourceName() == null) {
                KSUtil.logger().warning(
                        getClass().getName()
                        + " does not define PRIMITIVE_PARAMETER_DEFINITIONS or EVENT_DEFINITIONS or CONSTANT_DEFINITIONS in its properties file.");
            }
            KSUtil.logger().fine(getClass().getName() + " initialized");
        } catch (IOException e) {
            throw new KnowledgeSourceBackendInitializationException(
                    "Could not initialize " + getClass().getName(), e);
        }
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        PropositionDefinition propDef = readEventDefinition(id);
        if (propDef != null) {
            return propDef;
        }
        if ((propDef = readConstantDefinition(id)) != null) {
            return propDef;
        }
        if ((propDef = readPrimitiveParameterDefinition(id)) != null) {
            return propDef;
        }
        return propDef;
    }
    
    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }
    
    @Override
    public String[] readAbstractedInto(String propId) {
        return StringUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] readIsA(String propId) {
        return StringUtils.EMPTY_STRING_ARRAY;
    }

    private EventDefinition readEventDefinition(String id)
            throws KnowledgeSourceReadException {
        readEventDefinitions();
        String[] vals = this.eventsMap.get(id);
        if (vals == null) {
            return null;
        }
        EventDefinition def = new EventDefinition(id);
        def.setDisplayName(vals[0]);
        def.setAbbreviatedDisplayName(vals[1]);
        return def;
    }

    private void readEventDefinitions()
            throws KnowledgeSourceReadException {
        if (this.eventsMap == null) {
            this.eventsMap = new HashMap<String, String[]>();
            extractDefinitions(this.events, this.eventsMap, 2,
                    getEventDefinitionsPropertiesResourceName());
        }
    }

    private ConstantDefinition readConstantDefinition(String id)
            throws KnowledgeSourceReadException {
        readConstantDefinitions();
        String[] vals = this.constantsMap.get(id);
        if (vals == null) {
            return null;
        }
        ConstantDefinition def =
                new ConstantDefinition(id);
        def.setDisplayName(vals[0]);
        def.setAbbreviatedDisplayName(vals[1]);
        return def;
    }

    private void readConstantDefinitions()
            throws KnowledgeSourceReadException {
        if (this.constantsMap == null) {
            this.constantsMap = new HashMap<String, String[]>();
            extractDefinitions(this.constants, this.constantsMap, 2,
                    getConstantDefinitionsPropertiesResourceName());
        }
    }

    private PrimitiveParameterDefinition readPrimitiveParameterDefinition(
            String id)
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
                id);
        String displayName = vals[0];
        def.setDisplayName(displayName.length() > 0 ? displayName : null);
        String abbrevDisplayName = vals[1];
        def.setAbbreviatedDisplayName(abbrevDisplayName.length() > 0
                ? abbrevDisplayName : null);
        def.setValueType(ValueType.valueOf(vals[2]));
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
        this.constantsMap = null;
    }

    private boolean extractDefinitions(Properties properties,
            Map<String, String[]> defsMap, int expectedLength, String name) {
        try {
            for (Enumeration<?> enu = properties.propertyNames();
                    enu.hasMoreElements();) {
                String id = (String) enu.nextElement();
                String val = properties.getProperty(id);
                String[] elts = val.split("\\|");
                if (elts.length != expectedLength) {
                    throw new IOException("invalid definition in " + name
                            + ": " + id + ": " + val + "(expected length="
                            + expectedLength + ", but actual length="
                            + elts.length + ")");
                }
                Logger logger = KSUtil.logger();
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                            "New definition: {0}; {0}", new Object[]{id, Arrays.asList(elts)});
                }
                defsMap.put(id, elts);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
