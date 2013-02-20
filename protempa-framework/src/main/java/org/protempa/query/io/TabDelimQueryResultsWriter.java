/*
 * #%L
 * Protempa Framework
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
package org.protempa.query.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.protempa.ProtempaException;
import org.protempa.QueryResults;
import org.protempa.QueryResults.QueryResultsEntry;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.visitor.AbstractPropositionCheckedVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 *
 * @author Andrew Post
 */
public class TabDelimQueryResultsWriter extends QueryResultsWriter {

    public TabDelimQueryResultsWriter(QueryResults queryResults, Writer out) {
        super(queryResults, out);
    }

    public TabDelimQueryResultsWriter(QueryResults queryResults,
            OutputStream out) {
        super(queryResults, out);
    }

    public TabDelimQueryResultsWriter(QueryResults queryResults,
            String fileName) throws IOException {
        super(queryResults, fileName);
    }

    public TabDelimQueryResultsWriter(QueryResults queryResults, File file)
            throws IOException {
        super(queryResults, file);
    }

    
    
    @Override
    public void writeAll() throws IOException {
        for (QueryResultsEntry qre : getQueryResults()) {
            try {
                new TabDelimWriterPropositionVisitor(qre.getKeyId(), this)
                        .visit(qre.getPropositions());
            } catch (TabDelimWriterProtempaException pe) {
                throw pe.getIOException();
            } catch (ProtempaException pe) {
                throw new AssertionError(pe);
            }
        }
    }

    private final static class TabDelimWriterProtempaException
            extends ProtempaException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2989876076560172439L;
		private IOException ioe;

        TabDelimWriterProtempaException(IOException cause) {
            this(null, cause);
        }

        TabDelimWriterProtempaException(String message, IOException cause) {
            super(message, cause);
            assert cause != null : "cause cannot be null";
            this.ioe = cause;
        }

        IOException getIOException() {
            return this.ioe;
        }

    }

    private final static class TabDelimWriterPropositionVisitor
            extends AbstractPropositionCheckedVisitor {
        private final String keyId;
        private final TabDelimQueryResultsWriter writer;
        TabDelimWriterPropositionVisitor(String keyId, 
                TabDelimQueryResultsWriter writer) {
            this.keyId = keyId;
            this.writer = writer;
        }

        @Override
        public void visit(AbstractParameter abstractParameter)
                throws TabDelimWriterProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(abstractParameter);
                doWriteValue(abstractParameter);
                doWriteTime(abstractParameter);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimWriterProtempaException(ioe);
            }
        }

        @Override
        public void visit(Event event)
                throws TabDelimWriterProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(event);
                this.writer.write('\t');
                doWriteTime(event);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimWriterProtempaException(ioe);
            }
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter)
                throws TabDelimWriterProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(primitiveParameter);
                doWriteValue(primitiveParameter);
                doWriteTime(primitiveParameter);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimWriterProtempaException(ioe);
            }
        }

        @Override
        public void visit(Constant constant)
                throws TabDelimWriterProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(constant);
                this.writer.write('\t');
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimWriterProtempaException(ioe);
            }
        }

        private void doWriteKeyId() throws IOException {
            this.writer.write(this.keyId);
            this.writer.write('\t');
        }

        private void doWritePropId(Proposition proposition) throws IOException {
            this.writer.write(proposition.getId());
            this.writer.write('\t');
        }

        private void doWriteValue(Parameter parameter) throws IOException {
            this.writer.write(parameter.getValueFormatted());
            this.writer.write('\t');
        }

        private void doWriteTime(TemporalProposition proposition) 
                throws IOException {
            this.writer.write(proposition.getStartFormattedShort());
            this.writer.write('\t');
            this.writer.write(proposition.getFinishFormattedShort());
        }
    }

}
