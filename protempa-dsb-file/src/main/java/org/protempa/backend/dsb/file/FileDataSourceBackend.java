package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import java.io.File;
import org.protempa.PropositionDefinition;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public interface FileDataSourceBackend extends DataSourceBackend {
    Long getDefaultPosition();

    void setDefaultPosition(Long defaultPosition);
    
    Granularity getDefaultGranularity();

    void setDefaultGranularity(Granularity defaultGranularity);

    void parseDefaultGranularity(String granularityString);

    String getKeyId();

    void setKeyId(String keyId);

    File[] getFiles();

    void setFiles(File[] files);

    void parseFiles(String[] pathStrings);

    int getSkipLines();

    void setSkipLines(Integer skipLines);

    Class<? extends PropositionDefinition> getRowPropositionType();

    void setRowPropositionType(Class<? extends PropositionDefinition> rowPropositionType);

    void parseRowPropositionType(String className) throws ClassNotFoundException;

    int[] getRowSpecs();

    void setRowSpecs(int[] rowSpecs);

    void parseRowSpecs(String[] specStrings) throws NumberFormatException;
}
