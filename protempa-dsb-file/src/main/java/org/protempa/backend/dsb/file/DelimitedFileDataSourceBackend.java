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
public class DelimitedFileDataSourceBackend 
        extends AbstractFileDataSourceBackend {
    private static final Character DEFAULT_DELIMITER = '\t';

    private DelimitedColumnSpec[] delimitedColumnSpecs;
    private Integer keyIdIndex;
    private Character delimiter;

    public DelimitedFileDataSourceBackend() {
        this.delimiter = DEFAULT_DELIMITER;
    }

    @Override
    public DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters, 
            QuerySession qs, QueryResultsHandler queryResultsHandler) 
            throws DataSourceReadException {
        File[] files = getFiles();
        DelimitedFileLineIterator[] result = 
                new DelimitedFileLineIterator[files.length];
        for (int i = 0; i < files.length; i++) {
            result[i] = new DelimitedFileLineIterator(files[i], getSkipLines(), 
                    this.keyIdIndex, this.delimiter, this.delimitedColumnSpecs, 
                    getRowSpecs(), getId());
        }
        return new CloseableIteratorChain(result);
    }

    public DelimitedColumnSpec[] getDelimitedColumnSpecs() {
        return delimitedColumnSpecs;
    }

    public void setDelimitedColumnSpecs(
            DelimitedColumnSpec[] delimitedColumnSpecs) {
        this.delimitedColumnSpecs = delimitedColumnSpecs;
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
        List<DelimitedColumnSpec> specs = new ArrayList<>();
        for (String specString : specStrings) {
            DelimitedColumnSpec spec = new DelimitedColumnSpec();
            spec.parseDescriptor(specString);
            specs.add(spec);
        }
        this.delimitedColumnSpecs = 
                specs.toArray(new DelimitedColumnSpec[specs.size()]);
    }

}
