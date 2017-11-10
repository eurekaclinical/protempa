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
import au.com.bytecode.opencsv.CSVParser;
import java.io.File;
import java.io.IOException;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
class DelimitedFileLineIterator extends AbstractFileLineIterator {

    private final int[] rowSpecs;
    private final DelimitedColumnSpec[] columnSpecs;
    private final String keyId;
    private final PlainColumnSpec[] keyIdColumnSpecs;
    private final int keyIdIndex;
    private final CSVParser csvParser;

    DelimitedFileLineIterator(DelimitedFileDataSourceBackend backend,
            File file, Long defaultPosition)
            throws DataSourceReadException {
        super(backend, file, defaultPosition, backend.getKeyId() != null);
        this.columnSpecs = backend.getDelimitedColumnSpecs();
        this.rowSpecs = backend.getRowSpecs();
        this.keyIdIndex = backend.getKeyIdIndex();
        this.csvParser = new CSVParser(backend.getDelimiter());
        this.keyId = backend.getKeyId();
        this.keyIdColumnSpecs = backend.getKeyIdColumnSpecs();
    }

    @Override
    protected DataStreamingEvent<Proposition> dataStreamingEvent()
            throws DataSourceReadException {
        try {
            String[] line = this.csvParser.parseLine(getCurrentLine());
            String kId = this.keyIdIndex > -1 ? line[this.keyIdIndex] : this.keyId;
            if (kId == null) {
                throw new DataSourceReadException("keyId was never set");
            }
            if (this.keyIdColumnSpecs != null) {
                for (PlainColumnSpec colSpec : this.keyIdColumnSpecs) {
                    parseLinks(kId, colSpec.getLinks(), this.keyId, -1);
                }
            }
            int colNum = 0;
            for (int i = 0; i < this.columnSpecs.length; i++) {
                if (this.rowSpecs.length == 0 || this.rowSpecs[i] == getLineNumber()) {
                    DelimitedColumnSpec colSpec = this.columnSpecs[i];
                    String column = line[colSpec.getIndex()].trim();
                    String links = colSpec.getLinks();
                    parseLinks(kId, links, column, colNum++);
                }
            }
            return new DataStreamingEvent<>(kId, getData());
        } catch (ArrayIndexOutOfBoundsException | IOException ex) {
            throw new DataSourceReadException(ex);
        }
    }

}
