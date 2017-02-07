package org.protempa.dest.table;

import java.io.IOException;
import java.text.Format;
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
    
    void write(Value inValue, Format inFormat) throws IOException {
        this.valueVisitor.setFormat(inFormat);
        inValue.accept(this.valueVisitor);
        IOException exception = this.valueVisitor.getException();
        this.valueVisitor.clear();
        if (exception != null) {
            throw exception;
        }
    }
}
