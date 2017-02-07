package org.protempa.dest.table;

import java.io.IOException;
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
    
    private final TabularWriter tabularWriter;
    private IOException exception;
    private Format format;

    TabularWriterValueVisitor(TabularWriter tabularWriter) {
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
        } catch (IOException ex) {
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
        } catch (IOException ex) {
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
        } catch (IOException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(InequalityNumberValue inequalityNumberValue) {
        try {
            this.tabularWriter.writeInequality(inequalityNumberValue, this.format);
        } catch (IOException ex) {
            this.exception = ex;
        }
    }

    @Override
    public void visit(DateValue dateValue) {
        try {
            this.tabularWriter.writeDate(dateValue, this.format);
        } catch (IOException ex) {
            this.exception = ex;
        }
    }

    IOException getException() {
        return this.exception;
    }
    
    void clear() {
        this.format = null;
        this.exception = null;
    }
    
}