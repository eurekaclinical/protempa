/*
 * #%L
 * Commons INI Backend Configurations
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
package org.protempa.bconfigs.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.protempa.backend.Backend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationRemoveException;
import org.protempa.backend.ConfigurationsSaveException;
import org.protempa.backend.Configurations;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.backend.InvalidPropertyValueException;

/**
 *
 * @author Andrew Post
 */
public class INICommonsConfigurations implements Configurations {

    private static final String DEFAULT_PATHNAME = 
            System.getProperty("user.home") + File.separator +
            ".protempa-configs" + File.separator;

    static {
        DefaultExpressionEngine engine = new DefaultExpressionEngine();
        engine.setPropertyDelimiter("|");
        HierarchicalINIConfiguration.setDefaultExpressionEngine(engine);
    }
    
    private String pathname;


    public INICommonsConfigurations() {
        this.pathname =
                System.getProperty(
                "protempa.inicommonsconfigurations.pathname");
        if (this.pathname != null && !this.pathname.endsWith(File.separator))
            this.pathname += File.separator;
        if (this.pathname == null)
            this.pathname = DEFAULT_PATHNAME;
        CommonsUtil.logger().log(Level.FINE, "Using configurations path {0}", 
                this.pathname);
    }

    @Override
    public <B extends Backend> List<BackendInstanceSpec<B>> load(
            String configurationsId,
            BackendSpec<B> backendSpec)
            throws ConfigurationsLoadException {
        if (configurationsId == null)
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        if (backendSpec == null)
            throw new IllegalArgumentException(
                    "backendSpec cannot be null");
        File dir = new File(this.pathname);
        if (!dir.exists() && !dir.mkdir())
            throw new ConfigurationsLoadException("Cannot create directory "
                    + this.pathname);
        
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            config.load(this.pathname + configurationsId);
            Set<String> sectionNames = config.getSections();
            List<BackendInstanceSpec<B>> results =
                    new ArrayList<BackendInstanceSpec<B>>();
            for (String sectionName : sectionNames) {
                String specId = sectionName.substring(0,
                        sectionName.lastIndexOf('_'));
                if (!specId.equals(backendSpec.getId()))
                    continue;
                Configuration section = config.subset(sectionName);
                BackendInstanceSpec<B> backendInstanceSpec =
                        backendSpec.newBackendInstanceSpec();
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
        }
    }

    @Override
    public void save(String configurationsId,
            List<BackendInstanceSpec> backendInstanceSpecs)
            throws ConfigurationsSaveException {
        if (configurationsId == null)
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        File dir = new File(this.pathname);
        if (!dir.exists() && !dir.mkdir()) {
            throw new ConfigurationsSaveException("Cannot create directory "
                    + this.pathname);
        }
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            File configurationsPath = new File(dir, configurationsId);
            int i = 0;
            for (BackendInstanceSpec backendInstanceSpec :
                backendInstanceSpecs) {
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

    @Override
    public List<String> loadConfigurationIds(String configurationsId)
            throws ConfigurationsLoadException {
        if (configurationsId == null)
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        File dir = new File(this.pathname);
        if (!dir.exists() && !dir.mkdir())
            throw new ConfigurationsLoadException("Cannot create directory "
                    + this.pathname);
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration();
        try {
            config.load(this.pathname + configurationsId);
            Set<String> sections = config.getSections();
            String[] result = new String[sections.size()];
            for (String section : sections) {
                int index =
                        Integer.parseInt(section.substring(
                        section.lastIndexOf('_') + 1));
                if (result[index] != null) {
                    throw new ConfigurationsLoadException(
                            "duplicate indices on sections");
                } else if (index > result.length - 1)
                    throw new ConfigurationsLoadException("index too high: "
                            + index);
                result[index] =
                        section.substring(0, section.lastIndexOf('_'));
            }
            return Arrays.asList(result);
        } catch (ConfigurationException ex) {
            throw new ConfigurationsLoadException(ex);
        }
    }

    @Override
    public void remove(String configurationsId) 
            throws ConfigurationRemoveException {
        if (configurationsId == null)
            throw new IllegalArgumentException(
                    "configurationsId cannot be null");
        File f = new File(this.pathname + configurationsId);
        if (f.exists() && !f.delete())
            throw new ConfigurationRemoveException(
                    "Could not remove " + configurationsId);
    }

}
