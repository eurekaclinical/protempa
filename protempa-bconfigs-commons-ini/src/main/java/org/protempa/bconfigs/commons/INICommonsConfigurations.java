/*
 * #%L
 * Protempa Commons INI Backend Configurations
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.bconfigs.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.protempa.backend.Backend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationRemoveException;
import org.protempa.backend.ConfigurationsSaveException;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.backend.InvalidPropertyValueException;

/**
 * Implements configurations for Protempa using INI files. A configuration id
 * is the name of an INI file that is in a directory specified in the 
 * constructor.
 *
 * @author Andrew Post
 */
public class INICommonsConfigurations implements Configurations {

    /**
     * The default directory for configurations 
     * (<code>.protempa-configs</code> in the user's home directory).
     */
    public static final File DEFAULT_DIRECTORY =
            new File(FileUtils.getUserDirectory(), ".protempa-configs");
    
    /**
     * The name of the system property 
     * (<code>protempa.inicommonsconfigurations.pathname</code>) for specifying 
     * the configuration file directory.
     */
    public static final String DIRECTORY_SYSTEM_PROPERTY =
            "protempa.inicommonsconfigurations.pathname";

    static {
        DefaultExpressionEngine engine = new DefaultExpressionEngine();
        engine.setPropertyDelimiter("|");
        HierarchicalINIConfiguration.setDefaultExpressionEngine(engine);
    }
    private File directory;

    /**
     * Creates an INI file configurations instance. Using this constructor is
     * equivalent to specifying a
     * <code>pathname</code> of
     * <code>null</code> in the one-argument constructor.
     */
    public INICommonsConfigurations() {
        this(null);
    }

    /**
     * Creates an INI file configurations instance that looks for configuration
     * files in the specified directory. If <code>null</code>, it first looks 
     * for a system property, 
     * <code>protempa.inicommonsconfigurations.pathname</code> for a pathname.
     * If unspecified, it looks for configuration files in the default location
     * (see {@link #DEFAULT_FILE}).
     *
     * @param directory a directory.
     */
    public INICommonsConfigurations(File directory) {
        if (directory != null) {
            this.directory = directory;
        } else {
            String pathnameFromSystemProperty =
                    System.getProperty(DIRECTORY_SYSTEM_PROPERTY);
            if (pathnameFromSystemProperty != null) {
                this.directory = new File(pathnameFromSystemProperty);
            }

        }
        if (this.directory == null) {
            this.directory = DEFAULT_DIRECTORY;
        }
        CommonsUtil.logger().log(Level.FINE, "Using configurations path {0}",
                this.directory);
    }

    /**
     * Returns the directory in which configuration files are expected to be
     * found.
     *
     * @return a directory {@link File}.
     */
    public File getDirectory() {
        return this.directory;
    }

