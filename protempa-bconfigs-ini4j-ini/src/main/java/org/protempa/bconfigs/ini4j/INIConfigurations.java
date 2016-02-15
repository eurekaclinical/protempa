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
package org.protempa.bconfigs.ini4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;
import org.protempa.backend.AbstractConfigurations;
import org.protempa.backend.Backend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.Configuration;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationRemoveException;
import org.protempa.backend.ConfigurationsSaveException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.ConfigurationsSupport;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.backend.InvalidPropertyValueException;

/**
 * Implements configurations for Protempa using INI files. A configuration id is
 * the name of an INI file that is in a directory specified in the constructor.
 *
 * @author Andrew Post
 */
public class INIConfigurations extends AbstractConfigurations {

    /**
     * The default directory for configurations (<code>.protempa-configs</code>
     * in the user's home directory).
     */
    public static final File DEFAULT_DIRECTORY
            = new File(FileUtils.getUserDirectory(), ".protempa-configs");
    /**
     * The name of the system property (
     * <code>protempa.inicommonsconfigurations.pathname</code>) for specifying
     * the configuration file directory.
     */
    public static final String DIRECTORY_SYSTEM_PROPERTY
            = "protempa.ini4jconfigurations.pathname";
    private File directory;

    /**
     * Creates an INI file configurations instance. Using this constructor is
     * equivalent to specifying a <code>pathname</code> of <code>null</code> in
     * the one-argument constructor.
     */
    public INIConfigurations() {
        this(null);
    }
    
    /**
     * Creates an INI file configurations instance that looks for backend
     * configurations in the specified directory. If <code>null</code>, the
     * default directory will be used ({@link #DEFAULT_DIRECTORY}).
     * @param pathname the directory containing backend configurations.
     */
    public INIConfigurations(File pathname) {
        this(null, pathname);
    }
    
    /**
     * Creates an INI file configurations instance that looks for configuration
     * files in the specified directory. If <code>null</code>, it first looks
     * for a system property,
     * <code>protempa.inicommonsconfigurations.pathname</code> for a pathname.
     * If unspecified, it looks for configuration files in the default location
     * (see {@link #DEFAULT_DIRECTORY}).
     *
     * @param backendProvider the provider for loading data source, knowledge
     * source, algorithm source etc. backends.
     * @param directory the directory containing backend configuration files.
     */
    public INIConfigurations(BackendProvider backendProvider, File directory) {
        super(backendProvider);
        if (directory != null) {
            this.directory = directory;
        } else {
            String pathnameFromSystemProperty
                    = System.getProperty(DIRECTORY_SYSTEM_PROPERTY);
            if (pathnameFromSystemProperty != null) {
                this.directory = new File(pathnameFromSystemProperty);
            }

        }
        if (this.directory == null) {
            this.directory = DEFAULT_DIRECTORY;
        }
        Logger logger = Ini4jUtil.logger();
        logger.log(Level.FINE, "Using configurations path {0}",
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
    public Configuration load(final String configurationId)
            throws ConfigurationsLoadException, ConfigurationsNotFoundException {
        if (configurationId == null) {
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsLoadException("Cannot create directory "
                    + this.directory);
        }
        
        final ConfigurationsSupport configurationsSupport = new ConfigurationsSupport(getBackendProvider());
        configurationsSupport.init(configurationId);

        IniParser parser = new IniParser();
        File file = new File(this.directory, configurationId);
        try (FileReader fr = new FileReader(file)) {
            final List<Exception> exceptions = new ArrayList<>();
            parser.parse(fr, new IniHandler() {
                private BackendInstanceSpec<? extends Backend> backendInstanceSpec;

                @Override
                public void endIni() {
                }

                @Override
                public void endSection() {
                }

                @Override
                public void handleComment(String string) {
                }

                @Override
                public void handleOption(String string, String string1) {
                    if (backendInstanceSpec != null) {
                        try {
                            if (string.endsWith(".required")) {
                                string = string.substring(0, string.length() - ".required".length());
                                if (!Boolean.parseBoolean(string1)) {
                                    throw new InvalidPropertyValueException(string, string1, null);
                                }
                                backendInstanceSpec.addRequiredOverride(string);
                            } else if (string.endsWith(".displayName")) {
                                string = string.substring(0, string.length() - ".displayName".length());
                                backendInstanceSpec.addDisplayNameOverride(string, string1);
                            } else {
                                backendInstanceSpec.parseProperty(string, string1);
                            }
                        } catch (InvalidPropertyNameException ex) {
                            Ini4jUtil.logger().log(Level.WARNING, 
                                    "Invalid property name {0} in configuration {1}", 
                                    new Object[]{string, configurationId});
                        } catch (InvalidPropertyValueException ex) {
                            exceptions.add(ex);
                        }
                    }
                }

                @Override
                public void startIni() {
                }

                @Override
                public void startSection(String string) {
                    try {
                        backendInstanceSpec = configurationsSupport.load(string);
                        backendInstanceSpec.setConfigurationsId(
                                configurationId);
                    } catch (InvalidConfigurationException |
                            ConfigurationsLoadException ex) {
                        exceptions.add(ex);
                    }
                }
            });
            if (!exceptions.isEmpty()) {
                throw new ConfigurationsLoadException(exceptions.get(0));
            }
            return configurationsSupport.buildConfiguration();
        } catch (FileNotFoundException ex) {
            throw new ConfigurationsNotFoundException(ex);
        } catch (IOException | SecurityException ex) {
            throw new ConfigurationsLoadException(ex);
        }
    }

    @Override
    public void save(Configuration configuration)
            throws ConfigurationsSaveException {
        if (configuration == null) {
            throw new IllegalArgumentException(
                    "configuration cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsSaveException("Cannot create directory "
                    + this.directory);
        }
        Wini ini = new Wini();
        ini.getConfig().setMultiSection(true);
        try {
            File configurationsPath
                    = new File(this.directory, configuration.getConfigurationId());
            for (BackendInstanceSpec backendInstanceSpec
                    : configuration.getAllSections()) {
                BackendSpec backendSpec = backendInstanceSpec.getBackendSpec();
                Section section = ini.add(backendSpec.getId());
                for (BackendPropertySpec spec : backendInstanceSpec.getBackendSpec().getPropertySpecs()) {
                    section.put(spec.getName(), backendInstanceSpec.getProperty(spec.getName()));
                }
            }

            ini.store(configurationsPath);
        } catch (IOException | SecurityException | InvalidPropertyNameException ex) {
            throw new ConfigurationsSaveException(ex);
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
        try {
            if (f.exists() && !f.delete()) {
                throw new ConfigurationRemoveException(
                        "Could not remove " + configurationId);
            }
        } catch (SecurityException ex) {
            throw new ConfigurationRemoveException(ex);
        }
    }

}
