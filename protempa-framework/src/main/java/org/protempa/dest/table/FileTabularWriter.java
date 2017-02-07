package org.protempa.dest.table;

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
class FileTabularWriter extends AbstractTabularWriter {

    private final BufferedWriter writer;
    private int colIndex;
    private final char delimiter;
    
    FileTabularWriter(BufferedWriter inWriter, char inDelimiter) {
        this.writer = inWriter;
        this.delimiter = inDelimiter;
    }
    
    void writeString(String inValue) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue, this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeNominal(NominalValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue.format(inFormat), this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeNumber(NumberValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue.format(inFormat), this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeInequality(InequalityNumberValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        String comparatorString = inValue.getInequality().getComparatorString();
        StringUtil.escapeAndWriteDelimitedColumn(inFormat != null ? inFormat.format(comparatorString) : comparatorString, this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeNumber(InequalityNumberValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue.getNumberValue().format(inFormat), this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeDate(DateValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue.format(inFormat), this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeBoolean(BooleanValue inValue, Format inFormat) throws IOException {
        writeDelimiter();
        StringUtil.escapeAndWriteDelimitedColumn(inValue.format(inFormat), this.delimiter, this.writer);
        incr();
    }
    
    @Override
    public void writeId(Proposition inProposition) throws IOException {
        String value = inProposition.getId();
        writeString(value);
    }
    
    @Override
    public void writeUniqueId(Proposition inProposition) throws IOException {
        String value = inProposition.getUniqueId().getStringRepresentation();
        writeString(value);
    }
    
    @Override
    public void writeStart(TemporalProposition inProposition, Format inFormat) throws IOException {
        String value = inProposition.getStartFormattedShort();
        writeString(value);
    }
    
    @Override
    public void writeFinish(TemporalProposition inProposition, Format inFormat) throws IOException {
        String value = inProposition.getFinishFormattedShort();
        writeString(value);
    }
    
    @Override
    public void writeLength(TemporalProposition inProposition, Format inFormat) throws IOException {
        String value = inFormat != null ? inFormat.format(inProposition.getInterval().getMinLength()) : inProposition.getLengthFormattedShort();
        writeString(value);
    }
    
    @Override
    public void writeValue(Parameter inProposition, Format inFormat) throws IOException {
        Value value = inProposition.getValue();
        write(value, inFormat);
    }
    
    @Override
    public void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws IOException {
        Value value = inProposition.getProperty(inPropertyName);
        write(value, inFormat);
    }
    
    @Override
    public void newRow() throws IOException {
        this.writer.newLine();
        this.colIndex = 0;
    }
    
    @Override
    public void close() throws Exception {
        this.writer.close();
    }
    
    private void writeDelimiter() throws IOException {
        if (this.colIndex > 0) {
            this.writer.write(this.delimiter);
        }
    }
    
    private void incr() {
        this.colIndex++;
    }
    
}
