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

import java.io.IOException;
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
interface TabularWriter extends AutoCloseable {
    void writeNominal(NominalValue inValue, Format inFormat) throws IOException;
    
    void writeNumber(NumberValue inValue, Format inFormat) throws IOException;
    
    void writeInequality(InequalityNumberValue inValue, Format inFormat) throws IOException;
    
    void writeNumber(InequalityNumberValue inValue, Format inFormat) throws IOException;
    
    void writeDate(DateValue inValue, Format inFormat) throws IOException;
    
    void writeBoolean(BooleanValue inValue, Format inFormat) throws IOException;
    
    void writeId(Proposition inProposition) throws IOException;
    
    void writeUniqueId(Proposition inProposition) throws IOException;
    
    void writeStart(TemporalProposition inProposition, Format inFormat) throws IOException;
    
    void writeFinish(TemporalProposition inProposition, Format inFormat) throws IOException;
    
    void writeLength(TemporalProposition inProposition, Format inFormat) throws IOException;
    
    void writeValue(Parameter inProposition, Format inFormat) throws IOException;
    
    void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws IOException;
    
    void newRow() throws IOException;
}
