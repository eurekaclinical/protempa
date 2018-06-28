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
import java.util.Map;
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
    public void writeString(String inValue) throws TabularWriterException {
        this.row.add(inValue);
        incr();
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
        write(null, null);
    }
    
    @Override
    public void newRow() throws TabularWriterException {
        try {
            this.recordHandler.insert(this.row);
        } catch (SQLException ex) {
            throw new TabularWriterException(ex);
        }
        this.colIndex = 0;
    }
    
    @Override
    public void close() throws TabularWriterException {
        try {
            this.recordHandler.close();
        } catch (SQLException ex) {
            throw new TabularWriterException(ex);
        }
    }
    
    private int incr() {
        return this.colIndex++;
    }
    
    private <E extends Object> E doReplace(E val, Map<E, E> replace) {
        if (replace != null) {
            if (replace.containsKey(val)) {
                return replace.get(val);
            } else {
                return val;
            }
        } else {
            return val;
        }
    }
    
}
