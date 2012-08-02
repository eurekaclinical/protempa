/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 Emory University
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.backend.dsb.relationaldb;

import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public class QueryResultsCacheException extends SQLException {

    private static final long serialVersionUID = 1L;

    public QueryResultsCacheException(String string, String string1, int i, Throwable thrwbl) {
        super(string, string1, i, thrwbl);
    }

    public QueryResultsCacheException(String string, String string1, Throwable thrwbl) {
        super(string, string1, thrwbl);
    }

    public QueryResultsCacheException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public QueryResultsCacheException(Throwable thrwbl) {
        super(thrwbl);
    }

    public QueryResultsCacheException() {
    }

    public QueryResultsCacheException(String string) {
        super(string);
    }

    public QueryResultsCacheException(String string, String string1) {
        super(string, string1);
    }

    public QueryResultsCacheException(String string, String string1, int i) {
        super(string, string1, i);
    }
    
    
}
