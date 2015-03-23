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
import java.util.Set;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEventIterator;
import org.protempa.QuerySession;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
@BackendInfo(displayName = "Fixed Width Flat File Data Source Backend")
public class FixedWidthFileDataSourceBackend extends AbstractFileDataSourceBackend {

    private static final FixedWidthColumnSpec[] DEFAULT_FIXED_WIDTH_COLUMN_SPEC_ARR = new FixedWidthColumnSpec[0];
    private static final PlainColumnSpec[] DEFAULT_PLAIN_COLUMN_SPEC_ARR = new PlainColumnSpec[0];

    private FixedWidthColumnSpec[] fixedWidthColumnSpecs;
    private int keyIdOffset;
    private int keyIdLength;
    private PlainColumnSpec[] keyIdColumnSpecs;

    public FixedWidthFileDataSourceBackend() {
        this.keyIdOffset = -1;
        this.fixedWidthColumnSpecs = DEFAULT_FIXED_WIDTH_COLUMN_SPEC_ARR;
        this.keyIdColumnSpecs = DEFAULT_PLAIN_COLUMN_SPEC_ARR;
    }

    @Override
    public DataStreamingEventIterator<Proposition> readPropositions(Set<String> keyIds, Set<String> propIds, Filter filters, QuerySession qs, QueryResultsHandler queryResultsHandler) throws DataSourceReadException {
        File[] files = getFiles();
        FixedWidthFileLineIterator[] result = new FixedWidthFileLineIterator[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                result[i] = new FixedWidthFileLineIterator(this, files[i], getDefaultPositionPerFile(files[i]));
            } catch (IOException ex) {
                throw new DataSourceReadException(ex);
            }
        }
        return new CloseableIteratorChain(result);
    }
    
    public FixedWidthColumnSpec[] getFixedWidthColumnSpecs() {
        return this.fixedWidthColumnSpecs.clone();
    }

    public void setFixedWidthColumnSpecs(FixedWidthColumnSpec[] fixedWidthColumnSpecs) {
        if (fixedWidthColumnSpecs != null) {
            this.fixedWidthColumnSpecs = fixedWidthColumnSpecs.clone();
        } else {
            this.fixedWidthColumnSpecs = DEFAULT_FIXED_WIDTH_COLUMN_SPEC_ARR;
        }
    }
    
    public PlainColumnSpec[] getKeyIdColumnSpecs() {
        return this.keyIdColumnSpecs.clone();
    }

    public void setKeyIdColumnSpecs(PlainColumnSpec[] keyIdColumnSpecs) {
        if (fixedWidthColumnSpecs != null) {
            this.keyIdColumnSpecs = keyIdColumnSpecs.clone();
        } else {
            this.keyIdColumnSpecs = DEFAULT_PLAIN_COLUMN_SPEC_ARR;
        }
    }

    public Integer getKeyIdOffset() {
        return keyIdOffset;
    }

    @BackendProperty
    public void setKeyIdOffset(Integer keyIdOffset) {
        if (keyIdOffset == null || keyIdOffset.compareTo(0) < 0) {
            this.keyIdOffset = 0;
        } else {
            this.keyIdOffset = keyIdOffset;
        }
    }

    public Integer getKeyIdLength() {
        return keyIdLength;
    }

    @BackendProperty
    public void setKeyIdLength(Integer keyIdLength) {
        if (keyIdLength == null || keyIdLength.compareTo(0) < 0) {
            this.keyIdLength = 0;
        } else {
            this.keyIdLength = keyIdLength;
        }
    }

    @BackendProperty(propertyName = "columns")
    public void parseFixedWidthColumnSpecs(String[] specStrings) throws IOException {
        if (specStrings != null) {
            FixedWidthColumnSpec[] result = new FixedWidthColumnSpec[specStrings.length];
            for (int i = 0; i < specStrings.length; i++) {
                String specString = specStrings[i];
                FixedWidthColumnSpec spec = new FixedWidthColumnSpec();
                spec.parseDescriptor(specString);
                result[i] = spec;
            }
            this.fixedWidthColumnSpecs = result;
        }
    }
    
    @BackendProperty(propertyName = "keyIdSpecs")
    public void parseKeyId(String[] specStrings) throws IOException {
        if (specStrings != null) {
            PlainColumnSpec[] result = new PlainColumnSpec[specStrings.length];
            for (int i = 0; i < specStrings.length; i++) {
                String specString = specStrings[i];
                PlainColumnSpec spec = new PlainColumnSpec();
                spec.parseDescriptor(specString);
                result[i] = spec;
            }
            this.keyIdColumnSpecs = result;
        }
    }

}
