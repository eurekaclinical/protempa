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
public class DelimitedFileDataSourceBackend
        extends AbstractFileDataSourceBackend {

    private static final DelimitedColumnSpec[] DEFAULT_DELIMITED_COLUMN_SPEC_ARR = new DelimitedColumnSpec[0];
    private static final PlainColumnSpec[] DEFAULT_PLAIN_COLUMN_SPEC_ARR = new PlainColumnSpec[0];
    private static final Character DEFAULT_DELIMITER = '\t';

    private DelimitedColumnSpec[] delimitedColumnSpecs;
    private Integer keyIdIndex;
    private Character delimiter;
    private PlainColumnSpec[] keyIdColumnSpecs;

    public DelimitedFileDataSourceBackend() {
        this.delimiter = DEFAULT_DELIMITER;
        this.keyIdIndex = -1;
        this.delimitedColumnSpecs = DEFAULT_DELIMITED_COLUMN_SPEC_ARR;
        this.keyIdColumnSpecs = DEFAULT_PLAIN_COLUMN_SPEC_ARR;
    }

    @Override
    public DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters,
            QueryResultsHandler queryResultsHandler)
            throws DataSourceReadException {
        File[] files = getFiles();
        DelimitedFileLineIterator[] result
                = new DelimitedFileLineIterator[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                result[i] = new DelimitedFileLineIterator(this, files[i], getDefaultPositionPerFile(files[i]));
            } catch (IOException ex) {
                throw new DataSourceReadException(ex);
            }
        }
        return new CloseableIteratorChain(result);
    }

    public DelimitedColumnSpec[] getDelimitedColumnSpecs() {
        return delimitedColumnSpecs.clone();
    }

    public void setDelimitedColumnSpecs(
            DelimitedColumnSpec[] delimitedColumnSpecs) {
        if (delimitedColumnSpecs != null) {
            this.delimitedColumnSpecs = delimitedColumnSpecs.clone();
        } else {
            this.delimitedColumnSpecs = DEFAULT_DELIMITED_COLUMN_SPEC_ARR;
        }
    }

    public Integer getKeyIdIndex() {
        return keyIdIndex;
    }

    @BackendProperty
    public void setKeyIdIndex(Integer keyIdIndex) {
        if (keyIdIndex == null) {
            this.keyIdIndex = 0;
        } else {
            this.keyIdIndex = keyIdIndex;
        }
    }

    public Character getDelimiter() {
        return delimiter;
    }

    @BackendProperty
    public void setDelimiter(Character delimiter) {
        if (delimiter != null) {
            this.delimiter = delimiter;
        } else {
            this.delimiter = DEFAULT_DELIMITER;
        }
    }

    @BackendProperty(propertyName = "columns")
    public void parseDelimitedColumnSpecs(String[] specStrings)
            throws IOException {
        if (specStrings != null) {
            DelimitedColumnSpec[] result = new DelimitedColumnSpec[specStrings.length];
            for (int i = 0; i < specStrings.length; i++) {
                String specString = specStrings[i];
                DelimitedColumnSpec spec = new DelimitedColumnSpec();
                spec.parseDescriptor(specString);
                result[i] = spec;
            }
            this.delimitedColumnSpecs = result;
        }
    }

    public PlainColumnSpec[] getKeyIdColumnSpecs() {
        return keyIdColumnSpecs.clone();
    }

    public void setKeyIdColumnSpecs(PlainColumnSpec[] keyIdColumnSpecs) {
        if (keyIdColumnSpecs != null) {
            this.keyIdColumnSpecs = keyIdColumnSpecs.clone();
        } else {
            this.keyIdColumnSpecs = DEFAULT_PLAIN_COLUMN_SPEC_ARR;
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
