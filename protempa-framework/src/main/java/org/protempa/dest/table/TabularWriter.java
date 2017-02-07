package org.protempa.dest.table;

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