    @Override
    public <B extends Backend> List<BackendInstanceSpec<B>> load(
            String configurationsId,
            BackendSpec<B> backendSpec)
            throws ConfigurationsNotFoundException, ConfigurationsLoadException {
        if (configurationsId == null) {
            throw new IllegalArgumentException(
                    "configurationId cannot be null");
        }
        if (backendSpec == null) {
            throw new IllegalArgumentException(
                    "backendSpec cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsLoadException("Cannot create directory "
                    + this.directory);
        }
        
        File configFile = new File(this.directory, configurationsId);
        if (!configFile.exists()) {
            throw new ConfigurationsNotFoundException("No such configuration file: " + configFile.getAbsolutePath());
        }
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            config.load(configFile.getAbsolutePath());
            Set<String> sectionNames = config.getSections();
            List<BackendInstanceSpec<B>> results =
                    new ArrayList<>();
            List<Integer> sectionIndices = new ArrayList<>();
            Map<Integer, String> sectionsMap = new HashMap<>();
            for (String sectionName : sectionNames) {
                int indexOfSep = sectionName.lastIndexOf('_');
                String i = sectionName.substring(indexOfSep + 1);
                Integer ii = Integer.valueOf(i);
                sectionIndices.add(ii);
                sectionsMap.put(ii, sectionName);
            }
            Collections.sort(sectionIndices);
            for (Integer sectionIndex : sectionIndices) {
                String sectionName = sectionsMap.get(sectionIndex);
                String specId = sectionName.substring(0,
                        sectionName.lastIndexOf('_'));
                if (!specId.equals(backendSpec.getId())) {
                    continue;
                }
                Configuration section = config.subset(sectionName);
                BackendInstanceSpec<B> backendInstanceSpec =
                        backendSpec.newBackendInstanceSpec();
                backendInstanceSpec.setConfigurationsId(configurationsId);
                for (Iterator<String> itr = section.getKeys(); itr.hasNext();) {
                    String key = itr.next();
                    backendInstanceSpec.parseProperty(key,
                            section.getString(key));
                }
                results.add(backendInstanceSpec);
            }
            return results;
        } catch (InvalidPropertyNameException ex) {
            throw new ConfigurationsLoadException(ex);
        } catch (InvalidPropertyValueException ex) {
            throw new ConfigurationsLoadException(ex);
        } catch (ConfigurationException ex) {
            throw new ConfigurationsLoadException(ex);
        } catch (NumberFormatException ex) {
            throw new ConfigurationsLoadException(ex);
        }
    }

    @Override
    public void save(String configurationsId,
            List<BackendInstanceSpec> backendInstanceSpecs)
            throws ConfigurationsSaveException {
        if (configurationsId == null) {
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsSaveException("Cannot create directory "
                    + this.directory);
        }
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            File configurationsPath = 
                    new File(this.directory, configurationsId);
            int i = 0;
            for (BackendInstanceSpec backendInstanceSpec :
                    backendInstanceSpecs) {
                backendInstanceSpec.setConfigurationsId(configurationsId);
                BackendSpec backendSpec = backendInstanceSpec.getBackendSpec();
                List<BackendPropertySpec> specs =
                        backendInstanceSpec.getBackendPropertySpecs();
                for (BackendPropertySpec spec : specs) {
                    config.setProperty(backendSpec.getId() + "_" + i
                            + "|" + spec.getName(),
                            backendInstanceSpec.getProperty(spec.getName()));
                }
                i++;
            }

            config.save(configurationsPath.getPath());
        } catch (InvalidPropertyNameException ex) {
            throw new ConfigurationsSaveException(ex);
        } catch (ConfigurationException ex) {
            throw new ConfigurationsSaveException(ex);
        }
    }

    /**
     * Loads an INI file with the specified name in the configuration file
     * directory.
     * 
     * @param configurationId the INI file's name.
     * @return the ids of the sections of the file.
     * 
     * @throws ConfigurationsLoadException if an error occurs reading the file.
     */
    @Override
    public List<String> loadConfigurationIds(String configurationId)
            throws ConfigurationsLoadException {
        if (configurationId == null) {
            throw new IllegalArgumentException(
                    "configurationId cannot be null");
        }
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            config.load(
                    new File(this.directory, 
                    configurationId).getAbsolutePath());
            Set<String> sections = config.getSections();
            String[] result = new String[sections.size()];
            for (String section : sections) {
                int index =
                        Integer.parseInt(section.substring(
                        section.lastIndexOf('_') + 1));
                if (result[index] != null) {
                    throw new ConfigurationsLoadException(
                            "duplicate indices on sections");
                } else if (index > result.length - 1) {
                    throw new ConfigurationsLoadException("index too high: "
                            + index);
                }
                result[index] =
                        section.substring(0, section.lastIndexOf('_'));
            }
            return Arrays.asList(result);
        } catch (ConfigurationException ex) {
            throw new ConfigurationsLoadException(
                    "Could not load configuration " + configurationId, ex);
        }
    }

    @Override
    public void remove(String configurationId)
            throws ConfigurationRemoveException {
        if (configurationId == null) {
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        }
        File f = new File(this.directory + configurationId);
        if (f.exists() && !f.delete()) {
            throw new ConfigurationRemoveException(
                    "Could not remove " + configurationId);
        }
    }
}
