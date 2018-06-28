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

import java.text.Format;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;

/**
 *
 * @author Andrew Post
 */
public interface TabularWriter extends AutoCloseable {
    void writeString(String inValue) throws TabularWriterException;
    
    void writeNominal(NominalValue inValue) throws TabularWriterException;
    
    void writeNominal(NominalValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeNumber(NumberValue inValue) throws TabularWriterException;
    
    void writeNumber(NumberValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeInequality(InequalityNumberValue inValue) throws TabularWriterException;
    
    void writeInequality(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeNumber(InequalityNumberValue inValue) throws TabularWriterException;
    
    void writeNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeDate(DateValue inValue) throws TabularWriterException;
    
    void writeDate(DateValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeBoolean(BooleanValue inValue) throws TabularWriterException;
    
    void writeBoolean(BooleanValue inValue, Format inFormat) throws TabularWriterException;
    
    void writeId(Proposition inProposition) throws TabularWriterException;
    
    void writeUniqueId(Proposition inProposition) throws TabularWriterException;
    
    void writeLocalUniqueId(Proposition inProposition) throws TabularWriterException;
    
    void writeStart(TemporalProposition inProposition) throws TabularWriterException;
    
    void writeStart(TemporalProposition inProposition, Format inFormat) throws TabularWriterException;
    
    void writeFinish(TemporalProposition inProposition) throws TabularWriterException;
    
    void writeFinish(TemporalProposition inProposition, Format inFormat) throws TabularWriterException;
    
    void writeLength(TemporalProposition inProposition) throws TabularWriterException;
    
    void writeLength(TemporalProposition inProposition, Format inFormat) throws TabularWriterException;
    
    void writeValue(Parameter inProposition) throws TabularWriterException;
    
    void writeValue(Parameter inProposition, Format inFormat) throws TabularWriterException;
    
    void writePropertyValue(Proposition inProposition, String inPropertyName) throws TabularWriterException;
    
    void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws TabularWriterException;
    
    void writeNull() throws TabularWriterException;
    
    void newRow() throws TabularWriterException;

    @Override
    void close() throws TabularWriterException;
    
    
}
