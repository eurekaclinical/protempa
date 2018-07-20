package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.query.Query;

/**
 *
 * @author arpost
 */
public class AbstractThread extends Thread {

    private final Query query;
    private final MessageFormat logMessageFormat;
    private final Logger logger;
    
    AbstractThread(Query query, Logger logger, String name) {
        super(name);
        assert query != null : "query cannot be null";
        assert logger != null : "logger cannot be null";
        this.query = query;
        this.logMessageFormat = ProtempaUtil.getLogMessageFormat(this.query);
        this.logger = logger;
    }
    
    protected void log(Level level, String msg) {
        if (logger.isLoggable(level)) {
            logger.log(level, this.logMessageFormat.format(new Object[]{msg}));
        }
    }
    
    protected void log(Level level, String msg, Throwable throwable) {
        if (logger.isLoggable(level)) {
            logger.log(level, this.logMessageFormat.format(new Object[]{msg}), throwable);
        }
    }
    
    protected void log(Level level, String msg, Object param) {
        if (logger.isLoggable(level)) {
            logger.log(level, this.logMessageFormat.format(new Object[]{msg}), param);
        }
    }
    
    protected boolean isLoggable(Level level) {
        return this.logger.isLoggable(level);
    }

    protected Query getQuery() {
        return query;
    }
    
}
