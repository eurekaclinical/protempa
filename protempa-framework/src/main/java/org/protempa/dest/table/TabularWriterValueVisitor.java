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
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueList;
import org.protempa.proposition.value.ValueVisitor;

/**
 *
 * @author Andrew Post
 */
class TabularWriterValueVisitor implements ValueVisitor {
    
    private final AbstractTabularWriter tabularWriter;
    private TabularWriterException exception;
    private Format format;

    TabularWriterValueVisitor(AbstractTabularWriter tabularWriter) {
        this.tabularWriter = tabularWriter;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
    
    @Override
    public void visit(NominalValue nominalValue) {
        try {
            this.tabularWriter.writeNominal(nominalValue, this.format);
        } catch (TabularWriterException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(OrdinalValue ordinalValue) {
        throw new UnsupportedOperationException("ordinal values not supported");
    }

    @Override
    public void visit(BooleanValue booleanValue) {
        try {
            this.tabularWriter.writeBoolean(booleanValue, this.format);
        } catch (TabularWriterException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(ValueList<? extends Value> listValue) {
        throw new UnsupportedOperationException("list values not supported");
    }

    @Override
    public void visit(NumberValue numberValue) {
        try {
            this.tabularWriter.writeNumber(numberValue, this.format);
        } catch (TabularWriterException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(InequalityNumberValue inequalityNumberValue) {
        try {
            this.tabularWriter.writeInequalityNumber(inequalityNumberValue, this.format);
        } catch (TabularWriterException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(DateValue dateValue) {
        try {
            this.tabularWriter.writeDate(dateValue, this.format);
        } catch (TabularWriterException ex) {
            this.exception = ex;
        }
    }

    TabularWriterException getException() {
        return this.exception;
    }
    
    void clear() {
        this.format = null;
        this.exception = null;
    }
    
}