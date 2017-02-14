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
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractTabularWriter implements TabularWriter {

    private final TabularWriterValueVisitor valueVisitor;

    AbstractTabularWriter() {
        this.valueVisitor = new TabularWriterValueVisitor(this);
    }

    @Override
    public void writeNominal(NominalValue inValue) throws TabularWriterException {
        writeNominal(inValue, null);
    }

    @Override
    public void writeNumber(NumberValue inValue) throws TabularWriterException {
        writeNumber(inValue, null);
    }

    @Override
    public void writeInequality(InequalityNumberValue inValue) throws TabularWriterException {
        writeInequality(inValue, null);
    }

    @Override
    public void writeNumber(InequalityNumberValue inValue) throws TabularWriterException {
        writeNumber(inValue, null);
    }

    @Override
    public void writeDate(DateValue inValue) throws TabularWriterException {
        writeDate(inValue, null);
    }

    @Override
    public void writeBoolean(BooleanValue inValue) throws TabularWriterException {
        writeBoolean(inValue, null);
    }

    @Override
    public void writeStart(TemporalProposition inProposition) throws TabularWriterException {
        writeStart(inProposition, null);
    }

    @Override
    public void writeFinish(TemporalProposition inProposition) throws TabularWriterException {
        writeFinish(inProposition, null);
    }

    @Override
    public void writeLength(TemporalProposition inProposition) throws TabularWriterException {
        writeLength(inProposition, null);
    }

    @Override
    public void writeValue(Parameter inProposition) throws TabularWriterException {
        writeValue(inProposition, null);
    }

    @Override
    public void writePropertyValue(Proposition inProposition, String inPropertyName) throws TabularWriterException {
        writePropertyValue(inProposition, inPropertyName, null);
    }
    
    void write(Value inValue, Format inFormat) throws TabularWriterException {
        this.valueVisitor.setFormat(inFormat);
        inValue.accept(this.valueVisitor);
        TabularWriterException exception = this.valueVisitor.getException();
        this.valueVisitor.clear();
        if (exception != null) {
            throw exception;
        }
    }
}
