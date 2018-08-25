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
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, String> replacement;

    public FileTabularWriter(BufferedWriter inWriter, char inDelimiter) {
        this(inWriter, inDelimiter, null);
    }

    public FileTabularWriter(BufferedWriter inWriter, char inDelimiter,
            QuoteModel inQuoteModel) {
        this(inWriter, inDelimiter, inQuoteModel, null);
    }
    
    public FileTabularWriter(BufferedWriter inWriter, char inDelimiter,
            QuoteModel inQuoteModel, String nullValue) {
        this.writer = inWriter;
        this.delimiter = inDelimiter;
        if (inQuoteModel == null) {
            this.quoteModel = QuoteModel.WHEN_QUOTE_EMBEDDED;
        } else {
            this.quoteModel = inQuoteModel;
        }
        this.replacement = new HashMap<>();
        if (nullValue == null) {
            this.replacement.put(null, "NULL");
        } else {
            this.replacement.put(null, nullValue);
        }
    }

    @Override
    public final void writeNominal(NominalValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.format(inFormat) : null);
    }

    @Override
    public final void writeNumber(NumberValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.format(inFormat) : null);
    }

    @Override
    public final void writeInequality(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        String comparatorString = inValue != null ? inValue.getInequality().getComparatorString() : null;
        writeString(inFormat != null ? inFormat.format(comparatorString) : comparatorString);
    }

    @Override
    public final void writeNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.getNumberValue().format(inFormat) : null);
    }

    @Override
    public final void writeInequalityNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.format(inFormat) : null);
    }
    
    @Override
    public final void writeDate(DateValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.format(inFormat) : null);
    }

    @Override
    public final void writeBoolean(BooleanValue inValue, Format inFormat) throws TabularWriterException {
        writeString(inValue != null ? inValue.format(inFormat) : null);
    }

    @Override
    public final void writeId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getId();
        writeString(value);
    }

    @Override
    public final void writeUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getStringRepresentation();
        writeString(value);
    }

    @Override
    public final void writeLocalUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getLocalUniqueId().getId();
        writeString(value);
    }

    @Override
    public final void writeNumericalId(Proposition inProposition) throws TabularWriterException {
        String value = String.valueOf(inProposition.getUniqueId().getLocalUniqueId().getNumericalId());
        writeString(value);
    }

    @Override
    public final void writeStart(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getStartFormattedShort();
        } else {
            value = inProposition.formatStart(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeFinish(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getFinishFormattedShort();
        } else {
            value = inProposition.formatFinish(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeLength(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getLengthFormattedShort();
        } else {
            value = inProposition.formatLength(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeParameterValue(Parameter inProposition, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getValue();
        writeValue(value, inFormat);
    }

    @Override
    public final void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getProperty(inPropertyName);
        writeValue(value, inFormat);
    }

    @Override
    public final void writeNull() throws TabularWriterException {
        writeString(null);
    }

    @Override
    public final void newRow() throws TabularWriterException {
        try {
            this.writer.newLine();
            this.colIndex = 0;
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

    @Override
    public final void close() throws TabularWriterException {
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
            StringUtil.escapeAndWriteDelimitedColumn(inValue, this.delimiter, true, this.replacement, this.writer);
        } else {
            StringUtil.escapeAndWriteDelimitedColumn(inValue, this.delimiter, this.replacement, this.writer);
        }
    }

    private void incr() {
        this.colIndex++;
    }
    
    private void writeString(String inValue) throws TabularWriterException {
        try {
            writeDelimiter();
            escapeAndWriteDelimitedColumn(inValue);
            incr();
        } catch (IOException ex) {
            throw new TabularWriterException(ex);
        }
    }

}
