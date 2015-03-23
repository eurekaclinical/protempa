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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.backend.AbstractCommonsDataSourceBackend;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.dsb.DataValidationEvent;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractFileDataSourceBackend extends AbstractCommonsDataSourceBackend implements FileDataSourceBackend {
    private static final File[] EMPTY_FILE_ARRAY = new File[0];
    private File[] files;
    private int skipLines;
    private Class<? extends PropositionDefinition> rowPropositionType;
    private int[] rowSpecs;
    private String keyId;
    private Long defaultPosition;
    private Granularity defaultGranularity;

    protected AbstractFileDataSourceBackend() {
        this.files = EMPTY_FILE_ARRAY;
        this.rowSpecs = ArrayUtils.EMPTY_INT_ARRAY;
    }
    
    protected Long getDefaultPositionPerFile(File file) throws IOException {
        return getDefaultPosition();
    }

    @Override
    public Long getDefaultPosition() {
        return defaultPosition;
    }

    @Override
    public void setDefaultPosition(Long defaultPosition) {
        this.defaultPosition = defaultPosition;
    }
    
    @Override
    public Granularity getDefaultGranularity() {
        return defaultGranularity;
    }

    @Override
    public void setDefaultGranularity(Granularity defaultGranularity) {
        this.defaultGranularity = defaultGranularity;
    }

    @BackendProperty(propertyName = "defaultGranularity")
    @Override
    public void parseDefaultGranularity(String granularityString) {
        getGranularityFactory().toGranularity(granularityString);
    }

    @Override
    public String getKeyId() {
        return keyId;
    }

    @BackendProperty
    @Override
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public File[] getFiles() {
        return files;
    }

    @Override
    public void setFiles(File[] files) {
        if (files == null) {
            this.files = EMPTY_FILE_ARRAY;
        } else {
            this.files = files.clone();
        }
    }

    @BackendProperty(propertyName = "files")
    @Override
    public void parseFiles(String[] pathStrings) {
        File[] result = new File[pathStrings.length];
        int i = 0;
        for (String pathStr : pathStrings) {
            result[i++] = new File(pathStr);
        }
        this.files = result;
    }

    @Override
    public int getSkipLines() {
        return skipLines;
    }

    @BackendProperty
    @Override
    public void setSkipLines(Integer skipLines) {
        if (skipLines == null || skipLines.compareTo(0) < 0) {
            this.skipLines = 0;
        } else {
            this.skipLines = skipLines;
        }
    }

    @Override
    public Class<? extends PropositionDefinition> getRowPropositionType() {
        return rowPropositionType;
    }

    @Override
    public void setRowPropositionType(Class<? extends PropositionDefinition> rowPropositionType) {
        this.rowPropositionType = rowPropositionType;
    }

    @BackendProperty(propertyName = "rowPropositionType")
    @Override
    public void parseRowPropositionType(String className) throws ClassNotFoundException {
        this.rowPropositionType = (Class<? extends PropositionDefinition>) Class.forName(className);
    }

    @Override
    public int[] getRowSpecs() {
        return rowSpecs.clone();
    }

    @Override
    public void setRowSpecs(int[] rowSpecs) {
        if (rowSpecs != null) {
            this.rowSpecs = rowSpecs.clone();
        } else {
            this.rowSpecs = ArrayUtils.EMPTY_INT_ARRAY;
        }
    }

    @BackendProperty(propertyName = "rows")
    @Override
    public void parseRowSpecs(String[] specStrings) throws NumberFormatException {
        List<Integer> rows = new ArrayList<>();
        for (String specString : specStrings) {
            rows.add(Integer.valueOf(specString));
        }
        int[] rowsArr = new int[rows.size()];
        for (int i = 0, n = rows.size(); i < n; i++) {
            rowsArr[i] = rows.get(i);
        }
        this.rowSpecs = rowsArr;
    }

    @Override
    public DataValidationEvent[] validateData(KnowledgeSource knowledgeSource) throws DataSourceBackendFailedDataValidationException, KnowledgeSourceReadException {
        return new DataValidationEvent[0];
    }

    @Override
    public void validateConfiguration(KnowledgeSource knowledgeSource) throws DataSourceBackendFailedConfigurationValidationException, KnowledgeSourceReadException {
    }

}
