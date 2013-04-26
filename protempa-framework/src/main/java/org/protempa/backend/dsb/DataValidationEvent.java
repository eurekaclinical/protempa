/*
 * #%L
 * Eureka Services
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
package org.protempa.backend.dsb;

import java.net.URI;
import java.util.Date;

/**
 * Hold various validation messages that occur when verifying an input data
 * source.
 *
 * @author hrathod
 *
 */
public class DataValidationEvent {

    /**
     * The line number where the event occurred.
     */
    private Long line;
    /**
     * The type of event (usually the entity that caused the event)
     */
    private String type;
    /**
     * The text of the event.
     */
    private String message;
    /**
     * Whether the event is fatal.
     */
    private boolean fatal;
    
    private Date timestamp;
    
    private URI uri;

    public DataValidationEvent() {
        this.timestamp = new Date();
    }

    /**
     * @return the line
     */
    public Long getLine() {
        return this.line;
    }

    /**
     * @param inLine the line to set
     */
    public void setLine(Long inLine) {
        this.line = inLine;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param inType the type to set
     */
    public void setType(String inType) {
        this.type = inType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param inMessage the message to set
     */
    public void setMessage(String inMessage) {
        this.message = inMessage;
    }

    /**
     * @return the fatal
     */
    public boolean isFatal() {
        return this.fatal;
    }

    /**
     * @param inFatal the fatal to set
     */
    public void setFatal(boolean inFatal) {
        this.fatal = inFatal;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }
    
    
}
