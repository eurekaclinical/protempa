package org.protempa.test;

/*
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import java.io.Writer;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerFactory;
import org.protempa.query.handler.QueryResultsHandlerInitException;

/**
 *
 * @author Andrew Post
 */
public class SingleColumnQueryResultsHandlerFactory implements QueryResultsHandlerFactory {
    private final Writer writer;
    
    SingleColumnQueryResultsHandlerFactory(Writer writer) {
        this.writer = writer;
    }

    @Override
    public QueryResultsHandler getInstance() throws QueryResultsHandlerInitException {
        return new SingleColumnQueryResultsHandler(writer);
    }
    
}
