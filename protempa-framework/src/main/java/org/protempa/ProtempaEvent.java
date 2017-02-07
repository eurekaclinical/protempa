package org.protempa;

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

import java.util.Date;

/**
 *
 * @author Andrew Post
 */
public class ProtempaEvent {
    public static enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    };
    
    public static enum Type {
        DSB_QUERY_START,
        DSB_QUERY_STOP,
        DSB_QUERY_RESULT,
        QRH_STEP_START,
        QRH_STEP_STOP,
        QRH_STEP_RESULT
    }
    
    private final Level level;
    private final Type type;
    private final Class<?> component;
    private final Date timestamp;
    private final String description;

    public ProtempaEvent(Level level, Type type, Class<?> component, Date timestamp, String description) {
        this.level = level;
        this.type = type;
        this.component = component;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Level getLevel() {
        return level;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getComponent() {
        return component;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
    
}
