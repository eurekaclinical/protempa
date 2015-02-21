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
 * Implements configurations for Protempa using INI files. A configuration id is
 * the name of an INI file that is in a directory specified in the constructor.
 *
 * @author Andrew Post
 */
public class INIConfigurations implements Configurations {

    /**
     * The default directory for configurations (
     * <code>.protempa-configs</code> in the user's home directory).
     */
    public static final File DEFAULT_DIRECTORY =
            new File(FileUtils.getUserDirectory(), ".protempa-configs");
    /**
     * The name of the system property (
     * <code>protempa.inicommonsconfigurations.pathname</code>) for specifying
     * the configuration file directory.
     */
    public static final String DIRECTORY_SYSTEM_PROPERTY =
            "protempa.ini4jconfigurations.pathname";
    private File directory;

    /**
     * Creates an INI file configurations instance. Using this constructor is
     * equivalent to specifying a
     * <code>pathname</code> of
     * <code>null</code> in the one-argument constructor.
     */
    public INIConfigurations() {
        this(null);
    }

    /**
     * Creates an INI file configurations instance that looks for configuration
     * files in the specified directory. If
     * <code>null</code>, it first looks for a system property,
     * <code>protempa.inicommonsconfigurations.pathname</code> for a pathname.
     * If unspecified, it looks for configuration files in the default location
     * (see {@link #DEFAULT_FILE}).
     *
     * @param directory a directory.
     */
    public INIConfigurations(File directory) {
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
        Ini4jUtil.logger().log(Level.FINE, "Using configurations path {0}",
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
            final String configurationsId,
            final BackendSpec<B> backendSpec)
            throws ConfigurationsLoadException, ConfigurationsNotFoundException {
        if (configurationsId == null) {
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        }
        if (backendSpec == null) {
            throw new IllegalArgumentException(
                    "backendSpec cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsLoadException("Cannot create directory "
                    + this.directory);
        }

        IniParser parser = new IniParser();
        File file = new File(this.directory, configurationsId);
        try (FileReader fr = new FileReader(file)) {
            final List<BackendInstanceSpec<B>> results =
                    new ArrayList<>();
            final List<Exception> exceptions = new ArrayList<>();
            parser.parse(fr, new IniHandler() {
                private BackendInstanceSpec<B> backendInstanceSpec;

                @Override
                public void endIni() {
                }

                @Override
                public void endSection() {
                    if (backendInstanceSpec != null) {
                        results.add(backendInstanceSpec);
                    }
                }

                @Override
                public void handleComment(String string) {
                }

                @Override
                public void handleOption(String string, String string1) {
                    if (backendInstanceSpec != null) {
                        try {
                            backendInstanceSpec.parseProperty(string, string1);
                        } catch (InvalidPropertyNameException | InvalidPropertyValueException ex) {
                            exceptions.add(ex);
                        }
                    }
                }

                @Override
                public void startIni() {
                }

                @Override
                public void startSection(String string) {
                    if (backendSpec.getId().equals(string)) {
                        backendInstanceSpec =
                                backendSpec.newBackendInstanceSpec();
                        backendInstanceSpec.setConfigurationsId(
                                configurationsId);
                    } else {
                        backendInstanceSpec = null;
                    }
                }
            });
            if (!exceptions.isEmpty()) {
                throw new ConfigurationsLoadException(exceptions.get(0));
            }
            return results;
        } catch (FileNotFoundException ex) {
            throw new ConfigurationsNotFoundException(ex);
        } catch (IOException | SecurityException ex) {
            throw new ConfigurationsLoadException(ex);
        }
    }

    @Override
    public void save(String configurationId,
            List<BackendInstanceSpec> backendInstanceSpecs)
            throws ConfigurationsSaveException {
        if (configurationId == null) {
            throw new IllegalArgumentException(
                    "configurationId cannot be null");
        }
        if (!this.directory.exists() && !this.directory.mkdir()) {
            throw new ConfigurationsSaveException("Cannot create directory "
                    + this.directory);
        }
        Wini ini = new Wini();
        ini.getConfig().setMultiSection(true);
        try {
            File configurationsPath =
                    new File(this.directory, configurationId);
            for (BackendInstanceSpec backendInstanceSpec :
                    backendInstanceSpecs) {
                backendInstanceSpec.setConfigurationsId(configurationId);
                BackendSpec backendSpec = backendInstanceSpec.getBackendSpec();
                List<BackendPropertySpec> specs =
                        backendInstanceSpec.getBackendPropertySpecs();
                Section section = ini.add(backendSpec.getId());
                for (BackendPropertySpec spec : specs) {
                    section.put(spec.getName(), backendInstanceSpec.getProperty(spec.getName()));
                }
            }

            ini.store(configurationsPath);
        } catch (IOException | SecurityException | InvalidPropertyNameException ex) {
            throw new ConfigurationsSaveException(ex);
        }
    }

    /**
     * Loads the section names of the configuration with the specified name in
     * the configuration file directory.
     *
     * @param configurationId the INI file's name.
     * @return the ids of the sections of the file.
     *
     * @throws ConfigurationsLoadException if an error occurs reading the file.
     */
    @Override
    public List<String> loadConfigurationIds(String configurationId)
            throws ConfigurationsNotFoundException, ConfigurationsLoadException {
        if (configurationId == null) {
            throw new IllegalArgumentException(
                    "configurationId cannot be null");
        }
        IniParser parser = new IniParser();
        try (FileReader fr = new FileReader(new File(this.directory, configurationId))) {
            final List<String> results = new ArrayList<>();
            parser.parse(fr, new IniHandler() {
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
                }

                @Override
                public void startIni() {
                }

                @Override
                public void startSection(String string) {
                    results.add(string);
                }
            });
            return results;
        } catch (FileNotFoundException ex) {
            throw new ConfigurationsNotFoundException(ex);
        } catch (IOException | SecurityException ex) {
            throw new ConfigurationsLoadException(ex);
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
