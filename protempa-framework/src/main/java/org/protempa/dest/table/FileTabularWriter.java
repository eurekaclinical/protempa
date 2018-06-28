package org.protempa.dest.table;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2017 Emory University
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.Format;
import org.arp.javautil.string.StringUtil;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class FileTabularWriter extends AbstractTabularWriter {

    private final BufferedWriter writer;
    private int colIndex;
    private final char delimiter;
    private final QuoteModel quoteModel;
    
    public FileTabularWriter(BufferedWriter inWriter, char inDelimiter) {
        this(inWriter, inDelimiter, null);
    }

    public FileTabularWriter(BufferedWriter inWriter, char inDelimiter,
            QuoteModel inQuoteModel) {
        this.writer = inWriter;
        this.delimiter = inDelimiter;
        if (inQuoteModel == null) {
            this.quoteModel = QuoteModel.WHEN_QUOTE_EMBEDDED;
        } else {
            this.quoteModel = inQuoteModel;
        }
    }
    
    @Override
    public void writeString(String inValue) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue);
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeNominal(NominalValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue.format(inFormat));
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeNumber(NumberValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue.format(inFormat));
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeInequality(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            String comparatorString = inValue.getInequality().getComparatorString();
            escapeAndWriteDelimitedColumn(inFormat != null ? inFormat.format(comparatorString) : comparatorString);
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue.getNumberValue().format(inFormat));
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeDate(DateValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue.format(inFormat));
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeBoolean(BooleanValue inValue, Format inFormat) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue.format(inFormat));
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void writeId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getId();
        writeString(value);
    }

    @Override
    public void writeUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getStringRepresentation();
        writeString(value);
    }

    @Override
    public void writeLocalUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getLocalUniqueId().getId();
        writeString(value);
    }
    
    @Override
    public void writeNumericalId(Proposition inProposition) throws TabularWriterException {
        String value = String.valueOf(inProposition.getUniqueId().getLocalUniqueId().getNumericalId());
        writeString(value);
    }

    @Override
    public void writeStart(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value = inProposition.getStartFormattedShort();
        writeString(value);
    }

    @Override
    public void writeFinish(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value = inProposition.getFinishFormattedShort();
        writeString(value);
    }

    @Override
    public void writeLength(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value = inFormat != null ? inFormat.format(inProposition.getInterval().getMinLength()) : inProposition.getLengthFormattedShort();
        writeString(value);
    }

    @Override
    public void writeValue(Parameter inProposition, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getValue();
        write(value, inFormat);
    }

    @Override
    public void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getProperty(inPropertyName);
        write(value, inFormat);
    }

    @Override
    public void writeNull() throws TabularWriterException {
        writeString(null);
    }

    @Override
    public void newRow() throws TabularWriterException {
        try {
            this.writer.newLine();
            this.colIndex = 0;
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public void close() throws TabularWriterException {
        try {
            this.writer.close();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    private void writeDelimiter() throws IOException {
        if (this.colIndex > 0) {
            this.writer.write(this.delimiter);
        }
    }
    
    private void escapeAndWriteDelimitedColumn(String inValue) throws IOException {
        if (this.quoteModel == QuoteModel.ALWAYS) {
            StringUtil.escapeAndWriteDelimitedColumn(inValue, this.delimiter, true, this.writer);
        } else {
            StringUtil.escapeAndWriteDelimitedColumn(inValue, this.delimiter, this.writer);
        }
    }

    private void incr() {
        this.colIndex++;
    }

}
