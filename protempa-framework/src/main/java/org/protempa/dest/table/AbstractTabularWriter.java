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
