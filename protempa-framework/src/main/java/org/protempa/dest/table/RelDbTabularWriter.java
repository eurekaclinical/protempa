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
import java.sql.SQLException;
import java.text.Format;
import java.util.ArrayList;
import org.arp.javautil.sql.ConnectionSpec;
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
public class RelDbTabularWriter extends AbstractTabularWriter {

    private final RecordHandler<ArrayList<?>> recordHandler;
    private final ArrayList<Object> row;
    private int colIndex;

    public RelDbTabularWriter(ConnectionSpec inConnectionSpec, String inStatement) throws SQLException {
        this.recordHandler = new ListRecordHandler(inConnectionSpec, inStatement);
        this.row = new ArrayList<>();
    }

    @Override
    public void writeNominal(NominalValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getString());
        }
        incr();
    }

    @Override
    public void writeNumber(NumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getNumber());
        }
        incr();
    }

    @Override
    public void writeInequality(InequalityNumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inFormat.format(inValue.getComparator()));
        } else {
            this.row.add(inValue.getComparator().getComparatorString());
        }
        incr();
    }

    @Override
    public void writeNumber(InequalityNumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getNumber());
        }
        incr();
    }

    @Override
    public void writeInequalityNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getNumber());
        }
        
        incr();
    }

    @Override
    public void writeDate(DateValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getDate());
        }
        incr();
    }

    @Override
    public void writeBoolean(BooleanValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue.getBoolean());
        }
        incr();
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
        writeValue(null, null);
    }

    @Override
    public final void newRow() throws TabularWriterException {
        try {
            this.recordHandler.insert(this.row);
        } catch (SQLException ex) {
            throw new TabularWriterException(ex);
        }
        this.row.clear();
        this.colIndex = 0;
    }

    @Override
    public final void close() throws TabularWriterException {
        try {
            this.recordHandler.close();
        } catch (SQLException ex) {
            throw new TabularWriterException(ex);
        }
    }

    private int incr() {
        return this.colIndex++;
    }
    
    private void writeString(String inValue) throws TabularWriterException {
        this.row.add(inValue);
        incr();
    }

}
